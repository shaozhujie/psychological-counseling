package com.project.counseling.controller.result;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.config.utils.ShiroUtils;
import com.project.counseling.domain.*;
import com.project.counseling.service.ConAnalysisService;
import com.project.counseling.service.ConExamService;
import com.project.counseling.service.ConProblemService;
import com.project.counseling.service.ConResultService;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 测评结果controller
 * @date 2025/11/19 02:18
 */
@Controller
@ResponseBody
@RequestMapping("result")
public class ConResultController {

    @Autowired
    private ConResultService conResultService;
    @Autowired
    private ConAnalysisService conAnalysisService;
    @Autowired
    private ConProblemService conProblemService;
    @Autowired
    private ConExamService conExamService;

    /** 分页获取测评结果 */
    @PostMapping("getConResultPage")
    public Result getConResultPage(@RequestBody ConResult conResult) {
        User userInfo = ShiroUtils.getUserInfo();
        if (userInfo.getUserType() == 1) {
            conResult.setUserId(userInfo.getId());
        }
        Page<ConResult> page = new Page<>(conResult.getPageNumber(),conResult.getPageSize());
        QueryWrapper<ConResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(conResult.getExamId()),ConResult::getExamId,conResult.getExamId())
                .eq(StringUtils.isNotBlank(conResult.getUserId()),ConResult::getUserId,conResult.getUserId())
                .like(StringUtils.isNotBlank(conResult.getCreateBy()),ConResult::getCreateBy,conResult.getCreateBy());
        Page<ConResult> conResultPage = conResultService.page(page, queryWrapper);
        for (ConResult result : conResultPage.getRecords()) {
            result.setConExam(conExamService.getById(result.getExamId()));
        }
        return Result.success(conResultPage);
    }

    /** 根据id获取测评结果 */
    @GetMapping("getConResultById")
    public Result getConResultById(@RequestParam("id")String id) {
        ConResult conResult = conResultService.getById(id);
        return Result.success(conResult);
    }

    /** 保存测评结果 */
    @PostMapping("saveConResult")
    public Result saveConResult(@RequestBody ConResult conResult) {
        boolean save = conResultService.save(conResult);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑测评结果 */
    @PostMapping("editConResult")
    public Result editConResult(@RequestBody ConResult conResult) {
        boolean save = conResultService.updateById(conResult);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除测评结果 */
    @GetMapping("removeConResult")
    public Result removeConResult(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conResultService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("测评结果id不能为空！");
        }
    }

    @GetMapping("getResultByExamId")
    public Result getResultByExamId(@RequestParam("examId")String examId) {
        User userInfo = ShiroUtils.getUserInfo();
        QueryWrapper<ConResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConResult::getExamId,examId)
                .eq(ConResult::getUserId,userInfo.getId());
        int count = conResultService.count(queryWrapper);
        if (count > 0) {
            return Result.success(true);
        } else {
            return Result.success(false);
        }
    }

    @PostMapping("saveProblemResult")
    public Result saveProblemResult(@RequestBody JSONObject jsonObject) {
        JSONArray exam = jsonObject.getJSONArray("exam");
        List<ConProblem> conProblemList = exam.toJavaList(ConProblem.class);
        Integer score = 0;
        String examId = "";
        for (ConProblem conProblem : conProblemList) {
            examId = conProblem.getExamId();
            if (conProblem.getResult().equals(conProblem.getSelect1())) {
                score += conProblem.getScore1();
            }
            if (conProblem.getResult().equals(conProblem.getSelect2())) {
                score += conProblem.getScore2();
            }
            if (conProblem.getResult().equals(conProblem.getSelect3())) {
                score += conProblem.getScore3();
            }
            if (conProblem.getResult().equals(conProblem.getSelect4())) {
                score += conProblem.getScore4();
            }
        }
        ConResult conResult = new ConResult();
        conResult.setExamId(examId);
        conResult.setUserId(ShiroUtils.getUserInfo().getId());
        conResult.setScore(score);
        //判断分值区间
        QueryWrapper<ConAnalysis> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConAnalysis::getExamId,examId);
        List<ConAnalysis> conAnalyses = conAnalysisService.list(queryWrapper);
        for (ConAnalysis conAnalysis : conAnalyses) {
            if (score >= conAnalysis.getScoreStart() && score <= conAnalysis.getScoreEnd()) {
                conResult.setAnalysis(conAnalysis.getAnalysis());
                conResult.setAnalysisInfo(conAnalysis.getAnalysisInfo());
                conResult.setSuggest(conAnalysis.getSuggest());
                conResult.setIntroduce(conAnalysis.getIntroduce());
                break;
            }
        }
        boolean save = conResultService.save(conResult);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    @GetMapping("getResultByUserId")
    public Result getResultByUserId(@RequestParam("examId")String examId) {
        QueryWrapper<ConResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConResult::getExamId,examId)
                .eq(ConResult::getUserId,ShiroUtils.getUserInfo().getId());
        ConResult conResult = conResultService.getOne(queryWrapper);
        return Result.success(conResult);
    }

    @GetMapping("removeResultByExamId")
    public Result removeResultByExamId(@RequestParam("examId")String examId) {
        QueryWrapper<ConResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConResult::getExamId,examId)
                .eq(ConResult::getUserId,ShiroUtils.getUserInfo().getId());
        conResultService.remove(queryWrapper);
        return Result.success();
    }

}
