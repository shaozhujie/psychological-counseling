package com.project.counseling.controller.image;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.domain.ConReportImage;
import com.project.counseling.domain.Result;
import com.project.counseling.service.ConReportImageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 心理健康报告图片controller
 * @date 2025/11/15 02:19
 */
@Controller
@ResponseBody
@RequestMapping("image")
public class ConReportImageController {

    @Autowired
    private ConReportImageService conReportImageService;

    /** 分页获取心理健康报告图片 */
    @PostMapping("getConReportImagePage")
    public Result getConReportImagePage(@RequestBody ConReportImage conReportImage) {
        Page<ConReportImage> page = new Page<>(conReportImage.getPageNumber(),conReportImage.getPageSize());
        QueryWrapper<ConReportImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(conReportImage.getItemId()),ConReportImage::getItemId,conReportImage.getItemId())
                .like(StringUtils.isNotBlank(conReportImage.getTitle()),ConReportImage::getTitle,conReportImage.getTitle());
        Page<ConReportImage> conReportImagePage = conReportImageService.page(page, queryWrapper);
        return Result.success(conReportImagePage);
    }

    /** 根据id获取心理健康报告图片 */
    @GetMapping("getConReportImageById")
    public Result getConReportImageById(@RequestParam("id")String id) {
        ConReportImage conReportImage = conReportImageService.getById(id);
        return Result.success(conReportImage);
    }

    /** 保存心理健康报告图片 */
    @PostMapping("saveConReportImage")
    public Result saveConReportImage(@RequestBody ConReportImage conReportImage) {
        boolean save = conReportImageService.save(conReportImage);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑心理健康报告图片 */
    @PostMapping("editConReportImage")
    public Result editConReportImage(@RequestBody ConReportImage conReportImage) {
        boolean save = conReportImageService.updateById(conReportImage);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除心理健康报告图片 */
    @GetMapping("removeConReportImage")
    public Result removeConReportImage(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conReportImageService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("心理健康报告图片id不能为空！");
        }
    }

}
