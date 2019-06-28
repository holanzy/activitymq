package com.activitymq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by jinbiao.yao on 2019/6/25.
 * 持久化topic
 */
public class Comsumer_Topic_Persist {
    public static final String ACTIVEMQ_URL = "tcp://localhost:61616";
    public static final String TOPIC_NAME = "topic-persist";


    public static void main(String[] args) throws Exception {

        System.out.println("********张4");

        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.setClientID("atguigu1");//消费者id,不能重复

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(TOPIC_NAME);
        TopicSubscriber topicSubscriber = session.createDurableSubscriber(topic, "remark..123");
        connection.start();
        Message message = topicSubscriber.receive();
        while (message != null) {
            TextMessage textMessage = (TextMessage) message;
            System.out.println("*****收到持久化topic：" + textMessage.getText());
            message = topicSubscriber.receive(5000L);
            //手动签收Session.CLIENT_ACKNOWLEDGE模式，必须要调用acknowledge签收
            //message.acknowledge();
        }
        topicSubscriber.close();
        session.close();
        connection.close();

        /**
         * 1先运行一次消费者，向mq注册，类似我订阅了这个主题
         * 2然后在运行生产者，此时
         * 无论消费者是否启动，消费者启动后都会接受到。不在线的话，下次启动时会把没有收过的消息都收下来
         * 总结:订阅之前发的消息不会被消费，订阅之后的消息，无论发消息时消费者是否启动，消费者都可以消费
         */
    }
}