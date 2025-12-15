package com.project.counseling.controller.cooperation;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.domain.ConCooperation;
import com.project.counseling.domain.Result;
import com.project.counseling.service.ConCooperationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 合作媒体/平台/品牌controller
 * @date 2025/11/14 11:01
 */
@Controller
@ResponseBody
@RequestMapping("cooperation")
public class ConCooperationController {

    @Autowired
    private ConCooperationService conCooperationService;

    /** 分页获取合作媒体/平台/品牌 */
    @PostMapping("getConCooperationPage")
    public Result getConCooperationPage(@RequestBody ConCooperation conCooperation) {
        Page<ConCooperation> page = new Page<>(conCooperation.getPageNumber(),conCooperation.getPageSize());
        QueryWrapper<ConCooperation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(conCooperation.getName()),ConCooperation::getName,conCooperation.getName());
        Page<ConCooperation> conCooperationPage = conCooperationService.page(page, queryWrapper);
        return Result.success(conCooperationPage);
    }

    @GetMapping("getConCooperationList")
    public Result getConCooperationList() {
        List<ConCooperation> list = conCooperationService.list();
        return Result.success(list);
    }

    /** 根据id获取合作媒体/平台/品牌 */
    @GetMapping("getConCooperationById")
    public Result getConCooperationById(@RequestParam("id")String id) {
        ConCooperation conCooperation = conCooperationService.getById(id);
        return Result.success(conCooperation);
    }

    /** 保存合作媒体/平台/品牌 */
    @PostMapping("saveConCooperation")
    public Result saveConCooperation(@RequestBody ConCooperation conCooperation) {
        boolean save = conCooperationService.save(conCooperation);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑合作媒体/平台/品牌 */
    @PostMapping("editConCooperation")
    public Result editConCooperation(@RequestBody ConCooperation conCooperation) {
        boolean save = conCooperationService.updateById(conCooperation);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除合作媒体/平台/品牌 */
    @GetMapping("removeConCooperation")
    public Result removeConCooperation(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conCooperationService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("合作媒体/平台/品牌id不能为空！");
        }
    }

}
