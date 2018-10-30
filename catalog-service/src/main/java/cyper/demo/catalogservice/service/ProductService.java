package cyper.demo.catalogservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cyper.demo.catalogservice.dao.ProductRepository;
import cyper.demo.catalogservice.model.Product;
import cyper.demo.catalogservice.model.ProductInventoryResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ProductService {

	private final ProductRepository productRepository;
	private final InventoryServiceClient inventoryServiceClient;

	@Autowired
	public ProductService(ProductRepository productRepository, InventoryServiceClient inventoryServiceClient) {
		this.productRepository = productRepository;
		this.inventoryServiceClient = inventoryServiceClient;
	}

	public List<Product> findAllProducts() {
		return productRepository.findAll();

	}

	public Optional<Product> findProductByCode(String code) {
		Optional<Product> productOptional = productRepository.findByCode(code);
		if (productOptional.isPresent()) {
			log.info("Fetching inventory level for product_code: " + code);
			Optional<ProductInventoryResponse> data = inventoryServiceClient.getProductInventoryByCode(code);
			if (data.isPresent()) {
				Integer quantity = data.get().getAvailableQuantity();
				log.info("Available quantity:" + quantity);
				productOptional.get().setInStock(quantity > 0);
			}
		}

		return productOptional;
	}

}
