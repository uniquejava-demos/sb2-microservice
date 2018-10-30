package cyper.demo.inventoryservice.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import cyper.demo.inventoryservice.dao.InventoryItemRepository;
import cyper.demo.inventoryservice.model.InventoryItem;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class InventoryController {

	@Autowired
	private InventoryItemRepository inventoryItemRepository;

	@GetMapping("/api/inventory/{productCode}")
	public ResponseEntity<InventoryItem> findInventoryByProductCode(@PathVariable("productCode") String productCode,
			HttpServletRequest request) {
		log.info("2. should get header set from zuul filter: " + request.getHeader("AUTH_HEADER"));

		Optional<InventoryItem> inventoryItem = inventoryItemRepository.findByProductCode(productCode);
		if (inventoryItem.isPresent()) {
			return new ResponseEntity<InventoryItem>(inventoryItem.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<InventoryItem>(HttpStatus.NOT_FOUND);
		}

	}

	@GetMapping("/api/inventory")
	public List<InventoryItem> getInventory() {
		log.info("Finding inventory for all products");
		return inventoryItemRepository.findAll();
	}

}
