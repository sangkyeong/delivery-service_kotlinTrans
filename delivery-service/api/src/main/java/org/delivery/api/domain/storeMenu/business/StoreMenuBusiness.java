package org.delivery.api.domain.storeMenu.business;

import lombok.RequiredArgsConstructor;
import org.delivery.api.domain.store.service.StoreService;
import org.delivery.api.domain.storeMenu.controller.model.StoreMenuRegisterRequest;
import org.delivery.api.domain.storeMenu.controller.model.StoreMenuResponse;
import org.delivery.api.domain.storeMenu.converter.StoreMenuConverter;
import org.delivery.api.domain.storeMenu.service.StoreMenuService;
import org.delivery.common.annotation.Business;

import java.util.List;
import java.util.stream.Collectors;

@Business
@RequiredArgsConstructor
public class StoreMenuBusiness {

    private final StoreMenuService storeMenuService;

    private final StoreMenuConverter storeMenuConverter;

    private final StoreService storeService;

    public StoreMenuResponse register(
            StoreMenuRegisterRequest request
    ){
        //req -> entity -> save -> response
        var storeEntity = storeService.getStoreWithThrow(request.getStoreId());

        var entity = storeMenuConverter.toEntity(request, storeEntity);
        var newEntity = storeMenuService.register(entity);
        var response = storeMenuConverter.toResponse(newEntity);
        return response;
    }

    public List<StoreMenuResponse> search(
            Long storeId
    ){
        var list = storeMenuService.getStoreMenuByStoreId(storeId);
        return list.stream()
                .map(it-> {
                    return storeMenuConverter.toResponse(it);
                })
                //= 간결하게 .map(storeMenuConverter::toResponse)
                .collect(Collectors.toList());
    }
}
