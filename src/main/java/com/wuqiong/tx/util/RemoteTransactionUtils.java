package com.wuqiong.tx.util;

import com.google.gson.Gson;
import com.wuqiong.tx.bean.RemoteTransactionInfo;
import com.wuqiong.tx.context.ApplicationContextHelper;
import com.wuqiong.tx.enums.RemoteTransactionStatus;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description redis事务工具
 * @Author Cain
 * @date 2020/11/6
 */
public class RemoteTransactionUtils {

    /**
     * 创建远程主事务
     * @param mainTransactionID
     */
    public static void createMainTransaction(String mainTransactionID) {
        RemoteTransactionInfo txInfo = createTransaction(mainTransactionID, true);
        StringRedisTemplate template = ApplicationContextHelper
                .getBeanByNameAndType("stringRedisTemplate", StringRedisTemplate.class);
        template.opsForValue().set(mainTransactionID, new Gson().toJson(txInfo));
    }

    /**
     * 增加子事务
     * @param mainTransactionID 主事务ID
     * @param subTransactionID 子事务ID
     */
    public static void addSubTransaction(String mainTransactionID, String subTransactionID) {
        RemoteTransactionInfo remoteTx = getRemoteTransaction(mainTransactionID);
        RemoteTransactionInfo subTx = createTransaction(subTransactionID, false);
        if (remoteTx.getSubTransactionInfoList() == null) {
            List<RemoteTransactionInfo> subTxList = new ArrayList<>();
            subTxList.add(subTx);
            remoteTx.setSubTransactionInfoList(subTxList);
        } else {
            remoteTx.getSubTransactionInfoList().add(subTx);
        }
        StringRedisTemplate template = ApplicationContextHelper
                .getBeanByNameAndType("stringRedisTemplate", StringRedisTemplate.class);
        template.opsForValue().set(mainTransactionID, new Gson().toJson(remoteTx));
    }

    /**
     * 更新远程事务状态
     * @param mainTransactionID
     * @param transactionID
     * @param status
     */
    public static void updateRemoteTransactoinStatus(String mainTransactionID, String transactionID, int status) {
        RemoteTransactionInfo remoteTx = getRemoteTransaction(mainTransactionID);
        if (remoteTx.getTransactionID().equals(transactionID)) {
            remoteTx.setStatus(status);
        } else {
            for (RemoteTransactionInfo subTx : remoteTx.getSubTransactionInfoList()) {
                if (subTx.getTransactionID().equals(transactionID)) {
                    subTx.setStatus(status);
                }
            }
        }
        StringRedisTemplate template = ApplicationContextHelper
                .getBeanByNameAndType("stringRedisTemplate", StringRedisTemplate.class);
        template.opsForValue().set(mainTransactionID, new Gson().toJson(remoteTx));
    }

    /**
     * 获取远程事务
     * @param mainTransactionID
     * @return
     */
    public static RemoteTransactionInfo getRemoteTransaction(String mainTransactionID) {
        StringRedisTemplate template = ApplicationContextHelper
                .getBeanByNameAndType("stringRedisTemplate", StringRedisTemplate.class);
        String remoteTxStr = template.opsForValue().get(mainTransactionID);
        RemoteTransactionInfo remoteTx = new Gson().fromJson(remoteTxStr, RemoteTransactionInfo.class);
        return remoteTx;
    }

    /**
     * 是否是主事务
     * @param transactionID
     * @return
     */
    public static boolean isMainTransaction(String transactionID) {
        StringRedisTemplate template = ApplicationContextHelper
                .getBeanByNameAndType("stringRedisTemplate", StringRedisTemplate.class);
        String remoteTxStr = template.opsForValue().get(transactionID);
        return StringUtils.hasText(remoteTxStr);
    }

    /**
     * 是否存在异常的子事务
     * @param tx
     * @return
     */
    public static boolean existErrorSubTransaction(RemoteTransactionInfo tx) {
        if (tx == null) return false;
        if (CollectionUtils.isEmpty(tx.getSubTransactionInfoList())) return false;
        for (RemoteTransactionInfo subTx : tx.getSubTransactionInfoList()) {
            if (subTx == null) continue;
            if (subTx.getStatus() == RemoteTransactionStatus.ERROR.status) return true;
        }
        return false;
    }

    /**
     * 创建主事务
     * @param transactionID
     * @param isMain
     * @return
     */
    private static RemoteTransactionInfo createTransaction(String transactionID, boolean isMain) {
        RemoteTransactionInfo transactionInfo = new RemoteTransactionInfo();
        transactionInfo.setTransactionID(transactionID);
        transactionInfo.setMain(isMain);
        transactionInfo.setStatus(RemoteTransactionStatus.CREATED.status);
        return transactionInfo;
    }
}
