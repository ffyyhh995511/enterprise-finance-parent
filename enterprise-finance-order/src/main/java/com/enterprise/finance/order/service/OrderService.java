package com.enterprise.finance.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.enterprise.finance.common.dto.CreateOrderRequest;
import com.enterprise.finance.common.dto.OrderDTO;
import com.enterprise.finance.common.dto.UserDTO;
import com.enterprise.finance.common.exception.BusinessException;
import com.enterprise.finance.common.feign.UserFeignClient;
import com.enterprise.finance.common.result.R;
import com.enterprise.finance.order.entity.Order;
import com.enterprise.finance.order.mapper.OrderMapper;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final UserFeignClient userFeignClient;

    public OrderDTO getById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("order not found: " + id);
        }
        return toDto(order);
    }

    public List<OrderDTO> listByUserId(Long userId) {
        return orderMapper.selectList(new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId)
                        .orderByDesc(Order::getId))
                .stream()
                .map(this::toDto)
                .toList();
    }

    public UserDTO getUser(Long userId) {
        R<UserDTO> result = userFeignClient.getById(userId);
        if (!result.isSuccess()) {
            throw new BusinessException(result.getMessage());
        }
        return result.getData();
    }

    @GlobalTransactional(name = "create-order", rollbackFor = Exception.class)
    public OrderDTO create(CreateOrderRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setAmount(request.getAmount());
        order.setStatus("CREATED");
        orderMapper.insert(order);

        R<UserDTO> deductResult = userFeignClient.deduct(request.getUserId(), request.getAmount());
        if (!deductResult.isSuccess()) {
            throw new BusinessException(deductResult.getMessage());
        }
        if (Boolean.TRUE.equals(request.getRollback())) {
            throw new BusinessException("manual rollback for seata demo");
        }
        return toDto(order);
    }

    private OrderDTO toDto(Order order) {
        return new OrderDTO(order.getId(), order.getUserId(), order.getAmount(), order.getStatus());
    }
}
