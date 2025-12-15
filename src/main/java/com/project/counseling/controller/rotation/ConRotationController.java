package com.project.counseling.controller.rotation;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.domain.ConRotation;
import com.project.counseling.domain.Result;
import com.project.counseling.service.ConRotationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 轮播图controller
 * @date 2025/11/14 10:38
 */
@Controller
@ResponseBody
@RequestMapping("rotation")
public class ConRotationController {

    @Autowired
    private ConRotationService conRotationService;

    /** 分页获取轮播图 */
    @PostMapping("getConRotationPage")
    public Result getConRotationPage(@RequestBody ConRotation conRotation) {
        Page<ConRotation> page = new Page<>(conRotation.getPageNumber(),conRotation.getPageSize());
        QueryWrapper<ConRotation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(conRotation.getImage()),ConRotation::getImage,conRotation.getImage());
        Page<ConRotation> conRotationPage = conRotationService.page(page, queryWrapper);
        return Result.success(conRotationPage);
    }

    @GetMapping("getConRotationList")
    public Result getConRotationList() {
        List<ConRotation> list = conRotationService.list();
        return Result.success(list);
    }

    /** 根据id获取轮播图 */
    @GetMapping("getConRotationById")
    public Result getConRotationById(@RequestParam("id")String id) {
        ConRotation conRotation = conRotationService.getById(id);
        return Result.success(conRotation);
    }

    /** 保存轮播图 */
    @PostMapping("saveConRotation")
    public Result saveConRotation(@RequestBody ConRotation conRotation) {
        boolean save = conRotationService.save(conRotation);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑轮播图 */
    @PostMapping("editConRotation")
    public Result editConRotation(@RequestBody ConRotation conRotation) {
        boolean save = conRotationService.updateById(conRotation);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除轮播图 */
    @GetMapping("removeConRotation")
    public Result removeConRotation(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conRotationService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("轮播图id不能为空！");
        }
    }

}
