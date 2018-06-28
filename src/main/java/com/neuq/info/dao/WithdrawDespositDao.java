package com.neuq.info.dao;

import com.neuq.info.entity.WithdrawDeposit;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Lin Dexiang
 * @date 2018/6/28
 */

@Repository
public interface WithdrawDespositDao {

    void insertWithdrawDesposit(@Param("withdrawDesposit") WithdrawDeposit withdrawDesposit);

}
