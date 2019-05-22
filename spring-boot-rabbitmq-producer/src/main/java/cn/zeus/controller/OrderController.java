package cn.zeus.controller;

import cn.zeus.ProducerUtil.RabbitmqMessageSend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Autowired
    RabbitmqMessageSend rabbitmqMessageSend;
    @RequestMapping("/order.do")
    public Object order(String message, String name,String routingKey){
        rabbitmqMessageSend.sendMessage(message, routingKey,name);
        return "下单成功";
    }

}
