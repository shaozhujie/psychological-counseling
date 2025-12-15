package com.project.counseling.controller.set;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.config.utils.ShiroUtils;
import com.project.counseling.domain.ConSet;
import com.project.counseling.domain.Result;
import com.project.counseling.domain.User;
import com.project.counseling.service.ConSetService;
import com.project.counseling.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 咨询师个人设置controller
 * @date 2025/11/14 05:27
 */
@Controller
@ResponseBody
@RequestMapping("set")
public class ConSetController {

    @Autowired
    private ConSetService conSetService;
    @Autowired
    private UserService userService;

    /** 分页获取咨询师个人设置 */
    @PostMapping("getConSetPage")
    public Result getConSetPage(@RequestBody ConSet conSet) {
        Page<ConSet> page = new Page<>(conSet.getPageNumber(),conSet.getPageSize());
        QueryWrapper<ConSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(conSet.getUserId()),ConSet::getUserId,conSet.getUserId());
        Page<ConSet> conSetPage = conSetService.page(page, queryWrapper);
        return Result.success(conSetPage);
    }

    @GetMapping("getConSetByUserId")
    public Result getConSetByUserId() {
        User userInfo = ShiroUtils.getUserInfo();
        ConSet conSet = conSetService.getOne(new QueryWrapper<ConSet>().lambda().eq(ConSet::getUserId,userInfo.getId()));
        User user = userService.getById(userInfo.getId());
        conSet.setTypes(user.getTypes());
        return Result.success(conSet);
    }

    /** 根据id获取咨询师个人设置 */
    @GetMapping("getConSetById")
    public Result getConSetById(@RequestParam("id")String id) {
        ConSet conSet = conSetService.getById(id);
        return Result.success(conSet);
    }

    /** 保存咨询师个人设置 */
    @PostMapping("saveConSet")
    public Result saveConSet(@RequestBody ConSet conSet) {
        boolean save = conSetService.save(conSet);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑咨询师个人设置 */
    @PostMapping("editConSet")
    public Result editConSet(@RequestBody ConSet conSet) {
        boolean save = conSetService.updateById(conSet);
        User user = userService.getById(conSet.getUserId());
        user.setTypes(conSet.getTypes());
        userService.updateById(user);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除咨询师个人设置 */
    @GetMapping("removeConSet")
    public Result removeConSet(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conSetService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("咨询师个人设置id不能为空！");
        }
    }

}
