package com.enterprise.finance.product.controller.internal;

import com.enterprise.finance.product.api.ProductApi;
import com.enterprise.finance.product.api.dto.ProductDTO;
import com.enterprise.finance.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductInternalController implements ProductApi {

    private final ProductService productService;

    @Override
    public ProductDTO getById(Long id) {
        return productService.getById(id);
    }

    @Override
    public ProductDTO deductStock(Long id, Integer quantity) {
        return productService.deductStock(id, quantity);
    }
}
