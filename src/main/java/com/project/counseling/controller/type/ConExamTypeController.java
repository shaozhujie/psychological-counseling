package com.project.counseling.controller.type;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.domain.ConExam;
import com.project.counseling.domain.ConExamType;
import com.project.counseling.domain.Result;
import com.project.counseling.service.ConExamService;
import com.project.counseling.service.ConExamTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 测评类别controller
 * @date 2025/11/19 11:08
 */
@Controller
@ResponseBody
@RequestMapping("type")
public class ConExamTypeController {

    @Autowired
    private ConExamTypeService conExamTypeService;
    @Autowired
    private ConExamService conExamService;

    /** 分页获取测评类别 */
    @PostMapping("getConExamTypePage")
    public Result getConExamTypePage(@RequestBody ConExamType conExamType) {
        Page<ConExamType> page = new Page<>(conExamType.getPageNumber(),conExamType.getPageSize());
        QueryWrapper<ConExamType> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(conExamType.getName()),ConExamType::getName,conExamType.getName());
        Page<ConExamType> conExamTypePage = conExamTypeService.page(page, queryWrapper);
        return Result.success(conExamTypePage);
    }

    @GetMapping("getConExamTypeList")
    public Result getConExamTypeList() {
        List<ConExamType> list = conExamTypeService.list();
        return Result.success(list);
    }

    /** 根据id获取测评类别 */
    @GetMapping("getConExamTypeById")
    public Result getConExamTypeById(@RequestParam("id")String id) {
        ConExamType conExamType = conExamTypeService.getById(id);
        return Result.success(conExamType);
    }

    /** 保存测评类别 */
    @PostMapping("saveConExamType")
    public Result saveConExamType(@RequestBody ConExamType conExamType) {
        boolean save = conExamTypeService.save(conExamType);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑测评类别 */
    @PostMapping("editConExamType")
    public Result editConExamType(@RequestBody ConExamType conExamType) {
        boolean save = conExamTypeService.updateById(conExamType);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除测评类别 */
    @GetMapping("removeConExamType")
    public Result removeConExamType(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conExamTypeService.removeById(id);
                QueryWrapper<ConExam> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ConExam::getTypeId,id);
                int count = conExamService.count(queryWrapper);
                if (count > 0) {
                    return Result.fail("该测评类别下有测评，不能删除！");
                }
            }
            return Result.success();
        } else {
            return Result.fail("测评类别id不能为空！");
        }
    }

}
