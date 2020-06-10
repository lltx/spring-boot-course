package com.pig4cloud.course.prop.traceability.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 示例配置文件
 *
 * @author lengleng
 * @date 2020/6/10
 */
@Data
@Component
@ConfigurationProperties("demo")
public class DemoConfig {

    private String username;

    private String password;
}
