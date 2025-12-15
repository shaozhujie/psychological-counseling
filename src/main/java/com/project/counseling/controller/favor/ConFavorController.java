package com.project.counseling.controller.favor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.config.utils.ShiroUtils;
import com.project.counseling.domain.*;
import com.project.counseling.service.ConFavorService;
import com.project.counseling.service.ConSetService;
import com.project.counseling.service.ConTypeService;
import com.project.counseling.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 收藏controller
 * @date 2025/11/15 11:36
 */
@Controller
@ResponseBody
@RequestMapping("favor")
public class ConFavorController {

    @Autowired
    private ConFavorService conFavorService;
    @Autowired
    private UserService userService;
    @Autowired
    private ConSetService conSetService;
    @Autowired
    private ConTypeService conTypeService;

    /** 分页获取收藏 */
    @PostMapping("getConFavorPage")
    public Result getConFavorPage(@RequestBody ConFavor conFavor) {
        User userInfo = ShiroUtils.getUserInfo();
        conFavor.setUserId(userInfo.getId());
        Page<ConFavor> page = new Page<>(conFavor.getPageNumber(),conFavor.getPageSize());
        QueryWrapper<ConFavor> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(conFavor.getUserId()),ConFavor::getUserId,conFavor.getUserId())
                .eq(StringUtils.isNotBlank(conFavor.getConsultingId()),ConFavor::getConsultingId,conFavor.getConsultingId());
        Page<ConFavor> conFavorPage = conFavorService.page(page, queryWrapper);
        for (ConFavor favor : conFavorPage.getRecords()) {
            User user = userService.getById(favor.getConsultingId());
            QueryWrapper<ConSet> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(ConSet::getUserId,favor.getConsultingId());
            ConSet conSet = conSetService.getOne(wrapper);
            user.setConSet(conSet);
            if (StringUtils.isNotBlank(user.getTypes())) {
                String[] strings = user.getTypes().split(",");
                List<String> list = new ArrayList<>();
                for (String string : strings) {
                    ConType byId = conTypeService.getById(string);
                    list.add(byId.getName());
                }
                user.setTypeWz(list);
            }
            favor.setUser(user);
        }
        return Result.success(conFavorPage);
    }

    /** 根据id获取收藏 */
    @GetMapping("getConFavorById")
    public Result getConFavorById(@RequestParam("id")String id) {
        ConFavor conFavor = conFavorService.getById(id);
        return Result.success(conFavor);
    }

    /** 保存收藏 */
    @PostMapping("saveConFavor")
    public Result saveConFavor(@RequestBody ConFavor conFavor) {
        User userInfo = ShiroUtils.getUserInfo();
        conFavor.setUserId(userInfo.getId());
        QueryWrapper<ConFavor> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConFavor::getUserId,userInfo.getId())
                .eq(ConFavor::getConsultingId,conFavor.getConsultingId());
        int count = conFavorService.count(queryWrapper);
        if (count > 0) {
            return Result.fail("已收藏！");
        }
        boolean save = conFavorService.save(conFavor);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    @GetMapping("getConFavorByUserId")
    public Result getConFavorByUserId(@RequestParam("consultingId")String consultingId) {
        QueryWrapper<ConFavor> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConFavor::getUserId,ShiroUtils.getUserInfo().getId())
                .eq(ConFavor::getConsultingId,consultingId);
        int count = conFavorService.count(queryWrapper);
        if (count > 0) {
            return Result.success(true);
        } else {
            return Result.success(false);
        }
    }

    /** 编辑收藏 */
    @PostMapping("editConFavor")
    public Result editConFavor(@RequestBody ConFavor conFavor) {
        boolean save = conFavorService.updateById(conFavor);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除收藏 */
    @GetMapping("removeConFavor")
    public Result removeConFavor(@RequestParam("id")String id) {
        User userInfo = ShiroUtils.getUserInfo();
        QueryWrapper<ConFavor> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConFavor::getUserId,userInfo.getId())
                .eq(ConFavor::getConsultingId,id);
        conFavorService.remove(queryWrapper);
        return Result.success();
    }

}
