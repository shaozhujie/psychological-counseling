package com.project.counseling.controller.make;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.config.utils.ShiroUtils;
import com.project.counseling.domain.ConMake;
import com.project.counseling.domain.Result;
import com.project.counseling.domain.User;
import com.project.counseling.service.ConMakeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 预约设置controller
 * @date 2025/11/19 03:24
 */
@Controller
@ResponseBody
@RequestMapping("make")
public class ConMakeController {

    @Autowired
    private ConMakeService conMakeService;

    /** 分页获取预约设置 */
    @PostMapping("getConMakePage")
    public Result getConMakePage(@RequestBody ConMake conMake) {
        User userInfo = ShiroUtils.getUserInfo();
        conMake.setUserId(userInfo.getId());
        Page<ConMake> page = new Page<>(conMake.getPageNumber(),conMake.getPageSize());
        QueryWrapper<ConMake> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(conMake.getUserId()),ConMake::getUserId,conMake.getUserId())
                .eq(conMake.getSlotDate() != null,ConMake::getSlotDate,conMake.getSlotDate())
                .eq(conMake.getState() != null,ConMake::getState,conMake.getState())
                .orderByAsc(ConMake::getSlotDate);
        Page<ConMake> conMakePage = conMakeService.page(page, queryWrapper);
        return Result.success(conMakePage);
    }

    @GetMapping("getConMakeListByUserId")
    public Result getConMakeListByUserId(@RequestParam("userId")String userId,@RequestParam("slotDate")String slotDate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = simpleDateFormat.parse(slotDate.substring(0,10) + " 23:59:59");
        // 获取今天的日期
        LocalDate today = LocalDate.now();
        // 获取明天的日期
        LocalDate tomorrow = today.plusDays(1);
        // 设置时间为午夜0点
        LocalTime startTime = LocalTime.MIDNIGHT;
        // 创建明天的午夜0点的LocalDateTime对象
        LocalDateTime tomorrowStartTime = LocalDateTime.of(tomorrow, startTime);
        // 将LocalDateTime对象转换为Date对象
        Date tomorrowStart = Date.from(tomorrowStartTime.atZone(ZoneId.systemDefault()).toInstant());
        if (parse.getTime() < tomorrowStart.getTime()) {
            return Result.success(new ArrayList<>());
        }
        QueryWrapper<ConMake> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConMake::getUserId,userId)
                .eq(ConMake::getSlotDate,slotDate)
                .eq(ConMake::getState,0)
                .orderByAsc(ConMake::getSlotDate)
                .orderByAsc(ConMake::getSlotStart);
        List<ConMake> makeList = conMakeService.list(queryWrapper);
        return Result.success(makeList);
    }

    /** 根据id获取预约设置 */
    @GetMapping("getConMakeById")
    public Result getConMakeById(@RequestParam("id")String id) {
        ConMake conMake = conMakeService.getById(id);
        return Result.success(conMake);
    }

    /** 保存预约设置 */
    @PostMapping("saveConMake")
    public Result saveConMake(@RequestBody ConMake conMake) {
        User userInfo = ShiroUtils.getUserInfo();
        conMake.setUserId(userInfo.getId());
        boolean save = conMakeService.save(conMake);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑预约设置 */
    @PostMapping("editConMake")
    public Result editConMake(@RequestBody ConMake conMake) {
        boolean save = conMakeService.updateById(conMake);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除预约设置 */
    @GetMapping("removeConMake")
    public Result removeConMake(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conMakeService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("预约设置id不能为空！");
        }
    }

}
