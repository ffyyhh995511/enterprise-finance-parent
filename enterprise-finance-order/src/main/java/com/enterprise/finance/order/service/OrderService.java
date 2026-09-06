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
import com.enterprise.finance.order.converter.OrderConverter;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
// @RequiredArgsConstructor：Lombok 注解，自动为被 final 修饰的成员变量生成带参构造函数。
// 从 Spring 4.3 开始，如果一个类只有一个构造函数，Spring 会自动通过该构造函数注入依赖，
// 因此不需要在字段上使用 @Autowired 或 @Resource，这种做法被称为“构造器注入”，
// 可以保证依赖的不可变性（Immutability）并避免空指针异常。
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final UserFeignClient userFeignClient;
    private final OrderConverter orderConverter;

    public OrderDTO getById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("order not found: " + id);
        }
        return orderConverter.doToDto(order);
    }

    public List<OrderDTO> listByUserId(Long userId) {
        return orderConverter.doToDtoList(orderMapper.selectList(new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId)
                        .orderByDesc(Order::getId)));
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

        log.info("create order success: {}", order.getId());

        R<UserDTO> deductResult = userFeignClient.deduct(request.getUserId(), request.getAmount());
        if (!deductResult.isSuccess()) {
            throw new BusinessException(deductResult.getMessage());
        }
        if (Boolean.TRUE.equals(request.getRollback())) {
            throw new BusinessException("manual rollback for seata demo");
        }
        return orderConverter.doToDto(order);
    }
}
