package cn.zeus.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.UnsupportedEncodingException;

@Configuration
public class RabbitmqConfig {

    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost",5672);
//        connectionFactory.setHost("localhost");
//        connectionFactory.setPort(5672);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        connectionFactory.setVirtualHost("testhost");
        //是否开启消息确认机制
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }
    //如果使用yml，这个RabbitTemplate是不用配置的
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setConnectionFactory(connectionFactory);
        return rabbitTemplate;
    }

    //配置tomcat端口
    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory(){
        TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory(8082);
        return tomcatServletWebServerFactory;
    }

    //监听器容器工厂
    @Bean
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(){
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        //首先将连接工厂注入进来
        simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory());
        //设置手工确认
        simpleRabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return simpleRabbitListenerContainerFactory;
    }

    //消息监听器
    // --javabean方式
    //注解方式，是声明消息监听器工厂类，在@RabbitListener中赋值该工厂类名到containerfactory参数
    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(){
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setConnectionFactory(connectionFactory());
        //设置消息监听器
        simpleMessageListenerContainer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    System.out.println(new String(message.getBody(),"utf-8"));
                    System.out.println("消息监听器-javaconfig");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        //添加要监听的队列名集合
        simpleMessageListenerContainer.addQueueNames("","");
        //添加要监听的队列
//        simpleMessageListenerContainer.addQueues(new Queue(""));
        //设置手工确认
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return simpleMessageListenerContainer;

    }
}

