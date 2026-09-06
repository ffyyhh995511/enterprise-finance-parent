package com.enterprise.finance.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("identifier")
public class Identifier {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String deviceId;

    private String idfa;

    private String anonId;

    private String appVersion;

    private LocalDateTime createTime;

    private Integer createTimeZone;

    private LocalDateTime updateTime;

    private Integer updateTimeZone;
}