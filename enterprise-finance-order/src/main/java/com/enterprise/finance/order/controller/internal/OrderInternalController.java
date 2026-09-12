package com.enterprise.finance.order.controller.internal;

import com.enterprise.finance.order.api.OrderApi;
import com.enterprise.finance.order.api.dto.CreateOrderRequest;
import com.enterprise.finance.order.api.dto.OrderDTO;
import com.enterprise.finance.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderInternalController implements OrderApi {

    private final OrderService orderService;

    @Override
    public OrderDTO getById(Long id) {
        return orderService.getById(id);
    }

    @Override
    public List<OrderDTO> listByUserId(Long userId) {
        return orderService.listByUserId(userId);
    }

    @Override
    public OrderDTO create(CreateOrderRequest request) {
        return orderService.create(request);
    }
}
