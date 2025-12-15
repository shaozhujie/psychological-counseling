package com.project.counseling.controller.city;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.domain.ConCity;
import com.project.counseling.domain.Result;
import com.project.counseling.service.ConCityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 城市controller
 * @date 2025/11/14 03:30
 */
@Controller
@ResponseBody
@RequestMapping("city")
public class ConCityController {

    @Autowired
    private ConCityService conCityService;

    /** 分页获取城市 */
    @PostMapping("getConCityPage")
    public Result getConCityPage(@RequestBody ConCity conCity) {
        Page<ConCity> page = new Page<>(conCity.getPageNumber(),conCity.getPageSize());
        QueryWrapper<ConCity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(conCity.getName()),ConCity::getName,conCity.getName());
        Page<ConCity> conCityPage = conCityService.page(page, queryWrapper);
        return Result.success(conCityPage);
    }

    @GetMapping("getConCityList")
    public Result getConCityList() {
        List<ConCity> list = conCityService.list();
        return Result.success(list);
    }

    /** 根据id获取城市 */
    @GetMapping("getConCityById")
    public Result getConCityById(@RequestParam("id")String id) {
        ConCity conCity = conCityService.getById(id);
        return Result.success(conCity);
    }

    /** 保存城市 */
    @PostMapping("saveConCity")
    public Result saveConCity(@RequestBody ConCity conCity) {
        boolean save = conCityService.save(conCity);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑城市 */
    @PostMapping("editConCity")
    public Result editConCity(@RequestBody ConCity conCity) {
        boolean save = conCityService.updateById(conCity);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除城市 */
    @GetMapping("removeConCity")
    public Result removeConCity(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conCityService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("城市id不能为空！");
        }
    }

}
