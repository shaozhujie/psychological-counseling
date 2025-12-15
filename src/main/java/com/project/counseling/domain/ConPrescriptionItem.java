package com.project.counseling.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 处方药品
 * @date 2025/11/20 10:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("con_prescription_item")
public class ConPrescriptionItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 药品id
     */
    private String medicineId;

    /**
     * 名称
     */
    private String name;

    /**
     * 数量
     */
    private Integer stock;

    /**
     * 处方id
     */
    private String prescriptionId;

    private Float unit;

    /**
     * 金额
     */
    private Float price;

    private String image;

    @TableField(exist = false)
    private Integer pageNumber;

    @TableField(exist = false)
    private Integer pageSize;
}
