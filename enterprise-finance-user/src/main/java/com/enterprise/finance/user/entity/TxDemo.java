package com.enterprise.finance.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_tx_demo")
public class TxDemo {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String tag;

    private String service;
}
