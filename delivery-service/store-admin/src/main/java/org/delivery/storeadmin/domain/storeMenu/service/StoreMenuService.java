package org.delivery.storeadmin.domain.storeMenu.service;

import lombok.RequiredArgsConstructor;
import org.delivery.db.storemenu.StoreMenuRepository;
import org.delivery.db.storemenu.enums.StoreMenuStatus;
import org.delivery.db.storemenu.storeMenuEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreMenuService {

    private final StoreMenuRepository storeMenuRepository;

    public storeMenuEntity getStoreMenuWithThrow(Long id){
        return Optional.ofNullable(storeMenuRepository.findFirstByIdAndStatusOrderByIdDesc(id, StoreMenuStatus.REGISTERED)
        ).orElseThrow(() -> new RuntimeException("store menu not found"));
    }
}
