package com.enterprise.finance.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.enterprise.finance.common.dto.TxDemoDTO;
import com.enterprise.finance.order.api.dto.CreateOrderRequest;
import com.enterprise.finance.order.api.dto.OrderDTO;
import com.enterprise.finance.user.api.dto.UserDTO;
import com.enterprise.finance.common.exception.BusinessException;
import com.enterprise.finance.user.api.feign.UserFeignClient;
import com.enterprise.finance.order.entity.Order;
import com.enterprise.finance.order.entity.TxDemo;
import com.enterprise.finance.order.mapper.OrderMapper;
import com.enterprise.finance.order.mapper.TxDemoMapper;
import com.enterprise.finance.order.converter.OrderConverter;
import org.apache.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final TxDemoMapper txDemoMapper;
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
        return userFeignClient.getById(userId);
    }

    @GlobalTransactional(name = "create-order", rollbackFor = Exception.class)
    public OrderDTO create(CreateOrderRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setAmount(request.getAmount());
        order.setStatus("CREATED");
        orderMapper.insert(order);

        log.info("create order success: {}", order.getId());

        userFeignClient.deduct(request.getUserId(), request.getAmount());
        return orderConverter.doToDto(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public TxDemoDTO insertTxDemo(String tag) {
        userFeignClient.insertTxDemo(tag);
        TxDemo row = new TxDemo();
        row.setTag(tag);
        row.setService("order");
        txDemoMapper.insert(row);
        log.info("tx-demo order insert success: tag={}, id={}", tag, row.getId());
        return new TxDemoDTO(row.getId(), row.getTag(), row.getService());
    }

    public List<TxDemoDTO> listTxDemo(String tag) {
        return txDemoMapper.selectList(new LambdaQueryWrapper<TxDemo>()
                        .eq(TxDemo::getTag, tag)
                        .orderByAsc(TxDemo::getId))
                .stream()
                .map(row -> new TxDemoDTO(row.getId(), row.getTag(), row.getService()))
                .toList();
    }
}
