package com.project.counseling.controller.article;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.config.utils.ShiroUtils;
import com.project.counseling.domain.ConArticle;
import com.project.counseling.domain.ConArticleGood;
import com.project.counseling.domain.Result;
import com.project.counseling.domain.User;
import com.project.counseling.service.ConArticleGoodService;
import com.project.counseling.service.ConArticleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 文章点赞controller
 * @date 2025/11/15 05:13
 */
@Controller
@ResponseBody
@RequestMapping("good")
public class ConArticleGoodController {

    @Autowired
    private ConArticleGoodService conArticleGoodService;
    @Autowired
    private ConArticleService conArticleService;

    /** 分页获取文章点赞 */
    @PostMapping("getConArticleGoodPage")
    public Result getConArticleGoodPage(@RequestBody ConArticleGood conArticleGood) {
        Page<ConArticleGood> page = new Page<>(conArticleGood.getPageNumber(),conArticleGood.getPageSize());
        QueryWrapper<ConArticleGood> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(conArticleGood.getArticleId()),ConArticleGood::getArticleId,conArticleGood.getArticleId())
                .eq(StringUtils.isNotBlank(conArticleGood.getUserId()),ConArticleGood::getUserId,conArticleGood.getUserId());
        Page<ConArticleGood> conArticleGoodPage = conArticleGoodService.page(page, queryWrapper);
        return Result.success(conArticleGoodPage);
    }

    /** 根据id获取文章点赞 */
    @GetMapping("getConArticleGoodById")
    public Result getConArticleGoodById(@RequestParam("id")String id) {
        ConArticleGood conArticleGood = conArticleGoodService.getById(id);
        return Result.success(conArticleGood);
    }

    /** 保存文章点赞 */
    @PostMapping("saveConArticleGood")
    public Result saveConArticleGood(@RequestBody ConArticleGood conArticleGood) {
        User userInfo = ShiroUtils.getUserInfo();
        QueryWrapper<ConArticleGood> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConArticleGood::getArticleId,conArticleGood.getArticleId())
                .eq(ConArticleGood::getUserId,userInfo.getId());
        int count = conArticleGoodService.count(queryWrapper);
        if (count > 0) {
            return Result.fail("该文章已点赞！");
        }
        conArticleGood.setUserId(userInfo.getId());
        boolean save = conArticleGoodService.save(conArticleGood);
        if (save) {
            ConArticle article = conArticleService.getById(conArticleGood.getArticleId());
            article.setGood(article.getGood() + 1);
            conArticleService.updateById(article);
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑文章点赞 */
    @PostMapping("editConArticleGood")
    public Result editConArticleGood(@RequestBody ConArticleGood conArticleGood) {
        boolean save = conArticleGoodService.updateById(conArticleGood);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除文章点赞 */
    @GetMapping("removeConArticleGood")
    public Result removeConArticleGood(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conArticleGoodService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("文章点赞id不能为空！");
        }
    }

}
