package com.wuqiong.tx.enums;

/**
 * @Description 远程事务状态
 * @Author Cain
 * @date 2020/11/6
 */
public enum RemoteTransactionStatus {

    /** 已创建 */
    CREATED(1),
    /** 已准备 */
    PREPARED(2),
    /** 事务异常 */
    ERROR(3);

    public int status;

    RemoteTransactionStatus(int status) {
        this.status = status;
    }
}
