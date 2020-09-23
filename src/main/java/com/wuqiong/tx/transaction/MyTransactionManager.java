package com.wuqiong.tx.transaction;

import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.JdbcTransactionObjectSupport;
import org.springframework.transaction.*;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

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
            if (status.isNewTransaction()) {
                con.commit();
            }
        }
        catch (SQLException ex) {
            throw new TransactionSystemException("Could not commit JDBC transaction", ex);
        }
    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {
        MyTransactionManager.MyDataSourceTransactionObject txObject =
                (MyTransactionManager.MyDataSourceTransactionObject) ((DefaultTransactionStatus)status).getTransaction();
        Connection con = txObject.getConnectionHolder().getConnection();
        try {
            con.rollback();
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
        return (transaction.getConnectionHolder() != null);
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
