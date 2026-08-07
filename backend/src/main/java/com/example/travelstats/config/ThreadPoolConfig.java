package com.example.travelstats.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * JUC线程池配置
 * 用于数据导入时的多线程并发处理
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    @ConfigurationProperties(prefix = "thread-pool")
    public ThreadPoolProperties threadPoolProperties() {
        return new ThreadPoolProperties();
    }

    @Bean("importExecutor")
    public ThreadPoolExecutor importExecutor(ThreadPoolProperties props) {
        return new ThreadPoolExecutor(
                props.getCoreSize(),
                props.getMaxSize(),
                props.getKeepAliveSeconds(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(props.getQueueCapacity()),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    public static class ThreadPoolProperties {
        private int coreSize = 4;
        private int maxSize = 8;
        private int queueCapacity = 10000;
        private int keepAliveSeconds = 60;

        public int getCoreSize() { return coreSize; }
        public void setCoreSize(int coreSize) { this.coreSize = coreSize; }

        public int getMaxSize() { return maxSize; }
        public void setMaxSize(int maxSize) { this.maxSize = maxSize; }

        public int getQueueCapacity() { return queueCapacity; }
        public void setQueueCapacity(int queueCapacity) { this.queueCapacity = queueCapacity; }

        public int getKeepAliveSeconds() { return keepAliveSeconds; }
        public void setKeepAliveSeconds(int keepAliveSeconds) { this.keepAliveSeconds = keepAliveSeconds; }
    }
}
