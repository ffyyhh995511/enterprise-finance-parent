package com.enterprise.finance.product.api;

import com.enterprise.finance.product.api.dto.ProductDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface ProductApi {

    @GetMapping("/product/{id}")
    ProductDTO getById(@PathVariable("id") Long id);

    @PostMapping("/product/{id}/deduct-stock")
    ProductDTO deductStock(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity);
}
