package com.project.counseling.domain;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.enums.BooleanEnum;
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
 * @description: 测评题目
 * @date 2025/11/19 01:49
 */
@Data
@TableName("con_problem")
@HeadRowHeight(30) // 定义Excel的表头行高
@ContentRowHeight(20) // 定义Excel的内容行高
@HeadFontStyle(fontHeightInPoints = 11,bold = BooleanEnum.TRUE)
public class ConProblem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ExcelIgnore
    private String id;

    /**
     * 测评id
     */
    @ExcelIgnore
    private String examId;

    /**
     * 题目
     */
    @ExcelProperty(value = "题目", index = 1)
    @ColumnWidth(60)
    private String title;

    /**
     * 选项1
     */
    @ExcelProperty(value = "选项1", index = 2)
    @ColumnWidth(20)
    private String select1;

    /**
     * 选项2
     */
    @ExcelProperty(value = "选项2", index = 4)
    @ColumnWidth(20)
    private String select2;

    /**
     * 选项3
     */
    @ExcelProperty(value = "选项3", index = 6)
    @ColumnWidth(20)
    private String select3;

    /**
     * 选项4
     */
    @ExcelProperty(value = "选项4", index = 8)
    @ColumnWidth(20)
    private String select4;

    /**
     * 分值
     */
    @ExcelProperty(value = "分值", index = 3)
    @ColumnWidth(20)
    private Integer score1;

    /**
     * 分值
     */
    @ExcelProperty(value = "分值", index = 5)
    @ColumnWidth(20)
    private Integer score2;

    /**
     * 分值
     */
    @ExcelProperty(value = "分值", index = 7)
    @ColumnWidth(20)
    private Integer score3;

    /**
     * 分值
     */
    @ExcelProperty(value = "分值", index = 9)
    @ColumnWidth(20)
    private Integer score4;

    /**
     * 序号
     */
    @ExcelProperty(value = "序号", index = 0)
    @ColumnWidth(20)
    private Integer sort;

    @TableField(exist = false)
    @ExcelIgnore
    private Integer pageNumber;

    @TableField(exist = false)
    @ExcelIgnore
    private Integer pageSize;

    @TableField(exist = false)
    @ExcelIgnore
    private String result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSelect1() {
        return select1;
    }

    public void setSelect1(String select1) {
        this.select1 = select1;
    }

    public String getSelect2() {
        return select2;
    }

    public void setSelect2(String select2) {
        this.select2 = select2;
    }

    public String getSelect3() {
        return select3;
    }

    public void setSelect3(String select3) {
        this.select3 = select3;
    }

    public String getSelect4() {
        return select4;
    }

    public void setSelect4(String select4) {
        this.select4 = select4;
    }

    public Integer getScore1() {
        return score1;
    }

    public void setScore1(Integer score1) {
        this.score1 = score1;
    }

    public Integer getScore2() {
        return score2;
    }

    public void setScore2(Integer score2) {
        this.score2 = score2;
    }

    public Integer getScore3() {
        return score3;
    }

    public void setScore3(Integer score3) {
        this.score3 = score3;
    }

    public Integer getScore4() {
        return score4;
    }

    public void setScore4(Integer score4) {
        this.score4 = score4;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
