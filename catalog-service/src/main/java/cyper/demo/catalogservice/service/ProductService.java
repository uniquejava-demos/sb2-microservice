package cyper.demo.catalogservice.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cyper.demo.catalogservice.dao.ProductRepository;
import cyper.demo.catalogservice.model.Product;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;

    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();

    }

    public Optional<Product> findProductByCode(String code) {
        return productRepository.findByCode(code);

    }

}