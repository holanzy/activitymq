package com.activitymq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.concurrent.TimeUnit;


/**
 * Created by jinbiao.yao on 2019/6/20.
 * 事务
 */
public class Produce_TX {

    public static final String ACTIVEMQ_URL = "tcp://localhost:61616";
    public static final String QUEUE_NAME = "queue01";


    public static void main(String[] args) throws Exception {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();

        //true:开启事务
        Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(QUEUE_NAME);

        MessageProducer messageProducer = session.createProducer(queue);
        for (int i = 1; i <= 3; i++) {
            TextMessage textMessage = session.createTextMessage("ts msg---" + i);
            textMessage.setStringProperty("userName", "name" + i);//消息属性
            messageProducer.send(textMessage);
        }
        //如果开启事务，需要commit，不开启事务不需要执行commit
        TimeUnit.SECONDS.sleep(3);
        session.commit();
        //9 关闭连接
        messageProducer.close();
        session.close();
        connection.close();
        System.out.println("********tx消息发送到MQ成功");

        try {
            //正常情况 session.commit();
        } catch (Exception e) {
            e.printStackTrace();
            //异常情况,回滚事务
            session.rollback();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
