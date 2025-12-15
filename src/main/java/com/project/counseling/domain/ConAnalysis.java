package com.project.counseling.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 分值解析
 * @date 2025/11/19 02:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("con_analysis")
public class ConAnalysis implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 测评id
     */
    private String examId;

    /**
     * 最低分
     */
    private Integer scoreStart;

    /**
     * 最高分
     */
    private Integer scoreEnd;

    /**
     * 解析
     */
    private String analysis;

    /**
     * 详细解析
     */
    private String analysisInfo;

    private String introduce;

    /**
     * 成长建议
     */
    private String suggest;

    @TableField(exist = false)
    private Integer pageNumber;

    @TableField(exist = false)
    private Integer pageSize;
}
