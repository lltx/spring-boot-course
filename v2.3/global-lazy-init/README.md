## 关于延迟加载

在 Spring 中，默认情所有定的 bean 及其依赖项目都是在应用启动时创建容器上下文是被初始化的。测试代码如下：

```java
@Slf4j
@Configuration
public class DemoConfig {
    public DemoConfig() {
        log.warn(" > > > demoConfig 被初始化 > > >");
    }
}
```

启动应用日志：

```java
[           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
[           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1193 ms
[           main] c.p.c.global.lazy.config.DemoConfig      :  > > > demoConfig 被初始化 > > >
[           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
[           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
```

如上日志： 在 Tomcat started 之前 DemoConfig bean 已经被初始化创建。

一般情况程序在启动时时有大量的 Bean 需要初始化，例如 数据源初始化、缓存初始化等导致应用程序启动非常的慢。在 spring boot 2.2 之前的版本，我们对这些 bean 使用第手动增加 **@Lazy** 注解，来实现启动时不初始化，业务程序在调用需要时再去初始化，如上代码修改为即可：

```java
@Lazy
@Configuration
public class DemoConfig {}
```

## 为什么需要全局懒加载

同上文中提到我们需要手动在 bean 增加 **@Lazy** 注解，这就意味着我们仅能对程序中自行实现的 bean 进行添加。但是现在 spring boot 应用中引入了很多第三方 starter ，比如 druid-spring-boot-starter 数据源注入、spring-boot-starter-data-redis 缓存等默认情况下， 引入即注入了相关 bean 我们无法去修改添加 **@Lazy**。

- spring boot 2.2 新增全局懒加载属性，开启后全局 bean 被设置为懒加载，需要时再去创建

```yaml
spring:
  main:
    lazy-initialization: true  #默认false 关闭
```

- 个别 bean 可以通过设置 **@Lazy(false)** 排除，设置为启动时加载

```java
@Lazy(false)
@Configuration
public class DemoConfig {}
```

- 当然也可以指定规则实现 LazyInitializationExcludeFilter 规则实现排除

```java
@Bean
 LazyInitializationExcludeFilter integrationLazyInitExcludeFilter() {
    return LazyInitializationExcludeFilter.forBeanTypes(DemoConfig.class);
}
```

## 全局懒加载的问题

通过设置全局懒加载，我们可以减少启动时的创建任务从而大幅度的缩减应用的启动时间。但全局懒加载的缺点可以归纳为以下两点：

- Http 请求的初始时间变长。 这里准确的来说是第一次 http 请求处理的时间变长，之后的请求不受影响（说到这里自然而然的会联系到 spring cloud 启动后的第一次调用超时的问题）。

- 错误不会在应用启动时抛出，不利于早发现、早解决、早下班。

## 总结

![](http://pigx.vip/20200626174128_RQNZg9_Screenshot.jpeg)

![](http://pigx.vip/20200626174118_bswYrd_Screenshot.jpeg)
