package com.project.counseling.controller.prescription;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.domain.ConMedicine;
import com.project.counseling.domain.ConPrescription;
import com.project.counseling.domain.ConPrescriptionItem;
import com.project.counseling.domain.Result;
import com.project.counseling.service.ConMedicineService;
import com.project.counseling.service.ConPrescriptionItemService;
import com.project.counseling.service.ConPrescriptionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 处方药品controller
 * @date 2025/11/20 10:11
 */
@Controller
@ResponseBody
@RequestMapping("item")
public class ConPrescriptionItemController {

    @Autowired
    private ConPrescriptionItemService conPrescriptionItemService;
    @Autowired
    private ConMedicineService conMedicineService;
    @Autowired
    private ConPrescriptionService conPrescriptionService;

    /** 分页获取处方药品 */
    @PostMapping("getConPrescriptionItemPage")
    public Result getConPrescriptionItemPage(@RequestBody ConPrescriptionItem conPrescriptionItem) {
        Page<ConPrescriptionItem> page = new Page<>(conPrescriptionItem.getPageNumber(),conPrescriptionItem.getPageSize());
        QueryWrapper<ConPrescriptionItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(conPrescriptionItem.getMedicineId()),ConPrescriptionItem::getMedicineId,conPrescriptionItem.getMedicineId())
                .like(StringUtils.isNotBlank(conPrescriptionItem.getName()),ConPrescriptionItem::getName,conPrescriptionItem.getName())
                .eq(StringUtils.isNotBlank(conPrescriptionItem.getPrescriptionId()),ConPrescriptionItem::getPrescriptionId,conPrescriptionItem.getPrescriptionId());
        Page<ConPrescriptionItem> conPrescriptionItemPage = conPrescriptionItemService.page(page, queryWrapper);
        return Result.success(conPrescriptionItemPage);
    }

    /** 根据id获取处方药品 */
    @GetMapping("getConPrescriptionItemById")
    public Result getConPrescriptionItemById(@RequestParam("id")String id) {
        ConPrescriptionItem conPrescriptionItem = conPrescriptionItemService.getById(id);
        return Result.success(conPrescriptionItem);
    }

    @GetMapping("getConPrescriptionItemByPrescriptionId")
    public Result getConPrescriptionItemByPrescriptionId(@RequestParam("id")String id) {
        QueryWrapper<ConPrescriptionItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ConPrescriptionItem::getPrescriptionId,id);
        List<ConPrescriptionItem> conPrescriptionItems = conPrescriptionItemService.list(queryWrapper);
        return Result.success(conPrescriptionItems);
    }

    /** 保存处方药品 */
    @PostMapping("saveConPrescriptionItem")
    @Transactional(rollbackFor = Exception.class)
    public Result saveConPrescriptionItem(@RequestBody ConPrescriptionItem conPrescriptionItem) {
        ConPrescription prescription = conPrescriptionService.getById(conPrescriptionItem.getPrescriptionId());
        if (prescription.getState() == 1) {
            return Result.fail("该处方已支付，无法添加药品！");
        }
        ConMedicine medicine = conMedicineService.getById(conPrescriptionItem.getMedicineId());
        if ((medicine.getStock() - conPrescriptionItem.getStock()) < 0) {
            return Result.fail("药品库存不足！");
        }
        conPrescriptionItem.setName(medicine.getName());
        conPrescriptionItem.setImage(medicine.getImage());
        conPrescriptionItem.setUnit(medicine.getPrice());
        conPrescriptionItem.setPrice(medicine.getPrice() * conPrescriptionItem.getStock());
        boolean save = conPrescriptionItemService.save(conPrescriptionItem);
        medicine.setStock(medicine.getStock() - conPrescriptionItem.getStock());
        conMedicineService.updateById(medicine);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑处方药品 */
    @PostMapping("editConPrescriptionItem")
    @Transactional(rollbackFor = Exception.class)
    public Result editConPrescriptionItem(@RequestBody ConPrescriptionItem conPrescriptionItem) {
        ConPrescription prescription = conPrescriptionService.getById(conPrescriptionItem.getPrescriptionId());
        if (prescription.getState() == 1) {
            return Result.fail("该处方已支付，无法添加药品！");
        }
        ConPrescriptionItem prescriptionItem = conPrescriptionItemService.getById(conPrescriptionItem.getId());
        ConMedicine medicine = conMedicineService.getById(conPrescriptionItem.getMedicineId());
        if ((medicine.getStock() + prescriptionItem.getStock() - conPrescriptionItem.getStock()) < 0) {
            return Result.fail("药品库存不足！");
        }
        conPrescriptionItem.setName(medicine.getName());
        conPrescriptionItem.setPrice(medicine.getPrice() * conPrescriptionItem.getStock());
        boolean save = conPrescriptionItemService.updateById(conPrescriptionItem);
        medicine.setStock(medicine.getStock() + prescriptionItem.getStock() - conPrescriptionItem.getStock());
        conMedicineService.updateById(medicine);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除处方药品 */
    @GetMapping("removeConPrescriptionItem")
    @Transactional(rollbackFor = Exception.class)
    public Result removeConPrescriptionItem(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                ConPrescriptionItem prescriptionItem = conPrescriptionItemService.getById(id);
                ConPrescription prescription = conPrescriptionService.getById(prescriptionItem.getPrescriptionId());
                if (prescription.getState() == 0) {
                    // 回退药品
                    ConMedicine medicine = conMedicineService.getById(prescriptionItem.getMedicineId());
                    medicine.setStock(medicine.getStock() + prescriptionItem.getStock());
                    conMedicineService.updateById(medicine);
                }
                conPrescriptionItemService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("处方药品id不能为空！");
        }
    }

}
