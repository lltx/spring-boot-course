package com.pig4cloud.course.global.lazy.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author lengleng
 * @date 2020/6/26
 */
@Slf4j
@SpringBootTest
public class DemoConfigTest {
    @Autowired
    private DemoConfig demoConfig;

    @Test
    public void demoConfigBeanCreateTest() {
        log.warn("> > > Spring ApplicationContext Started > > >");

    }
}
