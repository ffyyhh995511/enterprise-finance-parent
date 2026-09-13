package com.enterprise.finance.user.api;

import com.enterprise.finance.common.dto.TxDemoDTO;
import com.enterprise.finance.user.api.dto.UserDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

public interface UserApi {

    @GetMapping("/user/{id}")
    UserDTO getById(@PathVariable("id") Long id);

    @PostMapping("/user/{id}/deduct")
    UserDTO deduct(@PathVariable("id") Long id, @RequestParam("amount") BigDecimal amount);

    @PostMapping("/user/tx-demo")
    TxDemoDTO insertTxDemo(@RequestParam("tag") String tag);

    @GetMapping("/user/tx-demo/{tag}")
    List<TxDemoDTO> listTxDemo(@PathVariable("tag") String tag);
}
