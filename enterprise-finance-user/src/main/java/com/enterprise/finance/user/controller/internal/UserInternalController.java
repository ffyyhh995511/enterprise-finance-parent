package com.enterprise.finance.user.controller.internal;

import com.enterprise.finance.common.dto.TxDemoDTO;
import com.enterprise.finance.user.api.UserApi;
import com.enterprise.finance.user.api.dto.UserDTO;
import com.enterprise.finance.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserInternalController implements UserApi {

    private final UserService userService;

    @Override
    public UserDTO getById(Long id) {
        return userService.getById(id);
    }

    @Override
    public UserDTO deduct(Long id, BigDecimal amount) {
        return userService.deduct(id, amount);
    }

    @Override
    public TxDemoDTO insertTxDemo(String tag) {
        return userService.insertTxDemo(tag);
    }

    @Override
    public List<TxDemoDTO> listTxDemo(String tag) {
        return userService.listTxDemo(tag);
    }
}
