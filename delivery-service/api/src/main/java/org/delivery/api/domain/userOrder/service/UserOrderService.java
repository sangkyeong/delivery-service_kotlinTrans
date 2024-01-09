package org.delivery.api.domain.userOrder.service;

import lombok.RequiredArgsConstructor;
import org.delivery.common.error.ErrorCode;
import org.delivery.common.exception.ApiException;
import org.delivery.db.userOrder.UserOrderEntity;
import org.delivery.db.userOrder.UserOrderRepository;
import org.delivery.db.userOrder.enums.UserOrderStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserOrderService {

    private final UserOrderRepository userOrderRepository;

    public UserOrderEntity getUserOrderWithOutStatusWithThrow(
            Long id,
            Long userId
    ){
        return Optional.ofNullable(userOrderRepository.findAllByIdAndUserId(id, userId)
        ).orElseThrow(() -> new ApiException(ErrorCode.NULL_POINT));
    }

    public UserOrderEntity getUserOrderWithThrow(
            Long id,
            Long userId
    ){
        return Optional.ofNullable(userOrderRepository.findAllByIdAndStatusAndUserId(id, UserOrderStatus.REGISTERED, userId)
        ).orElseThrow(() -> new ApiException(ErrorCode.NULL_POINT));
    }

    public List<UserOrderEntity> getUserOrderList(
            Long userId
        ){
        return userOrderRepository.findAllByUserIdAndStatusOrderByIdDesc(userId, UserOrderStatus.REGISTERED);
    }

    public List<UserOrderEntity> getUserOrderList(
            Long userId,
            List<UserOrderStatus> status
    ){
        return userOrderRepository.findAllByUserIdAndStatusInOrderByIdDesc(userId, status);
    }

    //현재 진행중인 내역
    public List<UserOrderEntity> current(Long userId){
        return getUserOrderList(userId,
                List.of(UserOrderStatus.ORDER,
                        UserOrderStatus.COOKING,
                        UserOrderStatus.DELIVERY,
                        UserOrderStatus.ACCEPT)
        );
    }

    //과거 주문한 내역
    public List<UserOrderEntity> history(Long userId){
        return getUserOrderList(userId,
                List.of(UserOrderStatus.RECEIVE)
        );
    }

    //주문
    public UserOrderEntity order(
            UserOrderEntity userOrderEntity
    ){
        return Optional.ofNullable(userOrderEntity)
                .map(it->{
                    it.setStatus(UserOrderStatus.ORDER);
                    it.setOrderedAt(LocalDateTime.now());
                    return userOrderRepository.save(it);
                }).orElseThrow(() -> new ApiException(ErrorCode.NULL_POINT));
    }

    //상태변경
    public UserOrderEntity setStatus(UserOrderEntity userOrderEntity, UserOrderStatus status){
        userOrderEntity.setStatus(status);
        return userOrderRepository.save(userOrderEntity);
    }

    //주문확인
    public UserOrderEntity accept(UserOrderEntity userOrderEntity){
        userOrderEntity.setAcceptedAt(LocalDateTime.now());
        return setStatus(userOrderEntity,UserOrderStatus.ACCEPT);
    }
    
    //조리시작
    public UserOrderEntity cooking(UserOrderEntity userOrderEntity){
        userOrderEntity.setAcceptedAt(LocalDateTime.now());
        return setStatus(userOrderEntity,UserOrderStatus.COOKING);
    }
    
    //배달시작
    public UserOrderEntity delivery(UserOrderEntity userOrderEntity){
        userOrderEntity.setAcceptedAt(LocalDateTime.now());
        return setStatus(userOrderEntity,UserOrderStatus.DELIVERY);
    }
    
    //배달완료
    public UserOrderEntity receive(UserOrderEntity userOrderEntity){
        userOrderEntity.setAcceptedAt(LocalDateTime.now());
        return setStatus(userOrderEntity,UserOrderStatus.RECEIVE);
    }
}
