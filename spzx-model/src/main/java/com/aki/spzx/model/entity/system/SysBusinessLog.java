package com.aki.spzx.model.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@TableName("sys_business_log")
@Schema(description = "系统业务日志实体类")
public class SysBusinessLog {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 业务模块 */
    private String module;

    /** 操作名称 */
    private String operation;

    /** 业务类型（INSERT/UPDATE/DELETE等） */
    private String businessType;

    /** 业务主键ID */
    private String businessId;

    /** 操作人ID */
    private Long operUserId;

    /** 操作人用户名 */
    private String operUserName;

    /** 操作时间 */
    private Date operTime;

    /** 操作IP */
    private String operIp;

    /** 操作状态（1成功 0失败） */
    private Integer status;

    /** 错误信息 */
    private String errorMsg;

    /** 耗时（毫秒） */
    private Long costTime;
}