package com.activitymq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by jinbiao.yao on 2019/6/25.
 * 持久化topic
 */
public class Produce_Topic_Persist {
    public static final String ACTIVEMQ_URL = "tcp://localhost:61616";
    public static final String TOPIC_NAME = "topic-persist";


    public static void main(String[] args) throws Exception {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = activeMQConnectionFactory.createConnection();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(TOPIC_NAME);

        MessageProducer messageProducer = session.createProducer(topic);
        messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);//设置持久化模式
        connection.start();
        for (int i = 1; i <= 3; i++) {
            TextMessage textMessage = session.createTextMessage("TOPIC_Persist---" + i);
            //`ltextMessage.setJMSExpiration(10000);
            //8 通过messageProducer发送给mq
            messageProducer.send(textMessage);
        }

        //9 关闭连接
        messageProducer.close();
        session.close();
        connection.close();
        System.out.println("********topic持久化消息发送到MQ成功");

    }
}
