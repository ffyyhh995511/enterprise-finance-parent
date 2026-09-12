package com.enterprise.finance.order.converter;

import com.enterprise.finance.order.api.dto.OrderDTO;
import com.enterprise.finance.order.entity.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderConverter {

    OrderDTO doToDto(Order order);

    List<OrderDTO> doToDtoList(List<Order> orderList);
}
