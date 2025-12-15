package com.project.counseling.controller.ask;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.config.utils.ShiroUtils;
import com.project.counseling.domain.*;
import com.project.counseling.service.ConArticleCommentService;
import com.project.counseling.service.ConAskCommentService;
import com.project.counseling.service.ConAskService;
import com.project.counseling.service.ConHugService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 问答controller
 * @date 2025/11/17 09:32
 */
@Controller
@ResponseBody
@RequestMapping("ask")
public class ConAskController {

    @Autowired
    private ConAskService conAskService;
    @Autowired
    private ConAskCommentService conAskCommentService;
    @Autowired
    private ConHugService conHugService;

    /** 分页获取问答 */
    @PostMapping("getConAskPage")
    public Result getConAskPage(@RequestBody ConAsk conAsk) {
        Page<ConAsk> page = new Page<>(conAsk.getPageNumber(),conAsk.getPageSize());
        QueryWrapper<ConAsk> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(conAsk.getUserId()),ConAsk::getUserId,conAsk.getUserId())
                .like(StringUtils.isNotBlank(conAsk.getTitle()),ConAsk::getTitle,conAsk.getTitle())
                .like(StringUtils.isNotBlank(conAsk.getContent()),ConAsk::getContent,conAsk.getContent())
                .eq(StringUtils.isNotBlank(conAsk.getCreateBy()),ConAsk::getCreateBy,conAsk.getCreateBy())
                .orderByDesc(ConAsk::getCreateTime);
        Page<ConAsk> conAskPage = conAskService.page(page, queryWrapper);
        for (ConAsk ask : conAskPage.getRecords()) {
            QueryWrapper<ConAskComment> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(ConAskComment::getAskId,ask.getId());
            ask.setComment(conAskCommentService.count(wrapper));
        }
        return Result.success(conAskPage);
    }

    /** 根据id获取问答 */
    @GetMapping("getConAskById")
    public Result getConAskById(@RequestParam("id")String id) {
        ConAsk conAsk = conAskService.getById(id);
        return Result.success(conAsk);
    }

    /** 保存问答 */
    @PostMapping("saveConAsk")
    public Result saveConAsk(@RequestBody ConAsk conAsk) {
        User userInfo = ShiroUtils.getUserInfo();
        conAsk.setUserId(userInfo.getId());
        boolean save = conAskService.save(conAsk);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑问答 */
    @PostMapping("editConAsk")
    public Result editConAsk(@RequestBody ConAsk conAsk) {
        boolean save = conAskService.updateById(conAsk);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除问答 */
    @GetMapping("removeConAsk")
    public Result removeConAsk(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conAskService.removeById(id);
                QueryWrapper<ConHug> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ConHug::getAskId,id);
                conHugService.remove(queryWrapper);
                QueryWrapper<ConAskComment> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(ConAskComment::getAskId,id);
                conAskCommentService.remove(wrapper);
            }
            return Result.success();
        } else {
            return Result.fail("问答id不能为空！");
        }
    }

    @GetMapping("getUserAskCount")
    public Result getUserAskCount() {
        User userInfo = ShiroUtils.getUserInfo();
        QueryWrapper<ConAsk> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConAsk::getUserId,userInfo.getId());
        int count = conAskService.count(queryWrapper);
        return Result.success(count);
    }

    @GetMapping("getAskFive")
    public Result getAskFive() {
        QueryWrapper<ConAsk> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(ConAsk::getHug).last("limit 5");
        List<ConAsk> list = conAskService.list(queryWrapper);
        for (ConAsk ask : list) {
            QueryWrapper<ConAskComment> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(ConAskComment::getAskId,ask.getId());
            List<ConAskComment> commentList = conAskCommentService.list(wrapper);
            if (commentList != null && commentList.size() > 0) {
                ask.setConAskComment(commentList);
            } else {
                List<ConAskComment> list1 = new ArrayList<>();
                ConAskComment conAskComment = new ConAskComment();
                conAskComment.setContent("暂无回答");
                list1.add(conAskComment);
                ask.setConAskComment(list1);
            }
        }
        return Result.success(list);
    }

}
