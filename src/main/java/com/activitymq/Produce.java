package com.activitymq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;


/**
 * Created by jinbiao.yao on 2019/6/20.
 */
public class Produce {

    public static final String ACTIVEMQ_URL = "tcp://localhost:61616";
    public static final String QUEUE_NAME = "queue01";


    public static void main(String[] args) throws Exception {

        //1 创建连接工厂，指定url,用户名密码默认admin，admin
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        //2 通过工厂连接，获得连接connection启动并访问
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();

        //3 创建会话session
        //第一个参数：事务，第二个参数：签收
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //4创建目的地，具体是队列还是topic
        //Destination是Queue的上级接口
        //Destination destination = session.createQueue(QUEUE_NAME);
        //此例用消息队列
        Queue queue = session.createQueue(QUEUE_NAME);

        //5 创建消息生产者
        MessageProducer messageProducer = session.createProducer(queue);
        messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);//设置是否持久化,默认持久化
        //6 通过messageProducer生产3条消息发送到MQ队列里面
        for (int i = 1; i <= 3; i++) {
            //7 创建TextMessage消息
            TextMessage textMessage = session.createTextMessage("msg---" + i);
            textMessage.setStringProperty("userName", "name" + i);//消息属性
            //8 通过messageProducer发送给mq
            messageProducer.send(textMessage);

            //创建mqp消息
            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setString("key1", "map的Value---------" + i);
            messageProducer.send(mapMessage);
        }
        //9 关闭连接
        messageProducer.close();
        session.close();
        connection.close();
        System.out.println("********消息发送到MQ成功");

    }
}
