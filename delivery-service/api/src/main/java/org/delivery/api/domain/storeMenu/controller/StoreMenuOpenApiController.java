package org.delivery.api.domain.storeMenu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.delivery.api.domain.storeMenu.business.StoreMenuBusiness;
import org.delivery.api.domain.storeMenu.controller.model.StoreMenuRegisterRequest;
import org.delivery.api.domain.storeMenu.controller.model.StoreMenuResponse;
import org.delivery.common.api.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open-api/store-menu")
@RequiredArgsConstructor
public class StoreMenuOpenApiController {

    private final StoreMenuBusiness storeMenuBusiness;

    @PostMapping("/register")
    public Api<StoreMenuResponse> register(
            @Valid
            @RequestBody
            Api<StoreMenuRegisterRequest> request
    ){
        var req = request.getBody();
        var response = storeMenuBusiness.register(req);
        return Api.ok(response);
    }
}
