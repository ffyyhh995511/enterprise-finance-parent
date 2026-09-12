package com.enterprise.finance.user.api.feign.fallback;

import com.enterprise.finance.common.exception.BusinessException;
import com.enterprise.finance.user.api.dto.UserDTO;
import com.enterprise.finance.user.api.feign.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class UserFeignFallbackFactory implements FallbackFactory<UserFeignClient> {

    @Override
    public UserFeignClient create(Throwable cause) {
        return new UserFeignClient() {
            @Override
            public UserDTO getById(Long id) {
                log.warn("user-service getById fallback, id={}", id, cause);
                throw new BusinessException("user-service degraded: " + cause.getMessage());
            }

            @Override
            public UserDTO deduct(Long id, BigDecimal amount) {
                log.warn("user-service deduct fallback, id={}, amount={}", id, amount, cause);
                throw new BusinessException("user-service deduct degraded: " + cause.getMessage());
            }
        };
    }
}
