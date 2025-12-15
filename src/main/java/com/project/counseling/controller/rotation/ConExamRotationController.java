package com.project.counseling.controller.rotation;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.domain.ConExamRotation;
import com.project.counseling.domain.Result;
import com.project.counseling.service.ConExamRotationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 测评轮播图controller
 * @date 2025/11/19 10:47
 */
@Controller
@ResponseBody
@RequestMapping("rotation")
public class ConExamRotationController {

    @Autowired
    private ConExamRotationService conExamRotationService;

    /** 分页获取测评轮播图 */
    @PostMapping("getConExamRotationPage")
    public Result getConExamRotationPage(@RequestBody ConExamRotation conExamRotation) {
        Page<ConExamRotation> page = new Page<>(conExamRotation.getPageNumber(),conExamRotation.getPageSize());
        QueryWrapper<ConExamRotation> queryWrapper = new QueryWrapper<>();
        Page<ConExamRotation> conExamRotationPage = conExamRotationService.page(page, queryWrapper);
        return Result.success(conExamRotationPage);
    }

    @GetMapping("getConExamRotationList")
    public Result getConExamRotationList() {
        List<ConExamRotation> list = conExamRotationService.list();
        return Result.success(list);
    }

    /** 根据id获取测评轮播图 */
    @GetMapping("getConExamRotationById")
    public Result getConExamRotationById(@RequestParam("id")String id) {
        ConExamRotation conExamRotation = conExamRotationService.getById(id);
        return Result.success(conExamRotation);
    }

    /** 保存测评轮播图 */
    @PostMapping("saveConExamRotation")
    public Result saveConExamRotation(@RequestBody ConExamRotation conExamRotation) {
        boolean save = conExamRotationService.save(conExamRotation);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑测评轮播图 */
    @PostMapping("editConExamRotation")
    public Result editConExamRotation(@RequestBody ConExamRotation conExamRotation) {
        boolean save = conExamRotationService.updateById(conExamRotation);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除测评轮播图 */
    @GetMapping("removeConExamRotation")
    public Result removeConExamRotation(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conExamRotationService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("测评轮播图id不能为空！");
        }
    }

}
