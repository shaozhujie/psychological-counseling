package com.project.counseling.controller.appointment;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.config.utils.ShiroUtils;
import com.project.counseling.domain.*;
import com.project.counseling.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 咨询预约controller
 * @date 2025/11/19 03:49
 */
@Controller
@ResponseBody
@RequestMapping("appointment")
public class ConAppointmentController {

    @Autowired
    private ConAppointmentService conAppointmentService;
    @Autowired
    private ConMakeService conMakeService;
    @Autowired
    private UserService userService;
    @Autowired
    private ConSetService conSetService;
    @Autowired
    private ConTalkService conTalkService;

    /** 分页获取咨询预约 */
    @PostMapping("getConAppointmentPage")
    public Result getConAppointmentPage(@RequestBody ConAppointment conAppointment) {
        User userInfo = ShiroUtils.getUserInfo();
        if (userInfo.getUserType() == 2) {
            conAppointment.setConsultId(userInfo.getId());
        }
        Page<ConAppointment> page = new Page<>(conAppointment.getPageNumber(),conAppointment.getPageSize());
        QueryWrapper<ConAppointment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(conAppointment.getOrderNumber()),ConAppointment::getOrderNumber,conAppointment.getOrderNumber())
                .eq(StringUtils.isNotBlank(conAppointment.getConsultId()),ConAppointment::getConsultId,conAppointment.getConsultId())
                .eq(StringUtils.isNotBlank(conAppointment.getUserId()),ConAppointment::getUserId,conAppointment.getUserId())
                .eq(StringUtils.isNotBlank(conAppointment.getMakeId()),ConAppointment::getMakeId,conAppointment.getMakeId())
                .eq(conAppointment.getSlotDate() != null,ConAppointment::getSlotDate,conAppointment.getSlotDate())
                .like(StringUtils.isNotBlank(conAppointment.getName()),ConAppointment::getName,conAppointment.getName())
                .like(StringUtils.isNotBlank(conAppointment.getTel()),ConAppointment::getTel,conAppointment.getTel())
                .eq(conAppointment.getType() != null,ConAppointment::getType,conAppointment.getType())
                .eq(conAppointment.getState() != null,ConAppointment::getState,conAppointment.getState());
        Page<ConAppointment> conAppointmentPage = conAppointmentService.page(page, queryWrapper);
        for (ConAppointment appointment : conAppointmentPage.getRecords()) {
            appointment.setUser(userService.getById(appointment.getConsultId()));
        }
        return Result.success(conAppointmentPage);
    }

    @GetMapping("getConAppointmentList")
    public Result getConAppointmentList() {
        User userInfo = ShiroUtils.getUserInfo();
        QueryWrapper<ConAppointment> queryWrapper = new QueryWrapper<>();
        if (userInfo.getUserType() == 1) {
            queryWrapper.lambda().eq(ConAppointment::getConsultId,userInfo.getId());
        }
        List<ConAppointment> list = conAppointmentService.list(queryWrapper);
        return Result.success(list);
    }

    /** 根据id获取咨询预约 */
    @GetMapping("getConAppointmentById")
    public Result getConAppointmentById(@RequestParam("id")String id) {
        ConAppointment conAppointment = conAppointmentService.getById(id);
        conAppointment.setUser(userService.getById(conAppointment.getConsultId()));
        return Result.success(conAppointment);
    }

    /** 保存咨询预约 */
    @PostMapping("saveConAppointment")
    @Transactional(rollbackFor = Exception.class)
    public Result saveConAppointment(@RequestBody ConAppointment conAppointment) {
        User userInfo = ShiroUtils.getUserInfo();
        conAppointment.setUserId(userInfo.getId());
        conAppointment.setOrderNumber(IdWorker.getMillisecond());
        ConMake make = conMakeService.getById(conAppointment.getMakeId());
        conAppointment.setSlotDate(make.getSlotDate());
        conAppointment.setSlotStart(make.getSlotStart());
        conAppointment.setSlotEnd(make.getSlotEnd());
        User user = userService.getById(conAppointment.getConsultId());
        conAppointment.setAddress(user.getAddress());
        QueryWrapper<ConSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConSet::getUserId,conAppointment.getConsultId()).last("limit 1");
        ConSet conSet = conSetService.getOne(queryWrapper);
        conAppointment.setPrice(conSet.getPrice());
        boolean save = conAppointmentService.save(conAppointment);
        make.setState(1);
        conMakeService.updateById(make);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑咨询预约 */
    @PostMapping("editConAppointment")
    @Transactional(rollbackFor = Exception.class)
    public Result editConAppointment(@RequestBody ConAppointment conAppointment) {
        if (conAppointment.getState() == 2) {
            ConAppointment appointment = conAppointmentService.getById(conAppointment.getId());
            User user = userService.getById(appointment.getConsultId());
            QueryWrapper<ConAppointment> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ConAppointment::getConsultId,user.getId())
                    .eq(ConAppointment::getState,2);
            List<ConAppointment> list = conAppointmentService.list(queryWrapper);
            float star = 0;
            for (ConAppointment conAppointment1 : list) {
                star += conAppointment1.getStar();
            }
            float total = star + conAppointment.getStar();
            user.setStar(total/(list.size() + 1));
            userService.updateById(user);
        }
        boolean save = conAppointmentService.updateById(conAppointment);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除咨询预约 */
    @GetMapping("removeConAppointment")
    public Result removeConAppointment(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conAppointmentService.removeById(id);
                QueryWrapper<ConTalk> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ConTalk::getAppointmentId,id);
                conTalkService.remove(queryWrapper);
            }
            return Result.success();
        } else {
            return Result.fail("咨询预约id不能为空！");
        }
    }

    @GetMapping("cancelAppointment")
    @Transactional(rollbackFor = Exception.class)
    public Result cancelAppointment(@RequestParam("id")String id) {
        ConAppointment appointment = conAppointmentService.getById(id);
        if (appointment.getState() != 3) {
            return Result.fail("该预约已确认无法取消！");
        }
        appointment.setState(4);
        boolean updateById = conAppointmentService.updateById(appointment);
        // 更新预约信息
        ConMake make = conMakeService.getById(appointment.getMakeId());
        make.setState(0);
        conMakeService.updateById(make);
        return Result.success();
    }

    @GetMapping("getCommentList")
    public Result getCommentList(@RequestParam("id")String id) {
        QueryWrapper<ConAppointment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConAppointment::getConsultId,id).eq(ConAppointment::getState,2)
                .orderByDesc(ConAppointment::getCreateTime);
        List<ConAppointment> list = conAppointmentService.list(queryWrapper);
        return Result.success(list);
    }

}
