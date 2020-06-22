package com.pig4cloud.course.refresh.bug;


import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * @author lengleng
 * @date 2020/6/22
 */
@SpringBootTest
public class RefreshBugApplicationTest {
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
}
