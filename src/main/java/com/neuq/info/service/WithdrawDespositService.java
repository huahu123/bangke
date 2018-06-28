package com.neuq.info.service;


import com.neuq.info.dao.WithdrawDespositDao;
import com.neuq.info.entity.WithdrawDeposit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WithdrawDespositService {

    @Autowired
    private WithdrawDespositDao withdrawDespositDao;

    public void insertWithdrawDesposit(WithdrawDeposit withdrawDesposit) {
        withdrawDespositDao.insertWithdrawDesposit(withdrawDesposit);
    }

}


