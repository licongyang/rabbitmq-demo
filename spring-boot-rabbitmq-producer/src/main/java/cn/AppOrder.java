package cn;

import cn.zeus.ProducerUtil.RabbitmqMessageSend;
import cn.zeus.config.RabbitmqConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class AppOrder {
    public static void main(String[] args) {
        SpringApplication.run(AppOrder.class);
        //模拟发送消息时，关闭连接，查看发送方确认机制
//        AnnotationConfigApplicationContext annotationConfigApplicationContext =
//                new AnnotationConfigApplicationContext(RabbitmqConfig.class);
//        RabbitmqMessageSend bean = annotationConfigApplicationContext.getBean(RabbitmqMessageSend.class);
//        bean.sendMessage("hello","direct.key","directExchage");
//        annotationConfigApplicationContext.close();

    }
}
