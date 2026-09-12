package com.enterprise.finance.user.api;

import com.enterprise.finance.user.api.dto.UserDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

public interface UserApi {

    @GetMapping("/user/{id}")
    UserDTO getById(@PathVariable("id") Long id);

    @PostMapping("/user/{id}/deduct")
    UserDTO deduct(@PathVariable("id") Long id, @RequestParam("amount") BigDecimal amount);
}
