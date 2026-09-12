package com.enterprise.finance.order.api;

import com.enterprise.finance.order.api.dto.CreateOrderRequest;
import com.enterprise.finance.order.api.dto.OrderDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface OrderApi {

    @GetMapping("/order/{id}")
    OrderDTO getById(@PathVariable("id") Long id);

    @GetMapping("/order/list/{userId}")
    List<OrderDTO> listByUserId(@PathVariable("userId") Long userId);

    @PostMapping("/order/create")
    OrderDTO create(@Valid @RequestBody CreateOrderRequest request);
}
