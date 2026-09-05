package com.enterprise.finance.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("t_order")
public class Order {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private BigDecimal amount;

    private String status;
}
