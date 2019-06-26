package com.activitymq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by jinbiao.yao on 2019/6/21.
 */
public class Consumer {

    public static final String ACTIVEMQ_URL = "tcp://localhost:61616";
    public static final String QUEUE_NAME = "queue01";


    public static void main(String[] args) throws Exception {

        System.out.println("********我是1号消费者");

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
        //5 创建消费者
        MessageConsumer messageConsumer = session.createConsumer(queue);

        /**
         * 1.同步阻塞方式receive
         * 订阅者或接收者调用receive方法接收消息，没有消息到来前会一直阻塞
         * */
        /*while (true) {
            //receive没有消息时会一直等待，receive(1000L):等待1秒
            TextMessage message = (TextMessage) messageConsumer.receive();
            if (message != null) {
                System.out.println("***********消费者受到消息:" + message.getText());
            } else{
                break;
            }
        }
        messageConsumer.close();
        session.close();
        connection.close();*/

        /**
         * 2.监听方式
         * */

        messageConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                //TextMessage
                if (message != null && message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        System.out.println("*******消费者监听模式接收string:" + textMessage.getText());
                        System.out.println("*******消费者接收到消息属性:" + textMessage.getStringProperty("userName"));//读取属性
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
                //MapMessage
                if (message != null && message instanceof MapMessage) {
                    MapMessage mapMessage = (MapMessage) message;
                    try {
                        System.out.println("*******消费者监听模式接收map:" + mapMessage.getString("key1"));
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
