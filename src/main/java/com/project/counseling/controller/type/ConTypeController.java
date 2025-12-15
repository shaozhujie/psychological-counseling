package com.project.counseling.controller.type;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.domain.ConType;
import com.project.counseling.domain.Result;
import com.project.counseling.domain.User;
import com.project.counseling.service.ConTypeService;
import com.project.counseling.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 类型controller
 * @date 2025/11/15 10:03
 */
@Controller
@ResponseBody
@RequestMapping("type")
public class ConTypeController {

    @Autowired
    private ConTypeService conTypeService;
    @Autowired
    private UserService userService;

    /** 分页获取类型 */
    @PostMapping("getConTypePage")
    public Result getConTypePage(@RequestBody ConType conType) {
        Page<ConType> page = new Page<>(conType.getPageNumber(),conType.getPageSize());
        QueryWrapper<ConType> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(conType.getName()),ConType::getName,conType.getName());
        Page<ConType> conTypePage = conTypeService.page(page, queryWrapper);
        return Result.success(conTypePage);
    }

    @GetMapping("getConTypeList")
    public Result getConTypeList() {
        List<ConType> list = conTypeService.list();
        return Result.success(list);
    }

    /** 根据id获取类型 */
    @GetMapping("getConTypeById")
    public Result getConTypeById(@RequestParam("id")String id) {
        ConType conType = conTypeService.getById(id);
        return Result.success(conType);
    }

    /** 保存类型 */
    @PostMapping("saveConType")
    public Result saveConType(@RequestBody ConType conType) {
        boolean save = conTypeService.save(conType);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑类型 */
    @PostMapping("editConType")
    public Result editConType(@RequestBody ConType conType) {
        boolean save = conTypeService.updateById(conType);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除类型 */
    @GetMapping("removeConType")
    public Result removeConType(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
                queryWrapper.lambda().like(User::getTypes,id);
                int count = userService.count(queryWrapper);
                if (count > 0) {
                    return Result.fail("该类型已被咨询师使用，无法删除！");
                }
                conTypeService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("类型id不能为空！");
        }
    }

}
