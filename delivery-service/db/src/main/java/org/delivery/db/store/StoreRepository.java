package org.delivery.db.store;

import org.delivery.db.store.enums.StoreCategory;
import org.delivery.db.store.enums.StoreStatus;
import org.delivery.db.storeUser.StoreUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface StoreRepository extends JpaRepository<StoreEntity, Long> {

    Optional<StoreEntity> findFirstByIdAndStatusOrderByIdDesc(Long id, String status);

    List<StoreEntity> findAllByStatusOrderByIdDesc(StoreStatus status);

    List<StoreEntity> findAllByStatusAndCategoryOrderByStatusDesc(StoreStatus status, StoreCategory storeCategory);

    Optional<StoreEntity> findFirstByNameAndStatusOrderByIdDesc(String name, StoreStatus status);

}
