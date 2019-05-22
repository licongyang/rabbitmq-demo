package cn.zeus.config;

import com.alibaba.fastjson.JSON;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan("cn")
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
    //配置tomcat端口
    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory(){
        TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory(8080);
        return tomcatServletWebServerFactory;
    }


    //如果使用yml，这个RabbitTemplate是不用配置的
    //如果想要实现多种的RabbitTemplate.ConfirmCallback，
    // 1.需要将该类设置为非单例（@Scope("prototype")）,
    // 2.然后在使用RabbitTemplate重新设置ConfirmCallback（参见RabbitmqMessageSend中前置处理@PostConstruct部分）
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setConnectionFactory(connectionFactory);
        //消息转化器
//        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setMessageConverter(new MessageConverter() {
            @Override
            public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
               //保证springboot解析数据时，正常解析
//                messageProperties.setContentType("text/xml");
//                messageProperties.setContentEncoding("utf-8");
                //发送时
                Message message = new Message(JSON.toJSONBytes(o),messageProperties);
                System.out.println("调用了消息解析器");
                return message;
            }

            @Override
            public Object fromMessage(Message message) throws MessageConversionException {
                //接受时
                return null;
            }
        });
        //发送方确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                //实现发送方确认，比如发送交换机失败，则将发送的消息入库
//                这个布尔值就是消息是否发送到交换机成功标识，可以根据这个标识对错误信息，进行处理
                System.out.println(b);
                //发送失败信息
                System.out.println(s);
//                 CorrelationData是业务id的封装类，可以在客户端发送消息时，携带这个信息(可以在发送时，增加这个参数，参加RabbitmqMessageSend)，当这里出现问题时，找到这笔消息
                System.out.println(correlationData);
            }
        });

        //开启失败回调
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                //message：发送的消息 “hello”+ 消息的配置（消息过期时间、排序权重 rpc contentType contentEncoding）
                System.out.println(message);
                //返回码
                System.out.println(replyCode);
                //返回信息
                System.out.println(replyText);
                //交换机
                System.out.println(exchange);
                //路由键
                System.out.println(routingKey);
            }
        });
        return rabbitTemplate;
    }
    //创建交换机
    @Bean
    public DirectExchange directExchange(){
        //增加死信交换机
        Map<String, Object> map = new HashMap<>();
//        rabbitAdmin(connectionFactory()).deleteExchange("directExchage2");
        //设置消息的过期时间 单位毫秒
        map.put("x-message-ttl",10000);
//        这个类型交换机，一般是fanout类型交换机
        map.put("alternate-exchange","exchange");
        //指定重定向的路由建 消息作废之后可以决定需不需要更改他的路由建 如果需要 就在这里指定
        map.put("x-dead-letter-routing-key","dead.order");
        return new DirectExchange("directExchage3",false,false,map);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        return new RabbitAdmin(connectionFactory);
    }

    //创建队列
    @Bean
    public Queue queue(){
        return new Queue("directQueue",true);
    }
    //创建绑定

    @Bean
    public Binding binding(){
        return BindingBuilder.bind(queue()).to(directExchange()).with("direct.key");
    }
}

