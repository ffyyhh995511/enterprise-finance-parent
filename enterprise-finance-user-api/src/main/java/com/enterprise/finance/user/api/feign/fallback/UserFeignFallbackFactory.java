package com.enterprise.finance.user.api.feign.fallback;

import com.enterprise.finance.common.dto.TxDemoDTO;
import com.enterprise.finance.common.exception.BusinessException;
import com.enterprise.finance.user.api.dto.UserDTO;
import com.enterprise.finance.user.api.feign.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
public class UserFeignFallbackFactory implements FallbackFactory<UserFeignClient> {

    @Override
    public UserFeignClient create(Throwable cause) {
        return new UserFeignClient() {
            @Override
            public UserDTO getById(Long id) {
                log.warn("user-service getById fallback, id={}", id, cause);
                return null;
            }

            @Override
            public UserDTO deduct(Long id, BigDecimal amount) {
                log.warn("user-service deduct fallback, id={}, amount={}", id, amount, cause);
                throw new BusinessException("user-service deduct degraded: " + cause.getMessage());
            }

            @Override
            public TxDemoDTO insertTxDemo(String tag) {
                log.warn("user-service insertTxDemo fallback, tag={}", tag, cause);
                throw new BusinessException("user-service tx-demo degraded: " + cause.getMessage());
            }

            @Override
            public List<TxDemoDTO> listTxDemo(String tag) {
                log.warn("user-service listTxDemo fallback, tag={}", tag, cause);
                throw new BusinessException("user-service tx-demo list degraded: " + cause.getMessage());
            }
        };
    }
}
