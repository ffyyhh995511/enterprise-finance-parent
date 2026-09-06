package com.enterprise.finance.user.controller;

import com.enterprise.finance.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequestMapping("/thread-test")
public class ThreadTestController {

    // 创建一个固定大小为 200 的线程池
    private final ExecutorService threadPool = Executors.newFixedThreadPool(200);

    /**
     * 1. 传统原生线程 (Platform Thread) 测试
     * 每一个任务直接 new Thread()。
     * 特点：线程是操作系统宝贵资源，非常重。如果 count 达到上万，很容易 OOM 或让 CPU 假死。
     */
    @GetMapping("/native")
    public R<String> testNativeThread(@RequestParam(defaultValue = "1000") int count) throws InterruptedException {
        long start = System.currentTimeMillis();
        List<Thread> threads = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(count);

        for (int i = 0; i < count; i++) {
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(100); // 模拟网络 I/O 阻塞
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
            threads.add(t);
            t.start();
        }

        latch.await();
        long end = System.currentTimeMillis();
        return R.ok("原生线程(new Thread) 创建 " + count + " 个任务，耗时: " + (end - start) + " ms");
    }

    /**
     * 2. 线程池测试
     * 特点：复用核心线程，避免了频繁创建和销毁线程的开销，保护了系统不被冲垮。
     * 区别：如果有大量 I/O 阻塞任务（比如这里 sleep），任务会在队列里等待，整体耗时会成倍变长。
     */
    @GetMapping("/pool")
    public R<String> testThreadPool(@RequestParam(defaultValue = "1000") int count) throws InterruptedException {
        long start = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(count);

        for (int i = 0; i < count; i++) {
            threadPool.submit(() -> {
                try {
                    Thread.sleep(100); // 模拟网络 I/O 阻塞
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long end = System.currentTimeMillis();
        return R.ok("线程池(固定200核心) 执行 " + count + " 个任务，耗时: " + (end - start) + " ms");
    }

    /**
     * 3. 异步并发 (CompletableFuture)
     * JDK 17 下，由于没有官方协程，通常用 CompletableFuture 做非阻塞的异步编排。
     */
    @GetMapping("/async")
    public R<String> testAsync(@RequestParam(defaultValue = "1000") int count) {
        long start = System.currentTimeMillis();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(100); // 模拟网络 I/O 阻塞
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, threadPool));
        }

        // 等待所有异步任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long end = System.currentTimeMillis();
        return R.ok("CompletableFuture 执行 " + count + " 个任务，耗时: " + (end - start) + " ms");
    }

    /**
     * 4. 伪代码：Java 的协程（虚拟线程 Virtual Threads）
     * ⚠️ 注意：你当前项目是 JDK 17，Java 的协程（虚拟线程）是在 JDK 21 正式发布的。
     * 所以这里我只能用文字返回告诉你它如果用 JDK 21 运行会怎样，无法直接编译 Thread.ofVirtual() 语法。
     */
    @GetMapping("/coroutine")
    public R<String> testCoroutineInfo() {
        String info = "你当前使用的是 JDK 17。Java 中的【协程】被称为【虚拟线程 (Virtual Threads)】，在 JDK 21 中正式发布。\n" +
                "如果升级到 JDK 21，你的代码可以写成：\n" +
                "Thread.startVirtualThread(() -> { Thread.sleep(100); });\n\n" +
                "【性能与区别】：\n" +
                "1. 线程/线程池（Platform Thread）：是操作系统层面的，1个Java线程对应1个系统线程，非常重，最多只能创建几千个。\n" +
                "2. 协程/虚拟线程（Virtual Thread）：是 JVM 层面的，由 JVM 调度（M:N 映射到少量系统线程上）。它非常轻量，可以同时创建【数百万个】！\n" +
                "3. 当碰到 I/O 阻塞（如 Sleep、查数据库、发 HTTP 请求）时，虚拟线程会自动让出底层的载体线程给其他虚拟线程用，完全不阻塞系统线程。\n" +
                "4. 性能提升：如果执行 10 万个 sleep(100) 的任务，线程池可能要执行几十秒，而虚拟线程只要 100 多毫秒就能全部干完！";
        return R.ok(info);
    }
}
