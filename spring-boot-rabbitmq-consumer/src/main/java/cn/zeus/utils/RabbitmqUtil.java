package cn.zeus.utils;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Component
public class RabbitmqUtil {

    @RabbitListener(queues = "queue4")
    public void get(String message ){
        System.out.println(message);
        System.out.println("消费者1");
    }

//    @RabbitListener(queues = "testQueue")
//    public void get1(Message message ) throws UnsupportedEncodingException {
//        System.out.println(new String(message.getBody(),"utf-8"));
//        //这里可以用fastjson解序列化
//        System.out.println("消费者2");
//    }

    //containerFactory指定监听器容器，来决定监听器是否自动确认
    @RabbitListener(queues = "queue4",containerFactory = "simpleRabbitListenerContainerFactory")
    public void getMessage(Message message, Channel channel) throws IOException {
        //这里可以用fastjson解序列化
        System.out.println(new String(message.getBody(),"utf-8"));
        //根据库存业务操作决定消息确认或者退回
        if(repertoryOp()){
            //channel根据消息配置中的id确认消息，第二个参数表示，是否是批量处理。
            //如果是批量处理，可以再多少条之后，提交一次即可，保证最后一笔消息id被提交，前面的也就相当于提交了。
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            System.out.println("消息确认");
        }else{
            //消息退回
            //这个可以批量退回
            //第一个参数：消息id,第二个参数：是否批量退回，第三个参数：是否退回消息队列（否则丢弃）
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            //单条退回
//            channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
            System.out.println("消息退回");
        }
        System.out.println("消费者4");
    }

    private boolean repertoryOp(){
        return true;
    }
}
