package com.enterprise.finance.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.finance.common.dto.IdentifierDTO;
import com.enterprise.finance.common.dto.IdentifierPageRequest;
import com.enterprise.finance.common.dto.IdentifierSaveRequest;
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
        Page<Identifier> page = new Page<>(request.getCurrent(), request.getSize());
        
        Page<Identifier> resultPage = identifierMapper.selectPageByCondition(page, request);
        
        return PageResult.of(
                identifierConverter.doToDtoList(resultPage.getRecords()),
                resultPage.getTotal(),
                resultPage.getSize(),
                resultPage.getCurrent()
        );
    }
}