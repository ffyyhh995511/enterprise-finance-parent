package com.enterprise.finance.product.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.enterprise.finance.common.config.DemoProperties;
import com.enterprise.finance.common.dto.DemoConfigDTO;
import com.enterprise.finance.order.api.dto.OrderDTO;
import com.enterprise.finance.product.api.dto.ProductChainResultDTO;
import com.enterprise.finance.product.api.dto.ProductDTO;
import com.enterprise.finance.product.api.dto.PurchaseProductRequest;
import com.enterprise.finance.product.api.dto.TxDemoChainResultDTO;
import com.enterprise.finance.user.api.dto.UserDTO;
import com.enterprise.finance.common.result.R;
import com.enterprise.finance.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final DemoProperties demoProperties;

    @GetMapping("/list")
    public R<List<ProductDTO>> list() {
        return R.ok(productService.listOnSale());
    }

    @GetMapping("/user/{userId}")
    public R<UserDTO> user(@PathVariable Long userId) {
        return R.ok(productService.getUser(userId));
    }

    @GetMapping("/orders/{userId}")
    public R<List<OrderDTO>> orders(@PathVariable Long userId) {
        return R.ok(productService.listOrders(userId));
    }

    /**
     * Smoke test: user -> product -> order list in one call.
     */
    @GetMapping("/chain/{userId}/{productId}")
    public R<ProductChainResultDTO> chain(@PathVariable Long userId, @PathVariable Long productId) {
        return R.ok(productService.chain(userId, productId));
    }

    /**
     * Purchase flow: deduct stock then create order (order deducts user balance).
     */
    @PostMapping("/purchase")
    public R<ProductChainResultDTO> purchase(@Valid @RequestBody PurchaseProductRequest request) {
        return R.ok(productService.purchase(request));
    }

    /**
     * Seata 全局事务回滚测算（写接口）。
     * <p>
     * 过程：product 开启全局事务 → 调用 order → order 先调 user 插入 t_tx_demo，
     * 再由 order 插入 t_tx_demo → 回到 product 插入 t_tx_demo → product 主动抛异常。
     * 效果：三个库里该 tag 的插入都应被回滚；本接口固定返回失败（HTTP 400）。
     * 随后用 GET /product/tx-demo/{tag} 核对 userRows / orderRows / productRows 是否都为空。
     */
    @PostMapping("/tx-demo/rollback")
    public R<Void> txDemoRollback(@RequestParam(required = false) String tag) {
        productService.insertChainThenRollback(tag);
        return R.ok();
    }

    /**
     * Seata 回滚结果查询（读接口）。
     * <p>
     * 过程：分别查 user、order、product 三个库的 t_tx_demo（同一 tag）。
     * 效果：若上一笔 POST /tx-demo/rollback 已回滚，三边列表为空且 rolledBack=true；
     * 若仍有行，说明对应服务未加入全局事务或未回滚。
     */
    @GetMapping("/tx-demo/{tag}")
    public R<TxDemoChainResultDTO> txDemo(@PathVariable String tag) {
        return R.ok(productService.queryTxDemo(tag));
    }

    @GetMapping("/config")
    public R<DemoConfigDTO> config() {
        return R.ok(new DemoConfigDTO("enterprise-finance-product", demoProperties.getMessage(), demoProperties.getTimeoutMs()));
    }

    @GetMapping("/sentinel/circuit")
    @SentinelResource(value = "productCircuit", blockHandler = "circuitBlock", fallback = "circuitFallback")
    public R<String> circuit(@RequestParam(defaultValue = "false") boolean fail,
                             @RequestParam(defaultValue = "0") long sleepMs) throws InterruptedException {
        if (sleepMs > 0) {
            Thread.sleep(sleepMs);
        }
        if (fail) {
            throw new IllegalStateException("simulated product circuit failure");
        }
        return R.ok("product circuit ok");
    }

    @GetMapping("/sentinel/fallback")
    @SentinelResource(value = "productFallback", blockHandler = "fallbackBlock", fallback = "doFallback")
    public R<String> fallback(@RequestParam(defaultValue = "false") boolean fail) {
        if (fail) {
            throw new IllegalStateException("simulated product business failure");
        }
        return R.ok("product fallback resource ok");
    }

    public R<String> circuitBlock(boolean fail, long sleepMs, BlockException ex) {
        return R.fail("product circuit blocked: " + ex.getClass().getSimpleName());
    }

    public R<String> circuitFallback(boolean fail, long sleepMs, Throwable ex) {
        return R.fail("product circuit fallback: " + ex.getMessage());
    }

    public R<String> fallbackBlock(boolean fail, BlockException ex) {
        return R.fail("product fallback blocked: " + ex.getClass().getSimpleName());
    }

    public R<String> doFallback(boolean fail, Throwable ex) {
        return R.fail("product fallback: " + ex.getMessage());
    }
}
