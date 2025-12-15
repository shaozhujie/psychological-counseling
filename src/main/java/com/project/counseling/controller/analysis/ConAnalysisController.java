package com.project.counseling.controller.analysis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.domain.ConAnalysis;
import com.project.counseling.domain.Result;
import com.project.counseling.service.ConAnalysisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 分值解析controller
 * @date 2025/11/19 02:02
 */
@Controller
@ResponseBody
@RequestMapping("analysis")
public class ConAnalysisController {

    @Autowired
    private ConAnalysisService conAnalysisService;

    /** 分页获取分值解析 */
    @PostMapping("getConAnalysisPage")
    public Result getConAnalysisPage(@RequestBody ConAnalysis conAnalysis) {
        Page<ConAnalysis> page = new Page<>(conAnalysis.getPageNumber(),conAnalysis.getPageSize());
        QueryWrapper<ConAnalysis> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(conAnalysis.getExamId()),ConAnalysis::getExamId,conAnalysis.getExamId());
        Page<ConAnalysis> conAnalysisPage = conAnalysisService.page(page, queryWrapper);
        return Result.success(conAnalysisPage);
    }

    /** 根据id获取分值解析 */
    @GetMapping("getConAnalysisById")
    public Result getConAnalysisById(@RequestParam("id")String id) {
        ConAnalysis conAnalysis = conAnalysisService.getById(id);
        return Result.success(conAnalysis);
    }

    /** 保存分值解析 */
    @PostMapping("saveConAnalysis")
    public Result saveConAnalysis(@RequestBody ConAnalysis conAnalysis) {
        boolean save = conAnalysisService.save(conAnalysis);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑分值解析 */
    @PostMapping("editConAnalysis")
    public Result editConAnalysis(@RequestBody ConAnalysis conAnalysis) {
        boolean save = conAnalysisService.updateById(conAnalysis);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除分值解析 */
    @GetMapping("removeConAnalysis")
    public Result removeConAnalysis(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conAnalysisService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("分值解析id不能为空！");
        }
    }

}
