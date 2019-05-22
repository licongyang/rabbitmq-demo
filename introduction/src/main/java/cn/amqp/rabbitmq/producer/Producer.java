package cn.amqp.rabbitmq.producer;

import cn.amqp.rabbitmq.utils.ConnectionUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class Producer {

    public static void main(String[] args) {
        try {
            sendByExchange("hello");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendByExchange(String message) throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        //        消费者：
        //这里，我们演示绑定fanout的类型的交换机，所以不需要routingKey 就可以路由只需要绑定即可
        //（可能有同学要问了，如果没有绑定交换机怎么办呢？没有绑定交换机的话，消息会发给rabbitmq默认的交换机
        //        里面 默认的交换机隐式的绑定了所有的队列，默认的交换机类型是direct 路由键就是队列的名字）
        //        基本上这样子的话就已经进行一个快速入门了，由于我们现在做项目基本上都是用spring boot（就算没用spring
        //        boot也用spring 吧）所以后面我们直接基于spring boot来讲解（rabbitmq的特性，实战等）
        //生产者适合创建交换机和绑定，至于队列在生产者或者消费者创建都可以
        // 声明exchange
//        channel.exchangeDeclare(ConnectionUtil.EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        channel.exchangeDeclare("te", BuiltinExchangeType.FANOUT);
        //声明队列
        // 第二个参数表示队列是否持久化（跟消息持久化不同），
        // 第三个参数表示是否是排他队列，表示这个队列只能绑定在这个链接上。
        // 第四个参数，表示是否队列为空时删除，一般结合第三个参数为ture使用true
        // 第五个参数为队列配置参数集合
        channel.queueDeclare("queue4", true, false, false, null);
//        channel.queueDeclare(ConnectionUtil.QUEUE_NAME, true, false, false, null);
        //交换机和队列绑定
        channel.queueBind("queue4","te", "direct.key");
//        channel.queueBind(ConnectionUtil.QUEUE_NAME, ConnectionUtil.EXCHANGE_NAME, "test");
//        channel.basicPublish(ConnectionUtil.EXCHANGE_NAME, "", null,
//                message.getBytes());
        for(int i = 0; i < 100; i++){
            channel.basicPublish("te", "direct.key", null,
                    (message + i).getBytes());
            System.out.println("发送的信息为:" + message + i);
        }

        channel.close();
        connection.close();
    }
}