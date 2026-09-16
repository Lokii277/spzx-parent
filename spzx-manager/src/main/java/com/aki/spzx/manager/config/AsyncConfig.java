package com.aki.spzx.manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 业务日志专用线程池
     */
    @Bean("businessLogExecutor")
    public Executor businessLogExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数：线程池创建时就会有的线程数量
        executor.setCorePoolSize(5);

        // 最大线程数：线程不够用时，最多能创建到的数量
        executor.setMaxPoolSize(10);

        // 队列容量：核心线程都在忙时，新任务会进入队列等待
        executor.setQueueCapacity(100);

        // 线程名前缀：方便在日志里识别是哪个线程池的线程
        executor.setThreadNamePrefix("business-log-");

        // 拒绝策略：队列满了且线程数到最大值时，由调用者线程自己执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 初始化线程池
        executor.initialize();

        return executor;
    }
}