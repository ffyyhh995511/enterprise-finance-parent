package com.enterprise.finance.product.converter;

import com.enterprise.finance.product.api.dto.ProductDTO;
import com.enterprise.finance.product.entity.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductConverter {

    ProductDTO doToDto(Product product);

    List<ProductDTO> doToDtoList(List<Product> productList);
}
