package com.rainbow.pangu.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
@EnableScheduling
@EnableAsync
class SchedulerConfig {
    @Bean
    fun taskScheduler(): TaskScheduler {
        val taskScheduler = ThreadPoolTaskScheduler()
        taskScheduler.setThreadNamePrefix("task-scheduler-")
        taskScheduler.poolSize = 20
        return taskScheduler
    }

    @Bean("asyncExecutor")
    fun taskExecutor(): TaskExecutor {
        val taskExecutor = ThreadPoolTaskExecutor()
        taskExecutor.setThreadNamePrefix("async-executor-")
        taskExecutor.corePoolSize = 10
        taskExecutor.maxPoolSize = 40
        taskExecutor.queueCapacity = 10
        taskExecutor.keepAliveSeconds = 1200
        return taskExecutor
    }
}