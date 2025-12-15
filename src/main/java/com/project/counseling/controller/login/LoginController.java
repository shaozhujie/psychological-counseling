package com.project.counseling.controller.login;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.counseling.common.utils.JwtUtil;
import com.project.counseling.common.utils.PasswordUtils;
import com.project.counseling.config.utils.RedisUtils;
import com.project.counseling.config.utils.ShiroUtils;
import com.project.counseling.domain.*;
import com.project.counseling.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @version 1.0
 * @description: 登陆
 * @date 2024/2/26 21:20
 */
@Controller
@ResponseBody
@RequestMapping("login")
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private ConAppointmentService conAppointmentService;
    @Autowired
    private ConArticleService conArticleService;
    @Autowired
    private ConAskService conAskService;
    @Autowired
    private RedisUtils redisUtils;

    @PostMapping()
    public Result login(HttpServletRequest request, @RequestBody JSONObject jsonObject) {
        String username = jsonObject.getString("loginAccount");
        String password = jsonObject.getString("password");
        Integer type = jsonObject.getInteger("type");
        QueryWrapper<User> query = new QueryWrapper<>();
        query.lambda().eq(User::getLoginAccount,username);
        if (type == 0) {
            query.lambda().eq(User::getUserType,1);
        } else {
            query.lambda().ne(User::getUserType,1);
        }
        User user = userService.getOne(query);
        if (user == null) {
            return Result.fail("用户名不存在！");
        }
        //比较加密后得密码
        boolean decrypt = PasswordUtils.decrypt(password, user.getPassword() + "$" + user.getSalt());
        if (!decrypt) {
            return Result.fail("用户名或密码错误！");
        }
        if (user.getStatus() == 1) {
            return Result.fail("用户被禁用！");
        }
        //密码正确生成token返回
        String token = JwtUtil.sign(user.getId(), user.getPassword());
        JSONObject json = new JSONObject();
        json.put("token", token);
        return Result.success(json);
    }

    @GetMapping("logout")
    public Result logout() {
        return Result.success();
    }

    @GetMapping("getIndexDate")
    public Result getIndexDate() {
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUserType, 1);
        int user = userService.count(queryWrapper);
        jsonObject.put("user",user);
        int appointment = conAppointmentService.count();
        jsonObject.put("appointment",appointment);
        QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.lambda().eq(User::getUserType, 2);
        int zixunshi = userService.count(queryWrapper1);
        jsonObject.put("zixunshi",zixunshi);
        int article = conArticleService.count();
        jsonObject.put("article",article);
        return Result.success(jsonObject);
    }

    @GetMapping("getIndexManager")
    public Result getIndexManager() {
        User userInfo = ShiroUtils.getUserInfo();
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUserType, 1);
        int user = userService.count(queryWrapper);
        jsonObject.put("user",user);
        QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.lambda().eq(User::getUserType, 2);
        int zixunshi = userService.count(queryWrapper1);
        jsonObject.put("zixunshi",zixunshi);
        int article = conArticleService.count();
        jsonObject.put("article",article);
        int ask = conAskService.count();
        jsonObject.put("ask",ask);
        // 获取用户性别分析
        List<JSONObject> list = new ArrayList<>();
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.lambda().eq(User::getUserType, 1)
                .eq(User::getSex, 0);
        int man = userService.count(userQueryWrapper);
        JSONObject sex1 = new JSONObject();
        sex1.put("value",man);
        sex1.put("name","男");
        list.add(sex1);
        QueryWrapper<User> userQueryWrapper1 = new QueryWrapper<>();
        userQueryWrapper1.lambda().eq(User::getUserType, 1)
                .eq(User::getSex, 1);
        int women = userService.count(userQueryWrapper1);
        JSONObject sex2 = new JSONObject();
        sex2.put("value",women);
        sex2.put("name","女");
        list.add(sex2);
        jsonObject.put("sex",list);
        // 获取预约类型分析
        List<JSONObject> list1 = new ArrayList<>();
        QueryWrapper<ConAppointment> appointmentQueryWrapper = new QueryWrapper<>();
        appointmentQueryWrapper.lambda().eq(ConAppointment::getType, 0);
        int mian = conAppointmentService.count(appointmentQueryWrapper);
        JSONObject type1 = new JSONObject();
        type1.put("value",mian);
        type1.put("name","面对面咨询");
        list1.add(type1);
        QueryWrapper<ConAppointment> appointmentQueryWrapper1 = new QueryWrapper<>();
        appointmentQueryWrapper1.lambda().eq(ConAppointment::getType, 1);
        int xian = conAppointmentService.count(appointmentQueryWrapper1);
        JSONObject type2 = new JSONObject();
        type2.put("value",xian);
        type2.put("name","线上咨询");
        list1.add(type2);
        jsonObject.put("type",list1);
        //获取近7日预约数量
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<Integer> count = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            Date currentDate = calendar.getTime();
            dates.add(sdf.format(currentDate));
            QueryWrapper<ConAppointment> appointmentQueryWrapper2 = new QueryWrapper<>();
            appointmentQueryWrapper2.lambda().eq(ConAppointment::getSlotDate, sdf.format(currentDate));
            if (userInfo.getUserType() == 2) {
                appointmentQueryWrapper2.lambda().eq(ConAppointment::getConsultId, userInfo.getId());
            }
            int count1 = conAppointmentService.count(appointmentQueryWrapper2);
            count.add(count1);
        }
        jsonObject.put("dates",dates);
        jsonObject.put("count",count);
        return Result.success(jsonObject);
    }

}
