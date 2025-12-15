package com.project.counseling.controller.ai;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.config.utils.ShiroUtils;
import com.project.counseling.domain.ConAi;
import com.project.counseling.domain.Result;
import com.project.counseling.domain.User;
import com.project.counseling.service.ConAiService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: AI对话controller
 * @date 2025/11/15 03:25
 */
@Controller
@ResponseBody
@RequestMapping("ai")
public class ConAiController {

    @Autowired
    private ConAiService conAiService;
    @Autowired
    private RestTemplate restTemplate;

    public static String header = "Bearer TetLpoAlInseqgQSbRYV:fyBDaLuvteHjDiJVzNAr";

    public static String url = "https://spark-api-open.xf-yun.com/v1/chat/completions";

    /** 分页获取AI对话 */
    @PostMapping("getConAiPage")
    public Result getConAiPage(@RequestBody ConAi conAi) {
        Page<ConAi> page = new Page<>(conAi.getPageNumber(),conAi.getPageSize());
        QueryWrapper<ConAi> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(conAi.getUserId()),ConAi::getUserId,conAi.getUserId())
                .eq(StringUtils.isNotBlank(conAi.getSendId()),ConAi::getSendId,conAi.getSendId())
                .like(StringUtils.isNotBlank(conAi.getContent()),ConAi::getContent,conAi.getContent());
        Page<ConAi> conAiPage = conAiService.page(page, queryWrapper);
        return Result.success(conAiPage);
    }

    @GetMapping("getAiList")
    public Result getAiList() {
        User userInfo = ShiroUtils.getUserInfo();
        QueryWrapper<ConAi> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConAi::getUserId,userInfo.getId())
                .orderByAsc(ConAi::getCreateTime);
        List<ConAi> list = conAiService.list(queryWrapper);
        return Result.success(list);
    }

    /** 根据id获取AI对话 */
    @GetMapping("getConAiById")
    public Result getConAiById(@RequestParam("id")String id) {
        ConAi conAi = conAiService.getById(id);
        return Result.success(conAi);
    }

    /** 保存AI对话 */
    @PostMapping("saveConAi")
    @Transactional(rollbackFor = Exception.class)
    public Result saveConAi(@RequestBody ConAi conAi){
        User userInfo = ShiroUtils.getUserInfo();
        conAi.setUserId(userInfo.getId());
        conAi.setSendId(userInfo.getId());
        boolean save = conAiService.save(conAi);
        // 保存之后调用星火大模型
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // 设置内容类型为JSON
        headers.set("Authorization", header);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("model", "4.0Ultra");
        List<JSONObject> list = new ArrayList<>();
        JSONObject item = new JSONObject();
        item.put("role","system");
        item.put("content","你是知识渊博的心理咨询师");
        list.add(item);
        JSONObject item1 = new JSONObject();
        item1.put("role","user");
        item1.put("content",conAi.getContent());
        list.add(item1);
        jsonObject.put("messages", list);
        jsonObject.put("stream", false);
        HttpEntity<String> entity = new HttpEntity<>(jsonObject.toJSONString(), headers);
        ResponseEntity<JSONObject> postedForEntity = restTemplate.postForEntity(url, entity, JSONObject.class);
        JSONObject body = postedForEntity.getBody();
        Integer code = body.getInteger("code");
        if (code == 0) {
            JSONArray choices = body.getJSONArray("choices");
            if (!choices.isEmpty()) {
                JSONObject object = choices.getJSONObject(0);
                String content = object.getJSONObject("message").getString("content");
                ConAi conAi1 = new ConAi();
                conAi1.setUserId(userInfo.getId());
                conAi1.setSendId("ai");
                conAi1.setContent(content);
                conAiService.save(conAi1);
            }
        } else {
            return Result.fail("消息发送失败");
        }
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑AI对话 */
    @PostMapping("editConAi")
    public Result editConAi(@RequestBody ConAi conAi) {
        boolean save = conAiService.updateById(conAi);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除AI对话 */
    @GetMapping("removeConAi")
    public Result removeConAi(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conAiService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("AI对话id不能为空！");
        }
    }

}
