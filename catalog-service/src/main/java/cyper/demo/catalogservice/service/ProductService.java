package cyper.demo.catalogservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import cyper.demo.catalogservice.dao.ProductRepository;
import cyper.demo.catalogservice.model.Product;
import cyper.demo.catalogservice.model.ProductInventoryResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ProductService {

	private final ProductRepository productRepository;
	private final RestTemplate restTemplate;

	@Autowired
	public ProductService(ProductRepository productRepository, RestTemplate restTemplate) {
		this.productRepository = productRepository;
		this.restTemplate = restTemplate;
	}

	public List<Product> findAllProducts() {
		return productRepository.findAll();

	}

	public Optional<Product> findProductByCode(String code) {
		Optional<Product> productOptional = productRepository.findByCode(code);
		if (productOptional.isPresent()) {
			log.info("Fetching inventory level for product_code: " + code);
			ResponseEntity<ProductInventoryResponse> itemResEntity = restTemplate.getForEntity(
					"http://inventory-service/api/inventory/{code}", ProductInventoryResponse.class, code);
			if (itemResEntity.getStatusCode() == HttpStatus.OK) {
				Integer quantity = itemResEntity.getBody().getAvailableQuantity();
				log.info("Available quantity:" + quantity);
				productOptional.get().setInStock(quantity > 0);
			} else {
				log.error("Unable to get inventory level for product_code: " + code + ", Statuscode: "
						+ itemResEntity.getStatusCode());
			}
		}

		return productOptional;
	}

}
