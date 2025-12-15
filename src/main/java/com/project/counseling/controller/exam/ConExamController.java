package com.project.counseling.controller.exam;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.config.utils.ShiroUtils;
import com.project.counseling.domain.*;
import com.project.counseling.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 测评controller
 * @date 2025/11/19 11:28
 */
@Controller
@ResponseBody
@RequestMapping("exam")
public class ConExamController {

    @Autowired
    private ConExamService conExamService;
    @Autowired
    private ConExamTypeService conExamTypeService;
    @Autowired
    private ConResultService conResultService;
    @Autowired
    private ConProblemService conProblemService;
    @Autowired
    private ConAnalysisService conAnalysisService;

    /** 分页获取测评 */
    @PostMapping("getConExamPage")
    public Result getConExamPage(@RequestBody ConExam conExam) {
        User userInfo = ShiroUtils.getUserInfo();
        if (userInfo.getUserType() == 2) {
            conExam.setUserId(userInfo.getId());
        }
        Page<ConExam> page = new Page<>(conExam.getPageNumber(),conExam.getPageSize());
        QueryWrapper<ConExam> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(conExam.getTitle()),ConExam::getTitle,conExam.getTitle())
                .eq(StringUtils.isNotBlank(conExam.getTypeId()),ConExam::getTypeId,conExam.getTypeId())
                .eq(StringUtils.isNotBlank(conExam.getUserId()),ConExam::getUserId,conExam.getUserId())
                .orderByDesc(ConExam::getCreateTime);
        Page<ConExam> conExamPage = conExamService.page(page, queryWrapper);
        for (ConExam exam : conExamPage.getRecords()) {
            ConExamType examType = conExamTypeService.getById(exam.getTypeId());
            exam.setTypeId(examType.getName());
            QueryWrapper<ConResult> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(ConResult::getExamId,exam.getId());
            exam.setPeople(conResultService.count(wrapper));
        }
        return Result.success(conExamPage);
    }

    @GetMapping("getExamList")
    public Result getExamList() {
        User userInfo = ShiroUtils.getUserInfo();
        QueryWrapper<ConExam> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConExam::getUserId,userInfo.getId());
        return Result.success(conExamService.list(queryWrapper));
    }

    /** 根据id获取测评 */
    @GetMapping("getConExamById")
    public Result getConExamById(@RequestParam("id")String id) {
        ConExam conExam = conExamService.getById(id);
        QueryWrapper<ConProblem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConProblem::getExamId,conExam.getId());
        int count = conProblemService.count(queryWrapper);
        conExam.setCount(count);
        QueryWrapper<ConResult> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ConResult::getExamId,conExam.getId());
        conExam.setPeople(conResultService.count(wrapper));
        return Result.success(conExam);
    }

    /** 保存测评 */
    @PostMapping("saveConExam")
    public Result saveConExam(@RequestBody ConExam conExam) {
        User userInfo = ShiroUtils.getUserInfo();
        conExam.setUserId(userInfo.getId());
        boolean save = conExamService.save(conExam);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑测评 */
    @PostMapping("editConExam")
    public Result editConExam(@RequestBody ConExam conExam) {
        boolean save = conExamService.updateById(conExam);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除测评 */
    @GetMapping("removeConExam")
    public Result removeConExam(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conExamService.removeById(id);
                QueryWrapper<ConResult> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ConResult::getExamId,id);
                conResultService.remove(queryWrapper);
                QueryWrapper<ConProblem> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.lambda().eq(ConProblem::getExamId,id);
                conProblemService.remove(queryWrapper1);
                QueryWrapper<ConAnalysis> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.lambda().eq(ConAnalysis::getExamId,id);
                conAnalysisService.remove(queryWrapper2);
            }
            return Result.success();
        } else {
            return Result.fail("测评id不能为空！");
        }
    }

}
