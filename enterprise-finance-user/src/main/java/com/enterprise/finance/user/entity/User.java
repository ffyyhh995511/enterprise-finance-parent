package com.enterprise.finance.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("t_user")
public class User {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String username;

    private BigDecimal balance;
}
