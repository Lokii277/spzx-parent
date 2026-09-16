package com.aki.spzx.manager.log.service;

import com.aki.spzx.manager.mapper.SysBusinessLogMapper;
import com.aki.spzx.model.entity.system.SysBusinessLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BusinessLogService  {
    @Autowired
    private SysBusinessLogMapper businessLogMapper;
    /**
     * 异步保存业务日志
     * 注意：方法必须是 public，且通过 Spring 注入的代理对象调用
     */
    @Async("businessLogExecutor")
    public void saveLog(SysBusinessLog businessLog) {
        // 异步方法必须try catch 避免影响主业务
        try {
            businessLogMapper.insert(businessLog);
        } catch (Exception e) {
            // 日志保存失败不能影响主业务，只记录错误
            System.out.println("保存业务日志失败：" + e.getMessage());
        }
    }
}
