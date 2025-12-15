package com.project.counseling.controller.article;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.config.utils.ShiroUtils;
import com.project.counseling.domain.*;
import com.project.counseling.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 文章controller
 * @date 2025/11/15 04:29
 */
@Controller
@ResponseBody
@RequestMapping("article")
public class ConArticleController {

    @Autowired
    private ConArticleService conArticleService;
    @Autowired
    private ConArticleTypeService conArticleTypeService;
    @Autowired
    private UserService userService;
    @Autowired
    private ConArticleCommentService conArticleCommentService;
    @Autowired
    private ConArticleGoodService conArticleGoodService;

    /** 分页获取文章 */
    @PostMapping("getConArticlePage")
    public Result getConArticlePage(@RequestBody ConArticle conArticle) {
        User userInfo = ShiroUtils.getUserInfo();
        if (userInfo.getUserType() == 2) {
            conArticle.setUserId(userInfo.getId());
        }
        Page<ConArticle> page = new Page<>(conArticle.getPageNumber(),conArticle.getPageSize());
        QueryWrapper<ConArticle> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(conArticle.getTypeId()),ConArticle::getTypeId,conArticle.getTypeId())
                .eq(StringUtils.isNotBlank(conArticle.getUserId()),ConArticle::getUserId,conArticle.getUserId())
                .like(StringUtils.isNotBlank(conArticle.getTitle()),ConArticle::getTitle,conArticle.getTitle())
                .orderByDesc(ConArticle::getCreateTime);
        Page<ConArticle> conArticlePage = conArticleService.page(page, queryWrapper);
        for (ConArticle article : conArticlePage.getRecords()) {
            ConArticleType articleType = conArticleTypeService.getById(article.getTypeId());
            article.setTypeId(articleType.getName());
            User user = userService.getById(article.getUserId());
            article.setAvatar(user.getAvatar());
        }
        return Result.success(conArticlePage);
    }

    @GetMapping("getConArticleByUserId")
    public Result getConArticleByUserId(@RequestParam("userId")String userId) {
        QueryWrapper<ConArticle> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ConArticle::getUserId,userId)
                .orderByDesc(ConArticle::getCreateTime);
        return Result.success(conArticleService.list(queryWrapper));
    }

    /** 根据id获取文章 */
    @GetMapping("getConArticleById")
    public Result getConArticleById(@RequestParam("id")String id) {
        ConArticle conArticle = conArticleService.getById(id);
        User user = userService.getById(conArticle.getUserId());
        conArticle.setAvatar(user.getAvatar());
        return Result.success(conArticle);
    }

    /** 保存文章 */
    @PostMapping("saveConArticle")
    public Result saveConArticle(@RequestBody ConArticle conArticle) {
        User userInfo = ShiroUtils.getUserInfo();
        conArticle.setUserId(userInfo.getId());
        boolean save = conArticleService.save(conArticle);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑文章 */
    @PostMapping("editConArticle")
    public Result editConArticle(@RequestBody ConArticle conArticle) {
        boolean save = conArticleService.updateById(conArticle);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除文章 */
    @GetMapping("removeConArticle")
    public Result removeConArticle(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conArticleService.removeById(id);
                QueryWrapper<ConArticleComment> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ConArticleComment::getArticleId,id);
                conArticleCommentService.remove(queryWrapper);
                QueryWrapper<ConArticleGood> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(ConArticleGood::getArticleId,id);
                conArticleGoodService.remove(wrapper);
            }
            return Result.success();
        } else {
            return Result.fail("文章id不能为空！");
        }
    }

    @GetMapping("addLook")
    public Result addLook(@RequestParam("id")String id) {
        if (StringUtils.isNotBlank(id)) {
            ConArticle conArticle = conArticleService.getById(id);
            conArticle.setLook(conArticle.getLook() + 1);
            conArticleService.updateById(conArticle);
        }
        return Result.success();
    }

}
