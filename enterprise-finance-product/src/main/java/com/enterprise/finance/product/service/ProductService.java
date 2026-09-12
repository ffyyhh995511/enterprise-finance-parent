package com.enterprise.finance.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.enterprise.finance.order.api.dto.CreateOrderRequest;
import com.enterprise.finance.order.api.dto.OrderDTO;
import com.enterprise.finance.product.api.dto.ProductChainResultDTO;
import com.enterprise.finance.product.api.dto.ProductDTO;
import com.enterprise.finance.product.api.dto.PurchaseProductRequest;
import com.enterprise.finance.user.api.dto.UserDTO;
import com.enterprise.finance.common.exception.BusinessException;
import com.enterprise.finance.order.api.feign.OrderFeignClient;
import com.enterprise.finance.user.api.feign.UserFeignClient;
import com.enterprise.finance.product.converter.ProductConverter;
import com.enterprise.finance.product.entity.Product;
import com.enterprise.finance.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;
    private final ProductConverter productConverter;
    private final UserFeignClient userFeignClient;
    private final OrderFeignClient orderFeignClient;

    public ProductDTO getById(Long id) {
        Product product = requireProduct(id);
        return productConverter.doToDto(product);
    }

    public List<ProductDTO> listOnSale() {
        return productConverter.doToDtoList(productMapper.selectList(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, "ON_SALE")
                .orderByAsc(Product::getId)));
    }

    public UserDTO getUser(Long userId) {
        return userFeignClient.getById(userId);
    }

    public List<OrderDTO> listOrders(Long userId) {
        return orderFeignClient.listByUserId(userId);
    }

    /**
     * Read-only chain: user -> product -> order list, for smoke testing Feign wiring.
     */
    public ProductChainResultDTO chain(Long userId, Long productId) {
        UserDTO user = getUser(userId);
        ProductDTO product = getById(productId);
        List<OrderDTO> orders = listOrders(userId);
        return new ProductChainResultDTO(user, product, null, orders);
    }

    /**
     * Write chain: validate user -> deduct stock -> create order (order will deduct balance).
     */
    @GlobalTransactional(name = "purchase-product", rollbackFor = Exception.class)
    public ProductChainResultDTO purchase(PurchaseProductRequest request) {
        UserDTO user = getUser(request.getUserId());
        if (user.getBalance() == null) {
            throw new BusinessException("user balance unavailable: " + request.getUserId());
        }

        Product product = requireProduct(request.getProductId());
        if (!"ON_SALE".equals(product.getStatus())) {
            throw new BusinessException("product not on sale: " + product.getId());
        }
        if (product.getStock() < request.getQuantity()) {
            throw new BusinessException("insufficient stock, remain=" + product.getStock());
        }

        BigDecimal amount = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        if (user.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("insufficient balance, need=" + amount + ", remain=" + user.getBalance());
        }

        product.setStock(product.getStock() - request.getQuantity());
        productMapper.updateById(product);
        log.info("deduct product stock success: id={}, quantity={}, remain={}",
                product.getId(), request.getQuantity(), product.getStock());

        CreateOrderRequest orderRequest = new CreateOrderRequest();
        orderRequest.setUserId(request.getUserId());
        orderRequest.setAmount(amount);
        orderRequest.setRollback(Boolean.FALSE);
        OrderDTO createdOrder = orderFeignClient.create(orderRequest);

        if (Boolean.TRUE.equals(request.getRollback())) {
            throw new BusinessException("manual rollback for product-order seata demo");
        }

        UserDTO latestUser = getUser(request.getUserId());
        List<OrderDTO> orders = listOrders(request.getUserId());
        return new ProductChainResultDTO(
                latestUser,
                productConverter.doToDto(product),
                createdOrder,
                orders
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductDTO deductStock(Long id, Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new BusinessException("quantity must be >= 1");
        }
        Product product = requireProduct(id);
        if (product.getStock() < quantity) {
            throw new BusinessException("insufficient stock, remain=" + product.getStock());
        }
        product.setStock(product.getStock() - quantity);
        productMapper.updateById(product);
        return productConverter.doToDto(product);
    }

    private Product requireProduct(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("product not found: " + id);
        }
        return product;
    }
}
