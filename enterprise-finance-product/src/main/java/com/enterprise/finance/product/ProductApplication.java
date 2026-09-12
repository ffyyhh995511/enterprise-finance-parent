package com.enterprise.finance.product;

import com.enterprise.finance.common.config.DemoProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients(basePackages = {
        "com.enterprise.finance.user.api.feign",
        "com.enterprise.finance.order.api.feign"
})
@EnableConfigurationProperties(DemoProperties.class)
@MapperScan("com.enterprise.finance.product.mapper")
@SpringBootApplication(scanBasePackages = "com.enterprise.finance")
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
