在 Spring Cloud 体系的项目中，配置中心主要用于提供分布式的配置管理，其中有一个重要的注解：@RefreshScope，如果代码中需要动态刷新配置，在需要的类上加上该注解就行。本文分享一下笔者遇到与 @ConditionalOnSingleCandidate 注解冲突的问题

## 问题背景

项目再引入 RabbitMQ,在自定义 connectionFactory 时，手滑加上了 `@RefreshScope`

```java
@Bean
@RefreshScope
public CachingConnectionFactory connectionFactory() {
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
    connectionFactory.setAddresses("172.17.0.111");
    connectionFactory.setUsername("guest");
    connectionFactory.setPassword("guest");
    connectionFactory.setVirtualHost("/");
    return connectionFactory;
}
```

系统报错无法注入 RabbitTemplate

```java
org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'com.pig4cloud.course.refresh.bug.RefreshBugApplicationTest':

Unsatisfied dependency expressed through field 'rabbitTemplate'; nested exception is org.springframework.beans.factory.NoSuchBeanDefinitionException:

No qualifying bean of type 'org.springframework.amqp.rabbit.core.RabbitTemplate' available: expected at least 1 bean which qualifies as autowire candidate.

Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true)}

```

## 排查

- 1. 默认情况下 spring-boot-starter-amqp 会默认给我们注入 rabbitTemplate 实现

RabbitAutoConfiguration#rabbitTemplate

```java
@Bean
@ConditionalOnSingleCandidate(ConnectionFactory.class)
@ConditionalOnMissingBean(RabbitOperations.class)
public RabbitTemplate rabbitTemplate(RabbitTemplateConfigurer configurer, ConnectionFactory connectionFactory) {
  RabbitTemplate template = new RabbitTemplate();
  configurer.configure(template, connectionFactory);
  return template;
}
```

- 2. 开启 Spring Boot 启动 debugger 日志，查看注入信息

```java
   RabbitAutoConfiguration.RabbitTemplateConfiguration#rabbitTemplate:
      Did not match:
         - @ConditionalOnSingleCandidate (types: org.springframework.amqp.rabbit.connection.ConnectionFactory; SearchStrategy: all)

         did not find a primary bean from beans 'connectionFactory', 'scopedTarget.connectionFactory' (OnBeanCondition)
```

提示 `ConditionalOnSingleCandidate` 注解的方法不能查找到唯一 `ConnectionFactory` 实现

- 3. 使用 @RefreshScope 注解的 bean，默认情况下同时会生成 `scopedTarget.beanName`的 bean

```java
@Autowired
private ApplicationContext context;

@Test
public void testRabbitTemplate() {
    String[] beanNames = context.getBeanNamesForType(ConnectionFactory.class);

    for (String beanName : beanNames) {
        System.out.println(beanName);
    }

    Assert.isTrue(beanNames.length == 2);
}

scopedTarget.connectionFactory
connectionFactory
```

- 4. 由于 ConditionalOnSingleCandidate 成立的条件是全局只能有一个此类型的 bean 所以 默认的 RabbitTemplate 无法注入

## 常见被 ConditionalOnSingleCandidate 注解的 bean

- 1. 使用 JdbcTemplate 无法在自定义 DataSource 添加 @RefreshScope

```java
@ConditionalOnSingleCandidate(DataSource.class)
public class JdbcTemplateAutoConfiguration {}
```

- 2.  MailSenderValidator 邮件发送校验器无法添加 @RefreshScope

```java
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(MailSenderAutoConfiguration.class)
@ConditionalOnProperty(prefix = "spring.mail", value = "test-connection")
@ConditionalOnSingleCandidate(JavaMailSenderImpl.class)
public class MailSenderValidatorAutoConfiguration {}
```

![](http://pigx.vip/20200622203347_4Mr1kM_Screenshot.jpeg)
