package com.activitymq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by jinbiao.yao on 2019/6/21.
 * 事务
 */
public class Consumer_TX {

    public static final String ACTIVEMQ_URL = "tcp://localhost:61616";
    public static final String QUEUE_NAME = "queue01";


    public static void main(String[] args) throws Exception {

        System.out.println("********我是1号消费者");

        //1 创建连接工厂，指定url,用户名密码默认admin，admin
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);

        Queue queue = session.createQueue(QUEUE_NAME);
        //5 创建消费者
        MessageConsumer messageConsumer = session.createConsumer(queue);

        while (true) {
            //receive没有消息时会一直等待，receive(1000L):等待1秒
            TextMessage message = (TextMessage) messageConsumer.receive(4000);
            if (message != null) {
                System.out.println("***********消费者受到消息:" + message.getText());
            } else {
                break;
            }
        }
        //消费者开启事务，如果不commit,消费者会重复消费
        session.commit();
        messageConsumer.close();
        session.close();
        connection.close();

        /**
         * 1.先生产，启动一个生产者，一个消费者，1号消费者可以正常消费
         * 2.先生产，先启动1号，再2号消费者，1号消费者能消费，2号消费者不能消费
         * 3.先启动3个消费者，在生产6条消息
         *   问题：
         *   1:1,2号都消费6条
         *   2：先到先得，6条全给第一个启动的
         *   3：一人一半
         *   答案：轮询，
         *   1,4
         *   2,5
         *   3,6
         *   一个一个轮询分配
         * 4.MQ挂了，那么消息的持久化和丢失情况如何？
         */
    }
}
