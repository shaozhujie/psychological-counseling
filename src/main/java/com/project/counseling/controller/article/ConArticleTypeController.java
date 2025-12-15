package com.project.counseling.controller.article;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.domain.ConArticle;
import com.project.counseling.domain.ConArticleType;
import com.project.counseling.domain.Result;
import com.project.counseling.service.ConArticleService;
import com.project.counseling.service.ConArticleTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 文章类别controller
 * @date 2025/11/15 03:29
 */
@Controller
@ResponseBody
@RequestMapping("type")
public class ConArticleTypeController {

    @Autowired
    private ConArticleTypeService conArticleTypeService;
    @Autowired
    private ConArticleService conArticleService;

    /** 分页获取文章类别 */
    @PostMapping("getConArticleTypePage")
    public Result getConArticleTypePage(@RequestBody ConArticleType conArticleType) {
        Page<ConArticleType> page = new Page<>(conArticleType.getPageNumber(),conArticleType.getPageSize());
        QueryWrapper<ConArticleType> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(conArticleType.getName()),ConArticleType::getName,conArticleType.getName());
        Page<ConArticleType> conArticleTypePage = conArticleTypeService.page(page, queryWrapper);
        return Result.success(conArticleTypePage);
    }

    @GetMapping("getConArticleTypeList")
    public Result getConArticleTypeList() {
        List<ConArticleType> list = conArticleTypeService.list();
        return Result.success(list);
    }

    /** 根据id获取文章类别 */
    @GetMapping("getConArticleTypeById")
    public Result getConArticleTypeById(@RequestParam("id")String id) {
        ConArticleType conArticleType = conArticleTypeService.getById(id);
        return Result.success(conArticleType);
    }

    /** 保存文章类别 */
    @PostMapping("saveConArticleType")
    public Result saveConArticleType(@RequestBody ConArticleType conArticleType) {
        boolean save = conArticleTypeService.save(conArticleType);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑文章类别 */
    @PostMapping("editConArticleType")
    public Result editConArticleType(@RequestBody ConArticleType conArticleType) {
        boolean save = conArticleTypeService.updateById(conArticleType);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除文章类别 */
    @GetMapping("removeConArticleType")
    public Result removeConArticleType(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conArticleTypeService.removeById(id);
                QueryWrapper<ConArticle> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ConArticle::getTypeId,id);
                int count = conArticleService.count(queryWrapper);
                if (count > 0) {
                    return Result.fail("该类别下存在文章，不能删除！");
                }
            }
            return Result.success();
        } else {
            return Result.fail("文章类别id不能为空！");
        }
    }

}
