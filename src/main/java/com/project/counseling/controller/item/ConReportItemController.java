package com.project.counseling.controller.item;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.domain.ConReportImage;
import com.project.counseling.domain.ConReportItem;
import com.project.counseling.domain.Result;
import com.project.counseling.service.ConReportImageService;
import com.project.counseling.service.ConReportItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 心理健康报告标题controller
 * @date 2025/11/15 01:45
 */
@Controller
@ResponseBody
@RequestMapping("item")
public class ConReportItemController {

    @Autowired
    private ConReportItemService conReportItemService;
    @Autowired
    private ConReportImageService conReportImageService;

    /** 分页获取心理健康报告标题 */
    @PostMapping("getConReportItemPage")
    public Result getConReportItemPage(@RequestBody ConReportItem conReportItem) {
        Page<ConReportItem> page = new Page<>(conReportItem.getPageNumber(),conReportItem.getPageSize());
        QueryWrapper<ConReportItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(conReportItem.getReportId()),ConReportItem::getReportId,conReportItem.getReportId())
                .like(StringUtils.isNotBlank(conReportItem.getTitle()),ConReportItem::getTitle,conReportItem.getTitle());
        Page<ConReportItem> conReportItemPage = conReportItemService.page(page, queryWrapper);
        return Result.success(conReportItemPage);
    }

    @GetMapping("getConReportItemByReportId")
    public Result getConReportItemByReportId(@RequestParam("reportId")String reportId) {
        QueryWrapper<ConReportItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(reportId),ConReportItem::getReportId,reportId);
        List<ConReportItem> list = conReportItemService.list(queryWrapper);
        for (ConReportItem item : list) {
            QueryWrapper<ConReportImage> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(ConReportImage::getItemId,item.getId());
            List<ConReportImage> images = conReportImageService.list(wrapper);
            item.setConReportImageList(images);
        }
        return Result.success(list);
    }

    /** 根据id获取心理健康报告标题 */
    @GetMapping("getConReportItemById")
    public Result getConReportItemById(@RequestParam("id")String id) {
        ConReportItem conReportItem = conReportItemService.getById(id);
        return Result.success(conReportItem);
    }

    /** 保存心理健康报告标题 */
    @PostMapping("saveConReportItem")
    public Result saveConReportItem(@RequestBody ConReportItem conReportItem) {
        boolean save = conReportItemService.save(conReportItem);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑心理健康报告标题 */
    @PostMapping("editConReportItem")
    public Result editConReportItem(@RequestBody ConReportItem conReportItem) {
        boolean save = conReportItemService.updateById(conReportItem);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除心理健康报告标题 */
    @GetMapping("removeConReportItem")
    public Result removeConReportItem(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conReportItemService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("心理健康报告标题id不能为空！");
        }
    }

}
