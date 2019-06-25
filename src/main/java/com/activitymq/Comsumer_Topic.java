package com.activitymq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by jinbiao.yao on 2019/6/25.
 */
public class Comsumer_Topic {
    public static final String ACTIVEMQ_URL = "tcp://localhost:61616";
    public static final String TOPIC_NAME = "topic-atguigu";


    public static void main(String[] args) throws Exception {

        System.out.println("********我是3号消费者");

        //1 创建连接工厂，指定url,用户名密码默认admin，admin
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        //2 通过工厂连接，获得连接connection启动并访问
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();

        //3 创建会话session
        //第一个参数：事务，第二个参数：签收
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //此例用消息队列
        Topic topic = session.createTopic(TOPIC_NAME);
        //5 创建消费者
        MessageConsumer messageConsumer = session.createConsumer(topic);
        messageConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (message != null && message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        System.out.println("*******消费者监听模式:" + textMessage.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        System.in.read();
        messageConsumer.close();
        session.close();
        connection.close();

    }
}