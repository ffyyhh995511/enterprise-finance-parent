package com.enterprise.finance.order.api;

import com.enterprise.finance.common.dto.TxDemoDTO;
import com.enterprise.finance.order.api.dto.CreateOrderRequest;
import com.enterprise.finance.order.api.dto.OrderDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface OrderApi {

    @GetMapping("/order/{id}")
    OrderDTO getById(@PathVariable("id") Long id);

    @GetMapping("/order/list/{userId}")
    List<OrderDTO> listByUserId(@PathVariable("userId") Long userId);

    @PostMapping("/order/create")
    OrderDTO create(@Valid @RequestBody CreateOrderRequest request);

    @PostMapping("/order/tx-demo")
    TxDemoDTO insertTxDemo(@RequestParam("tag") String tag);

    @GetMapping("/order/tx-demo/{tag}")
    List<TxDemoDTO> listTxDemo(@PathVariable("tag") String tag);
}
