package com.mymate.mymate.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 알림 처리를 위한 비동기 설정
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    
    /**
     * 알림 처리를 위한 전용 스레드 풀
     */
    @Bean("notificationTaskExecutor")
    public Executor notificationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);           // 기본 스레드 수
        executor.setMaxPoolSize(5);           // 최대 스레드 수
        executor.setQueueCapacity(100);       // 대기열 크기
        executor.setThreadNamePrefix("notification-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
