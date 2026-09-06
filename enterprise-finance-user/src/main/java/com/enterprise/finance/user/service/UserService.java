package com.enterprise.finance.user.service;

import com.enterprise.finance.common.dto.OrderDTO;
import com.enterprise.finance.common.dto.UserDTO;
import com.enterprise.finance.common.exception.BusinessException;
import com.enterprise.finance.common.feign.OrderFeignClient;
import com.enterprise.finance.common.result.R;
import com.enterprise.finance.user.entity.User;
import com.enterprise.finance.user.mapper.UserMapper;
import com.enterprise.finance.user.converter.UserConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
// @RequiredArgsConstructor：Lombok 注解，自动为被 final 修饰的成员变量生成带参构造函数。
// 从 Spring 4.3 开始，如果一个类只有一个构造函数，Spring 会自动通过该构造函数注入依赖，
// 因此不需要在字段上使用 @Autowired 或 @Resource，这种做法被称为“构造器注入”，
// 可以保证依赖的不可变性（Immutability）并避免空指针异常。
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final OrderFeignClient orderFeignClient;
    private final UserConverter userConverter;

    public UserDTO getById(Long id) {
        return userConverter.doToDto(requireUser(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public UserDTO deduct(Long id, BigDecimal amount) {
        User user = requireUser(id);
        if (user.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("insufficient balance");
        }
        user.setBalance(user.getBalance().subtract(amount));
        userMapper.updateById(user);
        return userConverter.doToDto(user);
    }

    public List<OrderDTO> listOrders(Long userId) {
        requireUser(userId);
        R<List<OrderDTO>> result = orderFeignClient.listByUserId(userId);
        if (!result.isSuccess()) {
            throw new BusinessException(result.getMessage());
        }
        return result.getData();
    }

    private User requireUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("user not found: " + id);
        }
        return user;
    }
}
