package cyper.demo.catalogservice.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cyper.demo.catalogservice.exception.ProductNotFoundException;
import cyper.demo.catalogservice.model.Product;
import cyper.demo.catalogservice.service.ProductService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {

	private final ProductService productService;

	@Autowired

	public ProductController(ProductService productService) {
		this.productService = productService;

	}

	@GetMapping("")

	public List<Product> allProducts() {

		return productService.findAllProducts();

	}

	@GetMapping("/{code}")
	public Product productByCode(@PathVariable String code, HttpServletRequest request) {
		log.info("1. should get header set from zuul filter: " + request.getHeader("AUTH_HEADER"));
		return productService.findProductByCode(code)
				.orElseThrow(() -> new ProductNotFoundException("Product with code [" + code + "] doesn't exist"));

	}

}