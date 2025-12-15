package com.project.counseling.controller.medicine;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.config.utils.ShiroUtils;
import com.project.counseling.domain.ConMedicine;
import com.project.counseling.domain.Result;
import com.project.counseling.domain.User;
import com.project.counseling.service.ConMedicineService;
import com.project.counseling.service.ConMedicineTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 药品controller
 * @date 2025/11/19 05:07
 */
@Controller
@ResponseBody
@RequestMapping("medicine")
public class ConMedicineController {

    @Autowired
    private ConMedicineService conMedicineService;
    @Autowired
    private ConMedicineTypeService conMedicineTypeService;

    /** 分页获取药品 */
    @PostMapping("getConMedicinePage")
    public Result getConMedicinePage(@RequestBody ConMedicine conMedicine) {
        User userInfo = ShiroUtils.getUserInfo();
        if (userInfo.getUserType() == 2) {
            conMedicine.setUserId(userInfo.getId());
        }
        Page<ConMedicine> page = new Page<>(conMedicine.getPageNumber(),conMedicine.getPageSize());
        QueryWrapper<ConMedicine> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(conMedicine.getUserId()),ConMedicine::getUserId,conMedicine.getUserId())
                .eq(StringUtils.isNotBlank(conMedicine.getTypeId()),ConMedicine::getTypeId,conMedicine.getTypeId())
                .like(StringUtils.isNotBlank(conMedicine.getName()),ConMedicine::getName,conMedicine.getName())
                .like(StringUtils.isNotBlank(conMedicine.getBrand()),ConMedicine::getBrand,conMedicine.getBrand());
        Page<ConMedicine> conMedicinePage = conMedicineService.page(page, queryWrapper);
        for (ConMedicine medicine : conMedicinePage.getRecords()) {
            medicine.setTypeId(conMedicineTypeService.getById(medicine.getTypeId()).getName());
        }
        return Result.success(conMedicinePage);
    }

    @GetMapping("getConMedicineList")
    public Result getConMedicineList() {
        User userInfo = ShiroUtils.getUserInfo();
        QueryWrapper<ConMedicine> queryWrapper = new QueryWrapper<>();
        if (userInfo.getUserType() == 2) {
            queryWrapper.lambda().eq(ConMedicine::getUserId,userInfo.getId());
        }
        List<ConMedicine> list = conMedicineService.list(queryWrapper);
        return Result.success(list);
    }

    /** 根据id获取药品 */
    @GetMapping("getConMedicineById")
    public Result getConMedicineById(@RequestParam("id")String id) {
        ConMedicine conMedicine = conMedicineService.getById(id);
        return Result.success(conMedicine);
    }

    /** 保存药品 */
    @PostMapping("saveConMedicine")
    public Result saveConMedicine(@RequestBody ConMedicine conMedicine) {
        User userInfo = ShiroUtils.getUserInfo();
        conMedicine.setUserId(userInfo.getId());
        boolean save = conMedicineService.save(conMedicine);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑药品 */
    @PostMapping("editConMedicine")
    public Result editConMedicine(@RequestBody ConMedicine conMedicine) {
        boolean save = conMedicineService.updateById(conMedicine);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除药品 */
    @GetMapping("removeConMedicine")
    public Result removeConMedicine(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conMedicineService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("药品id不能为空！");
        }
    }

}
