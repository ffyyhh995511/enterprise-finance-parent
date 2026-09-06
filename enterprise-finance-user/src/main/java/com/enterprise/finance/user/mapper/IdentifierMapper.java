package com.enterprise.finance.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.finance.common.dto.IdentifierPageRequest;
import com.enterprise.finance.user.entity.Identifier;
import org.apache.ibatis.annotations.Param;

public interface IdentifierMapper extends BaseMapper<Identifier> {
    
    /**
     * 自定义 XML 分页查询
     * @param page MyBatis-Plus 的分页对象（必须放在第一个参数）
     * @param request 查询条件
     * @return 分页结果
     */
    Page<Identifier> selectPageByCondition(Page<Identifier> page, @Param("req") IdentifierPageRequest request);
}