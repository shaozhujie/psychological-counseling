package com.project.counseling.controller.prescription;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.config.utils.ShiroUtils;
import com.project.counseling.domain.*;
import com.project.counseling.service.ConAppointmentService;
import com.project.counseling.service.ConPrescriptionItemService;
import com.project.counseling.service.ConPrescriptionService;
import com.project.counseling.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 处方controller
 * @date 2025/11/20 10:06
 */
@Controller
@ResponseBody
@RequestMapping("prescription")
public class ConPrescriptionController {

    @Autowired
    private ConPrescriptionService conPrescriptionService;
    @Autowired
    private ConAppointmentService conAppointmentService;
    @Autowired
    private ConPrescriptionItemService conPrescriptionItemService;
    @Autowired
    private UserService userService;

    /** 分页获取处方 */
    @PostMapping("getConPrescriptionPage")
    public Result getConPrescriptionPage(@RequestBody ConPrescription conPrescription) {
        User userInfo = ShiroUtils.getUserInfo();
        if (userInfo.getUserType() == 2) {
            conPrescription.setConsultId(userInfo.getId());
        }
        Page<ConPrescription> page = new Page<>(conPrescription.getPageNumber(),conPrescription.getPageSize());
        QueryWrapper<ConPrescription> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(conPrescription.getOrderNumber()),ConPrescription::getOrderNumber,conPrescription.getOrderNumber())
                .like(StringUtils.isNotBlank(conPrescription.getAppointmentNumber()),ConPrescription::getAppointmentNumber,conPrescription.getAppointmentNumber())
                .eq(StringUtils.isNotBlank(conPrescription.getUserId()),ConPrescription::getUserId,conPrescription.getUserId())
                .eq(StringUtils.isNotBlank(conPrescription.getConsultId()),ConPrescription::getConsultId,conPrescription.getConsultId())
                .like(StringUtils.isNotBlank(conPrescription.getName()),ConPrescription::getName,conPrescription.getName())
                .like(StringUtils.isNotBlank(conPrescription.getTel()),ConPrescription::getTel,conPrescription.getTel());
        Page<ConPrescription> conPrescriptionPage = conPrescriptionService.page(page, queryWrapper);
        for (ConPrescription prescription : conPrescriptionPage.getRecords()) {
            QueryWrapper<ConPrescriptionItem> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(ConPrescriptionItem::getPrescriptionId,prescription.getId());
            List<ConPrescriptionItem> list = conPrescriptionItemService.list(wrapper);
            float price = 0;
            for (ConPrescriptionItem item : list) {
                price += item.getPrice();
            }
            prescription.setPrice(price);
            User user = userService.getById(prescription.getConsultId());
            prescription.setUser(user);
            QueryWrapper<ConPrescriptionItem> wrapper1 = new QueryWrapper<>();
            wrapper1.lambda().eq(ConPrescriptionItem::getPrescriptionId,prescription.getId());
            List<ConPrescriptionItem> list1 = conPrescriptionItemService.list(wrapper1);
            List<String> medicines = new ArrayList<>();
            for (ConPrescriptionItem item : list1) {
                medicines.add(item.getName() + "*" + item.getStock());
            }
            prescription.setItems(StringUtils.join(medicines,","));
        }
        return Result.success(conPrescriptionPage);
    }

    /** 根据id获取处方 */
    @GetMapping("getConPrescriptionById")
    public Result getConPrescriptionById(@RequestParam("id")String id) {
        ConPrescription conPrescription = conPrescriptionService.getById(id);
        QueryWrapper<ConPrescriptionItem> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ConPrescriptionItem::getPrescriptionId,conPrescription.getId());
        List<ConPrescriptionItem> list = conPrescriptionItemService.list(wrapper);
        float price = 0;
        for (ConPrescriptionItem item : list) {
            price += item.getPrice();
        }
        conPrescription.setPrice(price);
        User user = userService.getById(conPrescription.getConsultId());
        conPrescription.setUser(user);
        QueryWrapper<ConPrescriptionItem> wrapper1 = new QueryWrapper<>();
        wrapper1.lambda().eq(ConPrescriptionItem::getPrescriptionId,conPrescription.getId());
        List<ConPrescriptionItem> list1 = conPrescriptionItemService.list(wrapper1);
        List<String> medicines = new ArrayList<>();
        for (ConPrescriptionItem item : list1) {
            medicines.add(item.getName() + "*" + item.getStock());
        }
        conPrescription.setItems(StringUtils.join(medicines,","));
        return Result.success(conPrescription);
    }

    /** 保存处方 */
    @PostMapping("saveConPrescription")
    public Result saveConPrescription(@RequestBody ConPrescription conPrescription) {
        QueryWrapper<ConAppointment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConAppointment::getOrderNumber,conPrescription.getAppointmentNumber()).last("limit 1");
        ConAppointment conAppointment = conAppointmentService.getOne(queryWrapper);
        conPrescription.setUserId(conAppointment.getUserId());
        conPrescription.setType(conAppointment.getType());
        conPrescription.setOrderNumber(IdWorker.getMillisecond());
        conPrescription.setAppointmentNumber(conAppointment.getOrderNumber());
        conPrescription.setConsultId(conAppointment.getConsultId());
        boolean save = conPrescriptionService.save(conPrescription);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑处方 */
    @PostMapping("editConPrescription")
    public Result editConPrescription(@RequestBody ConPrescription conPrescription) {
        ConPrescription prescription = conPrescriptionService.getById(conPrescription.getId());
        if (prescription.getState() == 0 && conPrescription.getState() == 1) {
            QueryWrapper<ConPrescriptionItem> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ConPrescriptionItem::getPrescriptionId,conPrescription.getId());
            int count = conPrescriptionItemService.count(queryWrapper);
            if (count <= 0) {
                return Result.fail("请先添加药品后再支付！");
            }
        }
        boolean save = conPrescriptionService.updateById(conPrescription);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除处方 */
    @GetMapping("removeConPrescription")
    public Result removeConPrescription(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conPrescriptionService.removeById(id);
                QueryWrapper<ConPrescriptionItem> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ConPrescriptionItem::getPrescriptionId,id);
                conPrescriptionItemService.remove(queryWrapper);
            }
            return Result.success();
        } else {
            return Result.fail("处方id不能为空！");
        }
    }

}
