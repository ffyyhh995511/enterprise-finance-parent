package com.enterprise.finance.common.feign.fallback;

import com.enterprise.finance.common.dto.UserDTO;
import com.enterprise.finance.common.feign.UserFeignClient;
import com.enterprise.finance.common.result.R;
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
            public R<UserDTO> getById(Long id) {
                log.warn("user-service getById fallback, id={}", id, cause);
                return R.fail("user-service degraded: " + cause.getMessage());
            }

            @Override
            public R<UserDTO> deduct(Long id, BigDecimal amount) {
                log.warn("user-service deduct fallback, id={}, amount={}", id, amount, cause);
                return R.fail("user-service deduct degraded: " + cause.getMessage());
            }
        };
    }
}
