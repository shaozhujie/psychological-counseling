package com.project.counseling.controller.report;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.domain.ConReport;
import com.project.counseling.domain.ConReportImage;
import com.project.counseling.domain.ConReportItem;
import com.project.counseling.domain.Result;
import com.project.counseling.service.ConReportImageService;
import com.project.counseling.service.ConReportItemService;
import com.project.counseling.service.ConReportService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 心理健康报告controller
 * @date 2025/11/15 11:39
 */
@Controller
@ResponseBody
@RequestMapping("report")
public class ConReportController {

    @Autowired
    private ConReportService conReportService;
    @Autowired
    private ConReportImageService conReportImageService;
    @Autowired
    private ConReportItemService conReportItemService;

    /** 分页获取心理健康报告 */
    @PostMapping("getConReportPage")
    public Result getConReportPage(@RequestBody ConReport conReport) {
        Page<ConReport> page = new Page<>(conReport.getPageNumber(),conReport.getPageSize());
        QueryWrapper<ConReport> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(conReport.getTitle()),ConReport::getTitle,conReport.getTitle())
                .eq(StringUtils.isNotBlank(conReport.getName()),ConReport::getName,conReport.getName());
        Page<ConReport> conReportPage = conReportService.page(page, queryWrapper);
        return Result.success(conReportPage);
    }

    /** 根据id获取心理健康报告 */
    @GetMapping("getConReportById")
    public Result getConReportById(@RequestParam("id")String id) {
        ConReport conReport = conReportService.getById(id);
        return Result.success(conReport);
    }

    /** 保存心理健康报告 */
    @PostMapping("saveConReport")
    public Result saveConReport(@RequestBody ConReport conReport) {
        boolean save = conReportService.save(conReport);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑心理健康报告 */
    @PostMapping("editConReport")
    public Result editConReport(@RequestBody ConReport conReport) {
        boolean save = conReportService.updateById(conReport);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除心理健康报告 */
    @GetMapping("removeConReport")
    public Result removeConReport(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conReportService.removeById(id);
                QueryWrapper<ConReportItem> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ConReportItem::getReportId,id);
                List<ConReportItem> itemList = conReportItemService.list(queryWrapper);
                for (ConReportItem item : itemList) {
                    conReportItemService.removeById(item.getId());
                    QueryWrapper<ConReportImage> wrapper = new QueryWrapper<>();
                    wrapper.lambda().eq(ConReportImage::getItemId,item.getId());
                    conReportImageService.remove(wrapper);
                }
            }
            return Result.success();
        } else {
            return Result.fail("心理健康报告id不能为空！");
        }
    }

}
