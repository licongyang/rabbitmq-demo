package cn.amqp.rabbitmq.consumer;

import cn.amqp.rabbitmq.utils.ConnectionUtil;
import com.rabbitmq.client.*;

import java.io.IOException;

public class Consumer2 {

    public static void main(String[] args) {
        try {
            getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void getMessage() throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        final Channel channel = connection.createChannel();
        // channel.queueDeclare(ConnectionUtil.QUEUE_NAME,true,false,false,null);
        DefaultConsumer deliverCallback = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(new String(body, "UTF-8"));
                //表示消息消费成功
//                System.out.println("消费者2消息消费成功");
                //手工确认，第一个参数是消息的id,第二个参数是批量标识（ture标识批量）
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        };
        //消费，消息从ready状态，变成unacked状态
//        channel.basicConsume(ConnectionUtil.QUEUE_NAME, deliverCallback);
        //消费自动确认，一旦选择这种模式，消费发送到消费端，不管消息是否被消费端正常消费，都会将队列中的消息删除掉
//        channel.basicConsume(ConnectionUtil.QUEUE_NAME,true, deliverCallback);
        //消息预取
        channel.basicQos(1);
        //手工确认
        channel.basicConsume("queue4",false, deliverCallback);
    }
}