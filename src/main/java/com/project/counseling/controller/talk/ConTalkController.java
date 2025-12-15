package com.project.counseling.controller.talk;

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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 在线咨询controller
 * @date 2025/11/19 04:09
 */
@Controller
@ResponseBody
@RequestMapping("talk")
public class ConTalkController {

    @Autowired
    private ConTalkService conTalkService;
    @Autowired
    private ConAppointmentService conAppointmentService;
    @Autowired
    private UserService userService;
    @Autowired
    private ConExamService conExamService;
    @Autowired
    private ConResultService conResultService;

    /** 分页获取在线咨询 */
    @PostMapping("getConTalkPage")
    public Result getConTalkPage(@RequestBody ConTalk conTalk) {
        Page<ConTalk> page = new Page<>(conTalk.getPageNumber(),conTalk.getPageSize());
        QueryWrapper<ConTalk> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(conTalk.getAppointmentId()),ConTalk::getAppointmentId,conTalk.getAppointmentId())
                .eq(StringUtils.isNotBlank(conTalk.getUserId()),ConTalk::getUserId,conTalk.getUserId())
                .eq(StringUtils.isNotBlank(conTalk.getSendId()),ConTalk::getSendId,conTalk.getSendId())
                .eq(StringUtils.isNotBlank(conTalk.getContent()),ConTalk::getContent,conTalk.getContent())
                .eq(StringUtils.isNotBlank(conTalk.getExamId()),ConTalk::getExamId,conTalk.getExamId())
                .eq(StringUtils.isNotBlank(conTalk.getImage()),ConTalk::getImage,conTalk.getImage())
                .eq(conTalk.getType() != null,ConTalk::getType,conTalk.getType())
                .eq(StringUtils.isNotBlank(conTalk.getCreateBy()),ConTalk::getCreateBy,conTalk.getCreateBy())
                .eq(conTalk.getCreateTime() != null,ConTalk::getCreateTime,conTalk.getCreateTime())
                .eq(StringUtils.isNotBlank(conTalk.getUpdateBy()),ConTalk::getUpdateBy,conTalk.getUpdateBy())
                .eq(conTalk.getUpdateTime() != null,ConTalk::getUpdateTime,conTalk.getUpdateTime());
        Page<ConTalk> conTalkPage = conTalkService.page(page, queryWrapper);
        return Result.success(conTalkPage);
    }

    @GetMapping("getConTalkList")
    public Result getConTalkList(@RequestParam("appointmentId")String appointmentId) {
        QueryWrapper<ConTalk> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConTalk::getAppointmentId,appointmentId)
                .orderByAsc(ConTalk::getCreateTime);
        List<ConTalk> talkList = conTalkService.list(queryWrapper);
        for (ConTalk conTalk : talkList) {
            if (conTalk.getType() == 2) {
                QueryWrapper<ConResult> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(ConResult::getExamId,conTalk.getExamId())
                        .eq(ConResult::getUserId,conTalk.getUserId()).last("limit 1");
                ConResult result = conResultService.getOne(wrapper);
                if (result != null) {
                    conTalk.setResultId(result.getId());
                } else {
                    conTalk.setResultId("");
                }
            }
        }
        return Result.success(talkList);
    }

    /** 根据id获取在线咨询 */
    @GetMapping("getConTalkById")
    public Result getConTalkById(@RequestParam("id")String id) {
        ConTalk conTalk = conTalkService.getById(id);
        return Result.success(conTalk);
    }

    /** 保存在线咨询 */
    @PostMapping("saveConTalk")
    public Result saveConTalk(@RequestBody ConTalk conTalk) {
        ConAppointment appointment = conAppointmentService.getById(conTalk.getAppointmentId());
        if (appointment.getState() != 0) {
            return Result.fail("咨询已结束");
        }
        conTalk.setUserId(appointment.getUserId());
        User userInfo = ShiroUtils.getUserInfo();
        if (userInfo.getUserType() == 1) {
            conTalk.setSendId(appointment.getUserId());
        } else {
            conTalk.setSendId(appointment.getConsultId());
        }
        if (conTalk.getType() == 2) {
            ConExam exam = conExamService.getById(conTalk.getExamId());
            conTalk.setExam(exam.getTitle());
            conTalk.setCover(exam.getCover());
        }
        boolean save = conTalkService.save(conTalk);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    @PostMapping("saveConTalkPic/{appointmentId}/{userId}")
    public Result saveConTalkPic(@PathVariable("userId") String userId,@PathVariable("appointmentId") String appointmentId,@RequestParam("file") MultipartFile img) {
        if(img.isEmpty()){
            return Result.fail("上传的图片不能为空!");
        }
        String coverType = img.getOriginalFilename().substring(img.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
        if ("jpeg".equals(coverType)  || "gif".equals(coverType) || "png".equals(coverType) || "bmp".equals(coverType)  || "jpg".equals(coverType)) {
            //文件名=当前时间到毫秒+原来的文件名
            int index = img.getOriginalFilename().lastIndexOf(".");
            String substring = img.getOriginalFilename().substring(index);
            String fileName = System.currentTimeMillis() + substring;
            //文件路径
            String filePath = System.getProperty("user.dir")+System.getProperty("file.separator")+"img";
            //如果文件路径不存在，新增该路径
            File file1 = new File(filePath);
            if(!file1.exists()){
                boolean mkdir = file1.mkdir();
            }
            //实际的文件地址
            File dest = new File(filePath + System.getProperty("file.separator") + fileName);
            //存储到数据库里的相对文件地址
            String storeImgPath = "/img/"+fileName;
            try {
                img.transferTo(dest);
                ConTalk conTalk = new ConTalk();
                ConAppointment appointment = conAppointmentService.getById(appointmentId);
                if (appointment.getState() != 0) {
                    return Result.fail("咨询已结束");
                }
                conTalk.setUserId(appointment.getUserId());
                User userInfo = userService.getById(userId);
                if (userInfo.getUserType() == 1) {
                    conTalk.setSendId(appointment.getUserId());
                } else {
                    conTalk.setSendId(appointment.getConsultId());
                }
                conTalk.setType(1);
                conTalk.setImage(storeImgPath);
                conTalk.setAppointmentId(appointmentId);
                conTalkService.save(conTalk);
                return Result.success(storeImgPath);
            } catch (IOException e) {
                return Result.fail("上传失败");
            }
        } else {
            return Result.fail("请选择正确的图片格式");
        }
    }

    /** 编辑在线咨询 */
    @PostMapping("editConTalk")
    public Result editConTalk(@RequestBody ConTalk conTalk) {
        boolean save = conTalkService.updateById(conTalk);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除在线咨询 */
    @GetMapping("removeConTalk")
    public Result removeConTalk(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                conTalkService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("在线咨询id不能为空！");
        }
    }

}
