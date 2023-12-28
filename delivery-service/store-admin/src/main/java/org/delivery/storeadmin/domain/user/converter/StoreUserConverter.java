package org.delivery.storeadmin.domain.user.converter;

import lombok.RequiredArgsConstructor;
import org.delivery.db.store.StoreEntity;
import org.delivery.db.storeUser.StoreUserEntity;
import org.delivery.storeadmin.domain.user.controller.model.StoreUserRegisterRequest;
import org.delivery.storeadmin.domain.user.controller.model.StoreUserResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreUserConverter {

    public StoreUserEntity toEntity(
            StoreUserRegisterRequest request,
            StoreEntity storeEntity
    ){
        return StoreUserEntity.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .storeId(storeEntity.getId())
                .build();
    }

    public StoreUserResponse toResponse(
            StoreUserEntity storeUserEntity,
            StoreEntity storeEntity
    ){
        return StoreUserResponse.builder()
                .user(StoreUserResponse.UserResponse.builder()
                        .id(storeUserEntity.getId())
                        .role(storeUserEntity.getRole())
                        .email(storeUserEntity.getEmail())
                        .status(storeUserEntity.getStatus())
                        .lastLoginAt(storeUserEntity.getLastLoginAt())
                        .registeredAt(storeUserEntity.getRegisteredAt())
                        .unregisteredAt(storeUserEntity.getUnregisteredAt())
                        .build()
                )
                .store(StoreUserResponse.StoreResponse.builder()
                        .id(storeEntity.getId())
                        .name(storeEntity.getName())
                        .build()
                )
                .build();
    }
}