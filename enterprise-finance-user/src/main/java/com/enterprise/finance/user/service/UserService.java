package com.enterprise.finance.user.service;

import com.enterprise.finance.common.dto.OrderDTO;
import com.enterprise.finance.common.dto.UserDTO;
import com.enterprise.finance.common.exception.BusinessException;
import com.enterprise.finance.common.feign.OrderFeignClient;
import com.enterprise.finance.common.result.R;
import com.enterprise.finance.user.entity.User;
import com.enterprise.finance.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final OrderFeignClient orderFeignClient;

    public UserDTO getById(Long id) {
        return toDto(requireUser(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public UserDTO deduct(Long id, BigDecimal amount) {
        User user = requireUser(id);
        if (user.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("insufficient balance");
        }
        user.setBalance(user.getBalance().subtract(amount));
        userMapper.updateById(user);
        return toDto(user);
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

    private UserDTO toDto(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getBalance());
    }
}
