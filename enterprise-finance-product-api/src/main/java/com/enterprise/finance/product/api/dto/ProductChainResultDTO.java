package com.enterprise.finance.product.api.dto;

import com.enterprise.finance.order.api.dto.OrderDTO;
import com.enterprise.finance.user.api.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductChainResultDTO {

    private UserDTO user;
    private ProductDTO product;
    private OrderDTO order;
    private List<OrderDTO> userOrders;
}
