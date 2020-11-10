package com.wuqiong.tx.transaction;

import com.google.gson.Gson;
import com.wuqiong.tx.bean.RemoteTransactionInfo;
import com.wuqiong.tx.context.ContextHolder;
import com.wuqiong.tx.enums.RemoteTransactionStatus;
import com.wuqiong.tx.util.MqProducerUtils;
import com.wuqiong.tx.util.RemoteTransactionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.JdbcTransactionObjectSupport;
import org.springframework.transaction.*;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

/**
 * @Description 自定义事务管理器
 * @Author Cain
 * @date 2020/9/9
 */
public class MyTransactionManager implements PlatformTransactionManager, Serializable {

    private DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
        MyDataSourceTransactionObject txInfo = doGetTransaction();
        String localTransactionID = ContextHolder.getLocalTransactionID();
        if (StringUtils.hasText(ContextHolder.getMainTransactionID())
                && RemoteTransactionUtils.isMainTransaction(ContextHolder.getMainTransactionID())) {
            if (localTransactionID == null) {
                localTransactionID = UUID.randomUUID().toString();
                // 存在远程事务,将本地事务状态更新到redis
                RemoteTransactionUtils.addSubTransaction(ContextHolder.getMainTransactionID(), localTransactionID);
            }
        } else {
            // 不存在远程事务将本地事务作为主事务更新到redis
            if (localTransactionID == null) {
                localTransactionID = UUID.randomUUID().toString();
                ContextHolder.setLocalTransactionID(localTransactionID);
                RemoteTransactionUtils.createMainTransaction(localTransactionID);
            }
        }
        if (definition == null) {
            // 使用默认的事务定义,默认事务传播机制PROPAGATION_REQUIRED,默认事务隔离级别使用数据库事务隔离级别
            definition = new DefaultTransactionDefinition();
        }
        if (isExistingTransaction(txInfo)) {
            return handleExistTransaction(txInfo);
        }
        if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRED) {
            // 开启一个事务
            doBegin(txInfo, definition);
            DefaultTransactionStatus status = new DefaultTransactionStatus(txInfo, true, false, false, true, null);
            return status;
        } else {
            // 创建一个空事务
            return newTransactionStatus(definition, null, true, true, true, null);
        }
    }

    @Override
    public void commit(TransactionStatus status) throws TransactionException {
        MyTransactionManager.MyDataSourceTransactionObject txObject =
                (MyTransactionManager.MyDataSourceTransactionObject) ((DefaultTransactionStatus)status).getTransaction();
        Connection con = txObject.getConnectionHolder().getConnection();
        try {
            if (StringUtils.hasText(ContextHolder.getLocalTransactionID()) &&
                    RemoteTransactionUtils.isMainTransaction(ContextHolder.getLocalTransactionID())) {
                // 有远程事务,并且是主事务
                RemoteTransactionInfo remoteTransactionInfo = RemoteTransactionUtils.getRemoteTransaction(ContextHolder.getLocalTransactionID());
                if (remoteTransactionInfo != null) {
                    // 检查所有子事务状态
                    if (!RemoteTransactionUtils.existErrorSubTransaction(remoteTransactionInfo)) {
                        // 通知其他子事务提交事务
                        SendResult sendResult = MqProducerUtils.commitNotify(ContextHolder.getLocalTransactionID());
                        System.out.println("通知子事务提交,发送结果:"+new Gson().toJson(sendResult));
                        // todo 检查消息处理结果
                        con.commit();
                    } else {
                        // 通知其他子事务回滚
                        SendResult sendResult = MqProducerUtils.rollbackNotify(ContextHolder.getLocalTransactionID());
                        System.out.println("通知子事务回滚,发送结果:"+new Gson().toJson(sendResult));
                        con.rollback();
                    }
                }
            } else if (StringUtils.hasText(ContextHolder.getMainTransactionID())) {
                // 有远程事务,并且是子事务
                // 标记本事务为可提交状态
                RemoteTransactionUtils.updateRemoteTransactoinStatus(ContextHolder.getMainTransactionID(),
                        ContextHolder.getLocalTransactionID(), RemoteTransactionStatus.PREPARED.status);
                // 挂起事务等待主事务通知提交或回滚
                DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("commit");
                consumer.setNamesrvAddr("114.116.237.83:9876");
                consumer.setInstanceName(UUID.randomUUID().toString());
                consumer.setVipChannelEnabled(false);
                try {
                    consumer.subscribe(ContextHolder.getMainTransactionID(), "*");
                    consumer.registerMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
                        MessageExt message = list.iterator().next();
                        String topic = message.getTopic();
                        byte[] body = message.getBody();
                        String keys = message.getKeys();
                        String tags = message.getTags();
                        String action = new String(message.getBody());
                        try {
                            if ("commit".equals(action)) {
                                System.out.println("子事务提交,topic:"+topic+",body:"+new String(body)+",keys:"+keys+",tags:"+tags);
                                con.commit();
                            } else {
                                System.out.println("子事务回滚,topic:"+topic+",body:"+new String(body)+",keys:"+keys+",tags:"+tags);
                                con.rollback();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    });
                    consumer.start();
                    System.out.println("Consumer Started.%n");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (status.isNewTransaction()) {
                con.commit();
            }
        } catch (SQLException ex) {
            throw new TransactionSystemException("Could not commit JDBC transaction", ex);
        }
    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {
        MyTransactionManager.MyDataSourceTransactionObject txObject =
                (MyTransactionManager.MyDataSourceTransactionObject) ((DefaultTransactionStatus)status).getTransaction();
        Connection con = txObject.getConnectionHolder().getConnection();
        try {
            if (StringUtils.hasText(ContextHolder.getLocalTransactionID())
                    && RemoteTransactionUtils.isMainTransaction(ContextHolder.getLocalTransactionID())) {
                // 有远程事务,且是主事务
                // 通知所有子事务回滚
                SendResult sendResult = MqProducerUtils.rollbackNotify(ContextHolder.getLocalTransactionID());
                System.out.println("通知子事务回滚,发送结果:"+new Gson().toJson(sendResult));
                con.rollback();
            } else if (StringUtils.hasText(ContextHolder.getMainTransactionID())) {
                // 有远程事务,且是子事务
                // 标记本事务为异常状态
                RemoteTransactionUtils.updateRemoteTransactoinStatus(ContextHolder.getMainTransactionID(),
                        ContextHolder.getLocalTransactionID(), RemoteTransactionStatus.ERROR.status);
                con.rollback();
            } else {
                con.rollback();
            }
        }
        catch (SQLException ex) {
            throw new TransactionSystemException("Could not roll back JDBC transaction", ex);
        }
    }

    /**
     * 获取事务连接
     * @return
     */
    private MyDataSourceTransactionObject doGetTransaction() {
        MyDataSourceTransactionObject txObject = new MyDataSourceTransactionObject();
        ConnectionHolder conHolder = (ConnectionHolder) TransactionSynchronizationManager.getResource(this.dataSource);
        txObject.setConnectionHolder(conHolder);
        txObject.setNewConnectionHolder(conHolder == null);
        return txObject;
    }

    /**
     * 创建事务状态
     * @param definition 事务定义
     * @param transaction 已存在的事务
     * @param newTransaction 是否开启新事务
     * @param newSynchronization
     * @param debug 是否开启debug级别日志
     * @param suspendedResources
     * @return
     */
    private DefaultTransactionStatus newTransactionStatus(
            TransactionDefinition definition, Object transaction, boolean newTransaction,
            boolean newSynchronization, boolean debug, Object suspendedResources) {

        boolean actualNewSynchronization = newSynchronization &&
                !TransactionSynchronizationManager.isSynchronizationActive();
        return new DefaultTransactionStatus(
                transaction, newTransaction, actualNewSynchronization,
                definition.isReadOnly(), debug, suspendedResources);
    }

    /**
     * 开始事务
     * @param txObject 事务对象
     * @param definition 事务定义
     */
    private void doBegin(MyDataSourceTransactionObject txObject, TransactionDefinition definition) {
        if (txObject.getConnectionHolder() == null ||
                txObject.getConnectionHolder().isSynchronizedWithTransaction()) {
            Connection newCon = null;
            try {
                newCon = this.dataSource.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            txObject.setConnectionHolder(new ConnectionHolder(newCon));
        }
        Connection con = txObject.getConnectionHolder().getConnection();
        try {
            con.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (txObject.isNewConnectionHolder()) {
            // 绑定事务到当前线程
            TransactionSynchronizationManager.bindResource(getDataSource(), txObject.getConnectionHolder());
        }
    }

    /**
     * 是否已存在事务
     * @param transaction
     * @return
     */
    private boolean isExistingTransaction(MyDataSourceTransactionObject transaction) {
//        String transactionID = ContextHolder.getLocalTransactionID();
        return (transaction.getConnectionHolder() != null);
//        return StringUtils.hasText(transactionID);
    }

    /**
     * 处理已经存在事务的情况
     * @param txInfo 事务信息
     * @return
     */
    private DefaultTransactionStatus handleExistTransaction(MyDataSourceTransactionObject txInfo ) {
        DefaultTransactionStatus status = new DefaultTransactionStatus(txInfo, false, false, false, true, null);
        return status;
    }

    /**
     * 自定义数据源事务对象
     */
    private static class MyDataSourceTransactionObject extends JdbcTransactionObjectSupport {

        private boolean newConnectionHolder;

        public boolean isNewConnectionHolder() {
            return newConnectionHolder;
        }

        public void setNewConnectionHolder(boolean newConnectionHolder) {
            this.newConnectionHolder = newConnectionHolder;
        }

        @Override
        public boolean isRollbackOnly() {
            return false;
        }
    }
}
