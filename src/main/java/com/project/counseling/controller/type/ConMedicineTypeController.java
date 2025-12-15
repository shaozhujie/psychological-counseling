package com.project.counseling.controller.type;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.domain.ConMedicine;
import com.project.counseling.domain.ConMedicineType;
import com.project.counseling.domain.Result;
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
 * @description: 药品类别controller
 * @date 2025/11/19 04:56
 */
@Controller
@ResponseBody
@RequestMapping("type")
public class ConMedicineTypeController {

    @Autowired
    private ConMedicineTypeService conMedicineTypeService;
    @Autowired
    private ConMedicineService conMedicineService;

    /** 分页获取药品类别 */
    @PostMapping("getConMedicineTypePage")
    public Result getConMedicineTypePage(@RequestBody ConMedicineType conMedicineType) {
        Page<ConMedicineType> page = new Page<>(conMedicineType.getPageNumber(),conMedicineType.getPageSize());
        QueryWrapper<ConMedicineType> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(conMedicineType.getName()),ConMedicineType::getName,conMedicineType.getName());
        Page<ConMedicineType> conMedicineTypePage = conMedicineTypeService.page(page, queryWrapper);
        return Result.success(conMedicineTypePage);
    }

    @GetMapping("getConMedicineTypeList")
    public Result getConMedicineTypeList() {
        List<ConMedicineType> list = conMedicineTypeService.list();
        return Result.success(list);
    }

    /** 根据id获取药品类别 */
    @GetMapping("getConMedicineTypeById")
    public Result getConMedicineTypeById(@RequestParam("id")String id) {
        ConMedicineType conMedicineType = conMedicineTypeService.getById(id);
        return Result.success(conMedicineType);
    }

    /** 保存药品类别 */
    @PostMapping("saveConMedicineType")
    public Result saveConMedicineType(@RequestBody ConMedicineType conMedicineType) {
        boolean save = conMedicineTypeService.save(conMedicineType);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑药品类别 */
    @PostMapping("editConMedicineType")
    public Result editConMedicineType(@RequestBody ConMedicineType conMedicineType) {
        boolean save = conMedicineTypeService.updateById(conMedicineType);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除药品类别 */
    @GetMapping("removeConMedicineType")
    public Result removeConMedicineType(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conMedicineTypeService.removeById(id);
                QueryWrapper<ConMedicine> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ConMedicine::getTypeId,id);
                int count = conMedicineService.count(queryWrapper);
                if (count > 0) {
                    return Result.fail("该药品类别下有药品，不能删除！");
                }
            }
            return Result.success();
        } else {
            return Result.fail("药品类别id不能为空！");
        }
    }

}
