package com.project.counseling.controller.book;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.domain.ConBook;
import com.project.counseling.domain.Result;
import com.project.counseling.service.ConBookService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 证书类别controller
 * @date 2025/11/14 03:48
 */
@Controller
@ResponseBody
@RequestMapping("book")
public class ConBookController {

    @Autowired
    private ConBookService conBookService;

    /** 分页获取证书类别 */
    @PostMapping("getConBookPage")
    public Result getConBookPage(@RequestBody ConBook conBook) {
        Page<ConBook> page = new Page<>(conBook.getPageNumber(),conBook.getPageSize());
        QueryWrapper<ConBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(conBook.getName()),ConBook::getName,conBook.getName());
        Page<ConBook> conBookPage = conBookService.page(page, queryWrapper);
        return Result.success(conBookPage);
    }

    @GetMapping("getConBookList")
    public Result getConBookList() {
        List<ConBook> list = conBookService.list();
        return Result.success(list);
    }

    /** 根据id获取证书类别 */
    @GetMapping("getConBookById")
    public Result getConBookById(@RequestParam("id")String id) {
        ConBook conBook = conBookService.getById(id);
        return Result.success(conBook);
    }

    /** 保存证书类别 */
    @PostMapping("saveConBook")
    public Result saveConBook(@RequestBody ConBook conBook) {
        boolean save = conBookService.save(conBook);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑证书类别 */
    @PostMapping("editConBook")
    public Result editConBook(@RequestBody ConBook conBook) {
        boolean save = conBookService.updateById(conBook);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除证书类别 */
    @GetMapping("removeConBook")
    public Result removeConBook(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conBookService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("证书类别id不能为空！");
        }
    }

}
