package com.project.counseling.controller.article;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.config.utils.ShiroUtils;
import com.project.counseling.domain.ConArticleComment;
import com.project.counseling.domain.Result;
import com.project.counseling.domain.User;
import com.project.counseling.service.ConArticleCommentService;
import com.project.counseling.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 文章评论controller
 * @date 2025/11/15 05:17
 */
@Controller
@ResponseBody
@RequestMapping("comment")
public class ConArticleCommentController {

    @Autowired
    private ConArticleCommentService conArticleCommentService;
    @Autowired
    private UserService userService;

    /** 分页获取文章评论 */
    @PostMapping("getConArticleCommentPage")
    public Result getConArticleCommentPage(@RequestBody ConArticleComment conArticleComment) {
        Page<ConArticleComment> page = new Page<>(conArticleComment.getPageNumber(),conArticleComment.getPageSize());
        QueryWrapper<ConArticleComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(conArticleComment.getContent()),ConArticleComment::getContent,conArticleComment.getContent())
                .eq(StringUtils.isNotBlank(conArticleComment.getUserId()),ConArticleComment::getUserId,conArticleComment.getUserId())
                .eq(StringUtils.isNotBlank(conArticleComment.getArticleId()),ConArticleComment::getArticleId,conArticleComment.getArticleId())
                .like(StringUtils.isNotBlank(conArticleComment.getCreateBy()),ConArticleComment::getCreateBy,conArticleComment.getCreateBy())
                .orderByDesc(ConArticleComment::getCreateTime);
        Page<ConArticleComment> conArticleCommentPage = conArticleCommentService.page(page, queryWrapper);
        for (ConArticleComment articleComment : conArticleCommentPage.getRecords()) {
            User user = userService.getById(articleComment.getUserId());
            articleComment.setAvatar(user.getAvatar());
        }
        return Result.success(conArticleCommentPage);
    }

    @GetMapping("getConArticleCommentByArticleId")
    public Result getConArticleCommentByArticleId(@RequestParam("articleId")String articleId) {
        QueryWrapper<ConArticleComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ConArticleComment::getArticleId,articleId).orderByDesc(ConArticleComment::getCreateTime);
        return Result.success(conArticleCommentService.list(queryWrapper));
    }

    /** 根据id获取文章评论 */
    @GetMapping("getConArticleCommentById")
    public Result getConArticleCommentById(@RequestParam("id")String id) {
        ConArticleComment conArticleComment = conArticleCommentService.getById(id);
        return Result.success(conArticleComment);
    }

    /** 保存文章评论 */
    @PostMapping("saveConArticleComment")
    public Result saveConArticleComment(@RequestBody ConArticleComment conArticleComment) {
        User userInfo = ShiroUtils.getUserInfo();
        conArticleComment.setUserId(userInfo.getId());
        boolean save = conArticleCommentService.save(conArticleComment);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑文章评论 */
    @PostMapping("editConArticleComment")
    public Result editConArticleComment(@RequestBody ConArticleComment conArticleComment) {
        boolean save = conArticleCommentService.updateById(conArticleComment);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除文章评论 */
    @GetMapping("removeConArticleComment")
    public Result removeConArticleComment(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conArticleCommentService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("文章评论id不能为空！");
        }
    }

}