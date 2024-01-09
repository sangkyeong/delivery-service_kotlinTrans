package org.delivery.api.domain.userOrder.business;

import lombok.RequiredArgsConstructor;
import org.delivery.api.domain.store.converter.StoreConverter;
import org.delivery.api.domain.store.service.StoreService;
import org.delivery.api.domain.storeMenu.converter.StoreMenuConverter;
import org.delivery.api.domain.storeMenu.service.StoreMenuService;
import org.delivery.api.domain.user.model.User;
import org.delivery.api.domain.userOrder.controller.model.UserOrderDetailResponse;
import org.delivery.api.domain.userOrder.controller.model.UserOrderRequest;
import org.delivery.api.domain.userOrder.controller.model.UserOrderResponse;
import org.delivery.api.domain.userOrder.converter.UserOrderConverter;
import org.delivery.api.domain.userOrder.producer.UserOrderProducer;
import org.delivery.api.domain.userOrder.service.UserOrderService;
import org.delivery.api.domain.userOrderMenu.converter.UserOrderMenuConverter;
import org.delivery.api.domain.userOrderMenu.service.UserOrderMenuService;
import org.delivery.common.annotation.Business;

import java.util.List;
import java.util.stream.Collectors;

@Business
@RequiredArgsConstructor
public class UserOrderBusiness {

    private  final UserOrderService userOrderService;
    private final StoreMenuService storeMenuService;
    private final UserOrderConverter userOrderConverter;
    private final UserOrderMenuConverter userOrderMenuConverter;
    private final UserOrderMenuService userOrderMenuService;
    private final StoreService storeService;
    private final StoreMenuConverter storeMenuConverter;
    private final StoreConverter storeConverter;
    private final UserOrderProducer userOrderProducer;

    //1. 사용자, 메뉴 id
    //2. userOrder 생성
    //3. userOrderMenu 생성
    //4. 응답 생성
    public UserOrderResponse userOrder(User user, UserOrderRequest body) {

        var storeEntity = storeService.getStoreWithThrow(body.getStoreId());

        var storeMenuEntityList = body.getStoreMenuIdList().stream()
                .map(it -> storeMenuService.getStoreMenuWithThrow(it))
                .collect(Collectors.toList());

        var userOrderEntity = userOrderConverter.toEntity(user, storeEntity, storeMenuEntityList);

        //주문
        var newUserOrderEntity = userOrderService.order(userOrderEntity);

        //맵핑
        var userOrderMenuEntityList = storeMenuEntityList.stream()
                .map(it -> {
                    //menu + user order
                    var userOrderMenuEntity = userOrderMenuConverter.toEntity(newUserOrderEntity, it);
                    return userOrderMenuEntity;
                }).collect(Collectors.toList());

        //주문내역 기록 남기기
        userOrderMenuEntityList.forEach(it -> {
            userOrderMenuService.order(it);
        });
        
        //비동기로 가맹점에 주문알리기
        userOrderProducer.sendOrder(newUserOrderEntity);


        //response
        return userOrderConverter.toResponse(newUserOrderEntity);
    }

    public List<UserOrderDetailResponse> current(User user) {
        var userOrderEntityList = userOrderService.current(user.getId());

        //주문1건씩 처리
        var userOrderDtailResponseList = userOrderEntityList.stream().map(it->{

            //사용자가 주문한 메뉴
            var userOrderMenuEntityList = userOrderMenuService.getUserOrderMenu(it.getId());
            var storeMenuEntityList = userOrderMenuEntityList.stream().map(
                    userOrderMenuEntity->{
                        var storeMenuEntity = storeMenuService.getStoreMenuWithThrow(userOrderMenuEntity.getStoreMenu().getId());
                        return storeMenuEntity;
                    }).collect(Collectors.toList());

            //사용자가 주문한 스토어
            var storeEntity = storeService.getStoreWithThrow(storeMenuEntityList.stream().findFirst().get().getStore().getId());
            return UserOrderDetailResponse.builder()
                    .userOrderResponse(userOrderConverter.toResponse(it))
                    .storeMenuResponseList(storeMenuConverter.toResponse(storeMenuEntityList))
                    .storeResponse(storeConverter.toResponse(storeEntity))
                    .build();
        }).collect(Collectors.toList());
        return userOrderDtailResponseList;
    }

    public List<UserOrderDetailResponse> history(User user) {
        var userOrderEntityList = userOrderService.history(user.getId());

        //주문1건씩 처리
        var userOrderDtailResponseList = userOrderEntityList.stream().map(it->{

            //사용자가 주문한 메뉴
            var userOrderMenuEntityList = userOrderMenuService.getUserOrderMenu(it.getId());
            var storeMenuEntityList = userOrderMenuEntityList.stream().map(
                    userOrderMenuEntity->{
                        var storeMenuEntity = storeMenuService.getStoreMenuWithThrow(userOrderMenuEntity.getStoreMenu().getId());
                        return storeMenuEntity;
                    }).collect(Collectors.toList());

            //사용자가 주문한 스토어
            var storeEntity = storeService.getStoreWithThrow(storeMenuEntityList.stream().findFirst().get().getStore().getId());
            return UserOrderDetailResponse.builder()
                    .userOrderResponse(userOrderConverter.toResponse(it))
                    .storeMenuResponseList(storeMenuConverter.toResponse(storeMenuEntityList))
                    .storeResponse(storeConverter.toResponse(storeEntity))
                    .build();
        }).collect(Collectors.toList());
        return userOrderDtailResponseList;

    }

    public UserOrderDetailResponse read(User user, Long orderId) {
        var userOrderEntity = userOrderService.getUserOrderWithOutStatusWithThrow(orderId, user.getId());

        //사용자가 주문한 메뉴
        var userOrderMenuEntityList = userOrderMenuService.getUserOrderMenu(userOrderEntity.getId());
        var storeMenuEntityList = userOrderMenuEntityList.stream().map(
                userOrderMenuEntity->{
                    var storeMenuEntity = storeMenuService.getStoreMenuWithThrow(userOrderMenuEntity.getStoreMenu().getId());
                    return storeMenuEntity;
                }).collect(Collectors.toList());

        //사용자가 주문한 스토어
        var storeEntity = storeService.getStoreWithThrow(storeMenuEntityList.stream().findFirst().get().getStore().getId());

        return UserOrderDetailResponse.builder()
                .userOrderResponse(userOrderConverter.toResponse(userOrderEntity))
                .storeMenuResponseList(storeMenuConverter.toResponse(storeMenuEntityList))
                .storeResponse(storeConverter.toResponse(storeEntity))
                .build();
    }
}
