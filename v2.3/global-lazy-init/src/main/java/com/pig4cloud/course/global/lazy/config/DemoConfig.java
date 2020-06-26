package com.pig4cloud.course.global.lazy.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * @author lengleng
 * @date 2020/6/26
 */
@Slf4j
@Configuration
public class DemoConfig {
    public DemoConfig() {
        log.warn(" > > > demoConfig 被初始化 > > >");
    }
}
