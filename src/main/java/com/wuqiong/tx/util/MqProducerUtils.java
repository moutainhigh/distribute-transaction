package com.wuqiong.tx.util;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.UUID;

/**
 * @Description 消息队列生产者工具类
 * @Author Cain
 * @date 2020/11/10
 */
public class MqProducerUtils {

    /**
     * 提交事务通知
     * @param topic
     * @return
     * @throws RuntimeException
     */
    public static SendResult commitNotify(String topic) throws RuntimeException {
        try {
            DefaultMQProducer producer = new DefaultMQProducer("notify");
            producer.setInstanceName(UUID.randomUUID().toString());
            producer.setNamesrvAddr("114.116.237.83:9876");
            producer.setVipChannelEnabled(false);
            producer.start();
            Message msg = new Message(topic, "TagA", ("commit").getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult result = producer.send(msg);
            producer.shutdown();
            return result;
        } catch (Exception e) {
            System.out.println("通知事务提交出错");
            throw new RuntimeException("通知事务提交出错");
        }
    }

    /**
     * 回滚事务通知
     * @param topic
     * @return
     * @throws RuntimeException
     */
    public static SendResult rollbackNotify(String topic) throws RuntimeException {
        try {
            DefaultMQProducer producer = new DefaultMQProducer("notify");
            producer.setInstanceName(UUID.randomUUID().toString());
            producer.setNamesrvAddr("114.116.237.83:9876");
            producer.setVipChannelEnabled(false);
            producer.start();
            Message msg = new Message(topic, "TagA", ("rollback").getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult result = producer.send(msg);
            producer.shutdown();
            return result;
        } catch (Exception e) {
            System.out.println("通知事务回滚出错");
            throw new RuntimeException("通知事务回滚出错");
        }
    }


}
