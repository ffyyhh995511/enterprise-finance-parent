package com.enterprise.finance.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.finance.user.api.dto.IdentifierDTO;
import com.enterprise.finance.user.api.dto.IdentifierPageRequest;
import com.enterprise.finance.user.api.dto.IdentifierSaveRequest;
import com.enterprise.finance.common.exception.BusinessException;
import com.enterprise.finance.common.result.PageResult;
import com.enterprise.finance.user.converter.IdentifierConverter;
import com.enterprise.finance.user.entity.Identifier;
import com.enterprise.finance.user.mapper.IdentifierMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class IdentifierService {

    private final IdentifierMapper identifierMapper;
    private final IdentifierConverter identifierConverter;

    @Transactional(rollbackFor = Exception.class)
    public IdentifierDTO create(IdentifierSaveRequest request) {
        // check unique device_id
        boolean exists = identifierMapper.exists(new LambdaQueryWrapper<Identifier>()
                .eq(Identifier::getDeviceId, request.getDeviceId()));
        if (exists) {
            throw new BusinessException("device_id already exists: " + request.getDeviceId());
        }

        Identifier identifier = identifierConverter.requestToDo(request);
        
        LocalDateTime now = LocalDateTime.now();
        // Assuming current system timezone offset in hours
        int zoneOffset = ZoneId.systemDefault().getRules().getOffset(now).getTotalSeconds() / 3600;
        
        identifier.setCreateTime(now);
        identifier.setCreateTimeZone(zoneOffset);
        identifier.setUpdateTime(now);
        identifier.setUpdateTimeZone(zoneOffset);

        identifierMapper.insert(identifier);
        return identifierConverter.doToDto(identifier);
    }

    @Transactional(rollbackFor = Exception.class)
    public IdentifierDTO update(Long id, IdentifierSaveRequest request) {
        Identifier identifier = identifierMapper.selectById(id);
        if (identifier == null) {
            throw new BusinessException("identifier not found: " + id);
        }

        // check unique device_id if changed
        if (!identifier.getDeviceId().equals(request.getDeviceId())) {
            boolean exists = identifierMapper.exists(new LambdaQueryWrapper<Identifier>()
                    .eq(Identifier::getDeviceId, request.getDeviceId()));
            if (exists) {
                throw new BusinessException("device_id already exists: " + request.getDeviceId());
            }
        }

        identifierConverter.updateDoFromRequest(request, identifier);
        
        LocalDateTime now = LocalDateTime.now();
        int zoneOffset = ZoneId.systemDefault().getRules().getOffset(now).getTotalSeconds() / 3600;
        
        identifier.setUpdateTime(now);
        identifier.setUpdateTimeZone(zoneOffset);

        identifierMapper.updateById(identifier);
        return identifierConverter.doToDto(identifier);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        int rows = identifierMapper.deleteById(id);
        if (rows == 0) {
            throw new BusinessException("identifier not found or already deleted: " + id);
        }
    }

    public IdentifierDTO getById(Long id) {
        Identifier identifier = identifierMapper.selectById(id);
        if (identifier == null) {
            throw new BusinessException("identifier not found: " + id);
        }
        return identifierConverter.doToDto(identifier);
    }

    public PageResult<IdentifierDTO> page(IdentifierPageRequest request) {
        // [第三步：在 Service 中调用，直接传 Page 对象]
        // 1. 创建 MyBatis-Plus 的 Page 对象，传入当前页码和每页大小
        Page<Identifier> page = new Page<>(request.getCurrent(), request.getSize());
        
        // 2. 调用 Mapper 查询，把 page 对象传进去。
        // 执行完这行后，MyBatis-Plus 会自动将查到的总记录数填入 page.total，将结果列表填入 page.records
        Page<Identifier> resultPage = identifierMapper.selectPageByCondition(page, request);
        
        // 3. 提取分页结果并转换为 DTO 返回
        return PageResult.of(
                identifierConverter.doToDtoList(resultPage.getRecords()),
                resultPage.getTotal(),
                resultPage.getSize(),
                resultPage.getCurrent()
        );
    }
}