package com.project.counseling.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 咨询师个人设置
 * @date 2025/11/14 05:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("con_set")
public class ConSet implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户id
     */
    private String userId;

    private String brief;

    /**
     * 个人简介
     */
    private String introduce;

    /**
     * 相册
     */
    private String image;

    /**
     * 写给来访者
     */
    private String autograph;

    @TableField(exist = false)
    private String types;

    /**
     * 咨询经验
     */
    private Integer experience;

    /**
     * 咨询费用
     */
    private Integer price;

    private String notice;

    @TableField(exist = false)
    private Integer pageNumber;

    @TableField(exist = false)
    private Integer pageSize;
}
