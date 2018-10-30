package cyper.demo.inventoryservice.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cyper.demo.inventoryservice.model.InventoryItem;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
	Optional<InventoryItem> findByProductCode(String productCode);
}
