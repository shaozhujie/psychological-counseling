package com.project.counseling.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 咨询预约
 * @date 2025/11/19 03:49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("con_appointment")
public class ConAppointment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 预约编号
     */
    private String orderNumber;

    /**
     * 咨询师id
     */
    private String consultId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 预约id
     */
    private String makeId;

    /**
     * 日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date slotDate;

    /**
     * 咨询开始时间
     */
    private String slotStart;

    /**
     * 咨询结束时间
     */
    private String slotEnd;

    /**
     * 地址
     */
    private String address;

    /**
     * 问题描述
     */
    private String content;

    /**
     * 姓名
     */
    private String name;

    /**
     * 联系方式
     */
    private String tel;

    /**
     * 咨询方式
     */
    private Integer type;

    private Integer price;

    private String starContent;

    private Float star;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 创建者
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新者
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(exist = false)
    private Integer pageNumber;

    @TableField(exist = false)
    private Integer pageSize;

    @TableField(exist = false)
    private User user;
}
