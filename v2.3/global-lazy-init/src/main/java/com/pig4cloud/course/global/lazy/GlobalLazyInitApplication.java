package com.pig4cloud.course.global.lazy;

import com.pig4cloud.course.global.lazy.config.DemoConfig;
import org.springframework.boot.LazyInitializationExcludeFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GlobalLazyInitApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlobalLazyInitApplication.class, args);
    }

    @Bean
    LazyInitializationExcludeFilter integrationLazyInitExcludeFilter() {
        return LazyInitializationExcludeFilter.forBeanTypes(DemoConfig.class);
    }

}
