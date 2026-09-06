package com.enterprise.finance.user.converter;

import com.enterprise.finance.common.dto.UserDTO;
import com.enterprise.finance.user.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserConverter {

    UserDTO doToDto(User user);

    List<UserDTO> doToDtoList(List<User> userList);
}
