package cn.zeus.ProducerUtil;

import com.alibaba.fastjson.JSON;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class RabbitmqMessageSend {

    @Autowired
    RabbitTemplate rabbitTemplate;

    //对于spring容器管理的类，对于该注解的方法，会在类初始化之前处理
//    @PostConstruct
//    public void init(){
//        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
//            @Override
//            public void confirm(CorrelationData correlationData, boolean b, String s) {
//
//            }
//        });
//    }

    public void sendMessage(String message, String routingKey,String exchangeName){
        //模拟订单id封装类
        CorrelationData data = new CorrelationData("订单id");
        Map<String,Object> map = new HashMap<>();
        map.put("user","123");
        map.put("password","123456");

        //发送消息
        //springboot 会有内嵌消息转换器，这里用fastjson
//        rabbitTemplate.convertAndSend("directExchage2","direct.key111", map,data);
//        rabbitTemplate.convertAndSend("directExchage","direct.key", JSON.toJSONBytes(map),data);
        rabbitTemplate.convertAndSend(exchangeName,routingKey,message,data);
    }
}
