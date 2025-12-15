package com.project.counseling.controller.archives;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.config.utils.ShiroUtils;
import com.project.counseling.domain.ConArchives;
import com.project.counseling.domain.Result;
import com.project.counseling.domain.User;
import com.project.counseling.service.ConArchivesService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 咨询档案controller
 * @date 2025/11/19 04:51
 */
@Controller
@ResponseBody
@RequestMapping("archives")
public class ConArchivesController {

    @Autowired
    private ConArchivesService conArchivesService;

    /** 分页获取咨询档案 */
    @PostMapping("getConArchivesPage")
    public Result getConArchivesPage(@RequestBody ConArchives conArchives) {
        User userInfo = ShiroUtils.getUserInfo();
        if (userInfo.getUserType() == 2) {
            conArchives.setUserId(userInfo.getId());
        }
        Page<ConArchives> page = new Page<>(conArchives.getPageNumber(),conArchives.getPageSize());
        QueryWrapper<ConArchives> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(conArchives.getUserId()),ConArchives::getUserId,conArchives.getUserId())
                .like(StringUtils.isNotBlank(conArchives.getTel()),ConArchives::getTel,conArchives.getTel())
                .like(StringUtils.isNotBlank(conArchives.getName()),ConArchives::getName,conArchives.getName())
                .eq(StringUtils.isNotBlank(conArchives.getAppDate()),ConArchives::getAppDate,conArchives.getAppDate());
        Page<ConArchives> conArchivesPage = conArchivesService.page(page, queryWrapper);
        return Result.success(conArchivesPage);
    }

    /** 根据id获取咨询档案 */
    @GetMapping("getConArchivesById")
    public Result getConArchivesById(@RequestParam("id")String id) {
        ConArchives conArchives = conArchivesService.getById(id);
        return Result.success(conArchives);
    }

    /** 保存咨询档案 */
    @PostMapping("saveConArchives")
    public Result saveConArchives(@RequestBody ConArchives conArchives) {
        User userInfo = ShiroUtils.getUserInfo();
        conArchives.setUserId(userInfo.getId());
        boolean save = conArchivesService.save(conArchives);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑咨询档案 */
    @PostMapping("editConArchives")
    public Result editConArchives(@RequestBody ConArchives conArchives) {
        boolean save = conArchivesService.updateById(conArchives);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除咨询档案 */
    @GetMapping("removeConArchives")
    public Result removeConArchives(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conArchivesService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("咨询档案id不能为空！");
        }
    }

}
