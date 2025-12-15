package com.project.counseling.controller.problem;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.domain.ConProblem;
import com.project.counseling.domain.Result;
import com.project.counseling.service.ConProblemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 测评题目controller
 * @date 2025/11/19 01:49
 */
@Controller
@ResponseBody
@RequestMapping("problem")
public class ConProblemController {

    @Autowired
    private ConProblemService conProblemService;

    /** 分页获取测评题目 */
    @PostMapping("getConProblemPage")
    public Result getConProblemPage(@RequestBody ConProblem conProblem) {
        Page<ConProblem> page = new Page<>(conProblem.getPageNumber(),conProblem.getPageSize());
        QueryWrapper<ConProblem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(conProblem.getExamId()),ConProblem::getExamId,conProblem.getExamId())
                .like(StringUtils.isNotBlank(conProblem.getTitle()),ConProblem::getTitle,conProblem.getTitle());
        Page<ConProblem> conProblemPage = conProblemService.page(page, queryWrapper);
        return Result.success(conProblemPage);
    }

    @GetMapping("getConProblemByExamId")
    public Result getConProblemByExamId(@RequestParam("examId")String examId) {
        QueryWrapper<ConProblem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConProblem::getExamId,examId)
                .orderByAsc(ConProblem::getSort);
        return Result.success(conProblemService.list(queryWrapper));
    }

    /** 根据id获取测评题目 */
    @GetMapping("getConProblemById")
    public Result getConProblemById(@RequestParam("id")String id) {
        ConProblem conProblem = conProblemService.getById(id);
        return Result.success(conProblem);
    }

    /** 保存测评题目 */
    @PostMapping("saveConProblem")
    public Result saveConProblem(@RequestBody ConProblem conProblem) {
        boolean save = conProblemService.save(conProblem);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑测评题目 */
    @PostMapping("editConProblem")
    public Result editConProblem(@RequestBody ConProblem conProblem) {
        boolean save = conProblemService.updateById(conProblem);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除测评题目 */
    @GetMapping("removeConProblem")
    public Result removeConProblem(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conProblemService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("测评题目id不能为空！");
        }
    }

    @GetMapping("downloadExcel")
    public void downloadExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode("测评题目", "UTF-8")
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");
        List<ConProblem> conProblemList = new ArrayList<>();
        // 写入Excel
        EasyExcel.write(response.getOutputStream(), ConProblem.class)
                .sheet("题目")
                .doWrite(conProblemList);
    }

    @PostMapping("/import/{examId}")
    public Result importFromExcel(@PathVariable("examId") String examId,@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return Result.fail("上传文件不能为空");
        }
        // 校验文件类型
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            return Result.fail("只支持Excel文件格式（.xlsx 或 .xls）");
        }
        try {
            EasyExcel.read(file.getInputStream(), ConProblem.class, new ReadListener<ConProblem>() {
                @Override
                public void invoke(ConProblem data, AnalysisContext context) {
                    data.setExamId(examId);
                    System.out.println("解析到数据: " + data.toString());
                    conProblemService.save(data);
                }
                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    System.out.println("所有数据解析完成");
                }
            }).sheet().doRead();
            return Result.success();
        } catch (Exception e) {
            return Result.fail();
        }
    }

}
