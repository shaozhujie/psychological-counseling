package com.project.counseling.controller.ask;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.config.utils.ShiroUtils;
import com.project.counseling.domain.ConAsk;
import com.project.counseling.domain.ConHug;
import com.project.counseling.domain.Result;
import com.project.counseling.domain.User;
import com.project.counseling.service.ConAskService;
import com.project.counseling.service.ConHugService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 问答抱抱controller
 * @date 2025/11/17 10:16
 */
@Controller
@ResponseBody
@RequestMapping("hug")
public class ConHugController {

    @Autowired
    private ConHugService conHugService;
    @Autowired
    private ConAskService conAskService;

    /** 分页获取问答抱抱 */
    @PostMapping("getConHugPage")
    public Result getConHugPage(@RequestBody ConHug conHug) {
        Page<ConHug> page = new Page<>(conHug.getPageNumber(),conHug.getPageSize());
        QueryWrapper<ConHug> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(conHug.getAskId()),ConHug::getAskId,conHug.getAskId())
                .eq(StringUtils.isNotBlank(conHug.getUserId()),ConHug::getUserId,conHug.getUserId());
        Page<ConHug> conHugPage = conHugService.page(page, queryWrapper);
        return Result.success(conHugPage);
    }

    /** 根据id获取问答抱抱 */
    @GetMapping("getConHugById")
    public Result getConHugById(@RequestParam("id")String id) {
        ConHug conHug = conHugService.getById(id);
        return Result.success(conHug);
    }

    /** 保存问答抱抱 */
    @PostMapping("saveConHug")
    @Transactional
    public Result saveConHug(@RequestBody ConHug conHug) {
        User userInfo = ShiroUtils.getUserInfo();
        conHug.setUserId(userInfo.getId());
        QueryWrapper<ConHug> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConHug::getAskId,conHug.getAskId())
                .eq(ConHug::getUserId,conHug.getUserId());
        int count = conHugService.count(queryWrapper);
        if (count > 0) {
            return Result.fail("已抱抱过该问答！");
        }
        boolean save = conHugService.save(conHug);
        if (save) {
            ConAsk ask = conAskService.getById(conHug.getAskId());
            ask.setHug(ask.getHug() + 1);
            conAskService.updateById(ask);
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑问答抱抱 */
    @PostMapping("editConHug")
    public Result editConHug(@RequestBody ConHug conHug) {
        boolean save = conHugService.updateById(conHug);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除问答抱抱 */
    @GetMapping("removeConHug")
    public Result removeConHug(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conHugService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("问答抱抱id不能为空！");
        }
    }

}
