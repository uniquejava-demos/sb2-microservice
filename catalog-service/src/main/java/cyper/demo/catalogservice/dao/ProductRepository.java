package cyper.demo.catalogservice.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cyper.demo.catalogservice.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByCode(String code);

}