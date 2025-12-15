package com.project.counseling.controller.ask;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.config.utils.ShiroUtils;
import com.project.counseling.domain.ConAskComment;
import com.project.counseling.domain.Result;
import com.project.counseling.domain.User;
import com.project.counseling.service.ConAskCommentService;
import com.project.counseling.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 问答评论controller
 * @date 2025/11/17 09:52
 */
@Controller
@ResponseBody
@RequestMapping("comment")
public class ConAskCommentController {

    @Autowired
    private ConAskCommentService conAskCommentService;
    @Autowired
    private UserService userService;

    /** 分页获取问答评论 */
    @PostMapping("getConAskCommentPage")
    public Result getConAskCommentPage(@RequestBody ConAskComment conAskComment) {
        Page<ConAskComment> page = new Page<>(conAskComment.getPageNumber(),conAskComment.getPageSize());
        QueryWrapper<ConAskComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(conAskComment.getAskId()),ConAskComment::getAskId,conAskComment.getAskId())
                .like(StringUtils.isNotBlank(conAskComment.getContent()),ConAskComment::getContent,conAskComment.getContent())
                .eq(StringUtils.isNotBlank(conAskComment.getUserId()),ConAskComment::getUserId,conAskComment.getUserId())
                .like(StringUtils.isNotBlank(conAskComment.getCreateBy()),ConAskComment::getCreateBy,conAskComment.getCreateBy())
                .orderByDesc(ConAskComment::getCreateTime);
        Page<ConAskComment> conAskCommentPage = conAskCommentService.page(page, queryWrapper);
        for (ConAskComment comment : conAskCommentPage.getRecords()) {
            User user = userService.getById(comment.getUserId());
            comment.setAvatar(user.getAvatar());
        }
        return Result.success(conAskCommentPage);
    }

    /** 根据id获取问答评论 */
    @GetMapping("getConAskCommentById")
    public Result getConAskCommentById(@RequestParam("id")String id) {
        ConAskComment conAskComment = conAskCommentService.getById(id);
        return Result.success(conAskComment);
    }

    /** 保存问答评论 */
    @PostMapping("saveConAskComment")
    public Result saveConAskComment(@RequestBody ConAskComment conAskComment) {
        User userInfo = ShiroUtils.getUserInfo();
        conAskComment.setUserId(userInfo.getId());
        boolean save = conAskCommentService.save(conAskComment);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑问答评论 */
    @PostMapping("editConAskComment")
    public Result editConAskComment(@RequestBody ConAskComment conAskComment) {
        boolean save = conAskCommentService.updateById(conAskComment);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除问答评论 */
    @GetMapping("removeConAskComment")
    public Result removeConAskComment(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conAskCommentService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("问答评论id不能为空！");
        }
    }

}
