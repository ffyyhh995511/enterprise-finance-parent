package com.enterprise.finance.user.converter;

import com.enterprise.finance.user.api.dto.IdentifierDTO;
import com.enterprise.finance.user.api.dto.IdentifierSaveRequest;
import com.enterprise.finance.user.entity.Identifier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IdentifierConverter {

    IdentifierDTO doToDto(Identifier identifier);

    List<IdentifierDTO> doToDtoList(List<Identifier> identifiers);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createTimeZone", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateTimeZone", ignore = true)
    Identifier requestToDo(IdentifierSaveRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createTimeZone", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateTimeZone", ignore = true)
    void updateDoFromRequest(IdentifierSaveRequest request, @MappingTarget Identifier identifier);
}