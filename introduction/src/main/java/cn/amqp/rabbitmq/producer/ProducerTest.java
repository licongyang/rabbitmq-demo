package cn.amqp.rabbitmq.producer;

import cn.amqp.rabbitmq.utils.ConnectionUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class ProducerTest {

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
        // 声明exchange
        channel.exchangeDelete("exchangeTest");
        channel.exchangeDeclare("exchangeTest", BuiltinExchangeType.FANOUT);
        //声明队列
        // 第二个参数表示队列是否持久化（跟消息持久化不同），
        // 第三个参数表示是否是排他队列，表示这个队列只能绑定在这个链接上。
        // 第四个参数，表示是否队列为空时删除，一般结合第三个参数为ture使用true
        // 第五个参数为队列配置参数集合
        channel.queueDeclare("queue1", true, false, false, null);
        channel.queueDeclare("queue2", true, false, false, null);
        channel.queueDeclare("queue3", true, false, false, null);
        //交换机和队列绑定
        //接受到3条
        channel.queueBind("queue1", "exchangeTest", "debug.*.B");
        //接受到9条
        channel.queueBind("queue2", "exchangeTest", "error.#");
        //接受到9条
        channel.queueBind("queue3", "exchangeTest", "*.email.*");
        String[] as = new String[]{"error","info","debug"};
        String[] bs = new String[]{"user","order","email"};
        String[] cs = new String[]{"A","B","C"};
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                for(int k = 0; k < 3; k++){
                    String mg = as[i] + "." + bs[j] + "." + cs[k];
                    System.out.println(mg);
                    channel.basicPublish("exchangeTest", mg, null,
                            mg.getBytes());
                }
            }
        }


        System.out.println("发送的信息为:" + message);
        channel.close();
        connection.close();
    }
}