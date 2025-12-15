package com.project.counseling.controller.user;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.common.utils.PasswordUtils;
import com.project.counseling.config.utils.RedisUtils;
import com.project.counseling.config.utils.ShiroUtils;
import com.project.counseling.domain.*;
import com.project.counseling.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @version 1.0
 * @description: 用户controller
 * @date 2024/2/26 21:00
 */
@Controller
@ResponseBody
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private ConSetService conSetService;
    @Autowired
    private ConTypeService conTypeService;
    @Autowired
    private ConAppointmentService conAppointmentService;
    @Autowired
    private ConAiService conAiService;
    @Autowired
    private ConAskService conAskService;
    @Autowired
    private ConTalkService conTalkService;
    @Autowired
    private ConAskCommentService conAskCommentService;
    @Autowired
    private ConArticleCommentService conArticleCommentService;
    @Autowired
    private ConFavorService conFavorService;
    @Autowired
    private ConPrescriptionService conPrescriptionService;
    @Autowired
    private ConPrescriptionItemService conPrescriptionItemService;
    @Autowired
    private ConResultService conResultService;
    @Autowired
    private ConExamService conExamService;
    @Autowired
    private ConProblemService conProblemService;
    @Autowired
    private ConAnalysisService conAnalysisService;
    @Autowired
    private ConArchivesService conArchivesService;
    @Autowired
    private ConArticleService conArticleService;
    @Autowired
    private ConMakeService conMakeService;
    @Autowired
    private ConMedicineService conMedicineService;

    /** 分页查询用户 */
    @PostMapping("getUserPage")
    public Result getUserPage(@RequestBody User user) {
        Page<User> page = userService.getUserPage(user);
        return Result.success(page);
    }

    @PostMapping("getUserPageByType")
    public Result getUserPageByType(@RequestBody User user) {
        user.setUserType(2);
        Page<User> page = userService.getUserPageByType(user);
        for (User user1 : page.getRecords()) {
            QueryWrapper<ConSet> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ConSet::getUserId,user1.getId()).last("limit 1");
            ConSet conSet = conSetService.getOne(queryWrapper);
            user1.setConSet(conSet);
            if (StringUtils.isNotBlank(user1.getTypes())) {
                String[] strings = user1.getTypes().split(",");
                List<String> list = new ArrayList<>();
                for (String string : strings) {
                    ConType byId = conTypeService.getById(string);
                    list.add(byId.getName());
                }
                user1.setTypeWz(list);
            }
        }
        return Result.success(page);
    }

    @GetMapping("getUserByType")
    public Result getUserByType(@RequestParam("type")Integer type) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUserType,type);
        List<User> userList = userService.list(queryWrapper);
        return Result.success(userList);
    }

    /** 根据id查询用户 */
    @GetMapping("getUserById")
    public Result getUserById(@RequestParam("id") String id) {
        User user = userService.getById(id);
        QueryWrapper<ConSet> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ConSet::getUserId,user.getId()).last("limit 1");
        ConSet conSet = conSetService.getOne(wrapper);
        user.setConSet(conSet);
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserRole::getUserId,user.getId());
        List<UserRole> list = userRoleService.list(queryWrapper);
        List<String> roles = new ArrayList<>();
        for (UserRole apeUserRole : list) {
            roles.add(apeUserRole.getRoleId());
        }
        user.setRoleIds(roles);
        return Result.success(user);
    }

    /** 新增用户 */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("saveUser")
    public Result saveUser(@RequestBody User user) {
        //先校验登陆账号是否重复
        boolean account = checkAccount(user);
        if (!account) {
            return Result.fail("登陆账号已存在不可重复！");
        }
        String uuid = IdWorker.get32UUID();
        //密码加盐加密
        String encrypt = PasswordUtils.encrypt(user.getPassword());
        String[] split = encrypt.split("\\$");
        user.setId(uuid);
        user.setPassword(split[0]);
        user.setSalt(split[1]);
        if (StringUtils.isBlank(user.getAvatar())) {
            user.setAvatar("/img/avatar.jpeg");
        }
        user.setPwdUpdateDate(new Date());
        //保存用户
        boolean save = userService.save(user);
        if (user.getUserType() == 2) {
            ConSet conSet = new ConSet();
            conSet.setUserId(user.getId());
            conSetService.save(conSet);
        }
        //再保存用户角色关系
        List<String> roleIds = user.getRoleIds();
        List<UserRole> apeUserRoles = new ArrayList<>();
        if (roleIds != null && roleIds.size() > 0) {
            for (String roleId : roleIds) {
                UserRole apeUserRole = new UserRole();
                apeUserRole.setUserId(uuid);
                apeUserRole.setRoleId(roleId);
                apeUserRoles.add(apeUserRole);
            }
        }
        userRoleService.saveBatch(apeUserRoles);
        return Result.success();
    }

    /** 编辑用户 */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("editUser")
    public Result editUser(@RequestBody User user) {
        User user1 = userService.getById(user.getId());
        if (!user1.getLoginAccount().equals(user.getLoginAccount())) {
            //先校验登陆账号是否重复
            boolean account = checkAccount(user);
            if (!account) {
                return Result.fail("登陆账号已存在不可重复！");
            }
        }
        //更新用户
        boolean edit = userService.updateById(user);
        //先删除用户角色关系
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserRole::getUserId,user.getId());
        userRoleService.remove(queryWrapper);
        //再次保存最新的关系
        List<String> roleIds = user.getRoleIds();
        List<UserRole> apeUserRoles = new ArrayList<>();
        if (roleIds != null && roleIds.size() > 0) {
            for (String roleId : roleIds) {
                UserRole apeUserRole = new UserRole();
                apeUserRole.setUserId(user.getId());
                apeUserRole.setRoleId(roleId);
                apeUserRoles.add(apeUserRole);
            }
        }
        userRoleService.saveBatch(apeUserRoles);
        return Result.success();
    }

    /** 校验用户 */
    public boolean checkAccount(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getLoginAccount,user.getLoginAccount());
        int count = userService.count(queryWrapper);
        return count <= 0;
    }

    /** 删除用户 */
    @Transactional(rollbackFor = Exception.class)
    @GetMapping("removeUser")
    public Result removeUser(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                User user = userService.getById(id);
                boolean remove = userService.removeById(id);
                //删除用户角色关系
                QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(UserRole::getUserId,user.getId());
                userRoleService.remove(queryWrapper);
                QueryWrapper<ConSet> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.lambda().eq(ConSet::getUserId,user.getId());
                conSetService.remove(queryWrapper1);
                if (user.getUserType() == 1) {
                    QueryWrapper<ConAppointment> wrapper = new QueryWrapper<>();
                    wrapper.lambda().eq(ConAppointment::getUserId,user.getId());
                    List<ConAppointment> conAppointments = conAppointmentService.list(wrapper);
                    for (ConAppointment conAppointment : conAppointments) {
                        conAppointmentService.removeById(conAppointment.getId());
                        QueryWrapper<ConTalk> wrapper1 = new QueryWrapper<>();
                        wrapper1.lambda().eq(ConTalk::getAppointmentId,conAppointment.getId());
                        conTalkService.remove(wrapper1);
                    }
                }
                if (user.getUserType() == 2) {
                    QueryWrapper<ConAppointment> wrapper = new QueryWrapper<>();
                    wrapper.lambda().eq(ConAppointment::getConsultId,user.getId());
                    List<ConAppointment> conAppointments = conAppointmentService.list(wrapper);
                    for (ConAppointment conAppointment : conAppointments) {
                        conAppointmentService.removeById(conAppointment.getId());
                        QueryWrapper<ConTalk> wrapper1 = new QueryWrapper<>();
                        wrapper1.lambda().eq(ConTalk::getAppointmentId,conAppointment.getId());
                        conTalkService.remove(wrapper1);
                    }
                    QueryWrapper<ConExam> queryWrapper2 = new QueryWrapper<>();
                    queryWrapper2.lambda().eq(ConExam::getUserId,user.getId());
                    List<ConExam> examList = conExamService.list(queryWrapper2);
                    for (ConExam conExam : examList) {
                        conExamService.removeById(conExam.getId());
                        QueryWrapper<ConProblem> wrapper1 = new QueryWrapper<>();
                        wrapper1.lambda().eq(ConProblem::getExamId,conExam.getId());
                        conProblemService.remove(wrapper1);
                        QueryWrapper<ConResult> queryWrapper3 = new QueryWrapper<>();
                        queryWrapper3.lambda().eq(ConResult::getExamId,conExam.getId());
                        conResultService.remove(queryWrapper3);
                        QueryWrapper<ConAnalysis> queryWrapper4 = new QueryWrapper<>();
                        queryWrapper4.lambda().eq(ConAnalysis::getExamId,conExam.getId());
                        conAnalysisService.remove(queryWrapper4);
                    }
                    QueryWrapper<ConArchives> queryWrapper3 = new QueryWrapper<>();
                    queryWrapper3.lambda().eq(ConArchives::getUserId,user.getId());
                    conArchivesService.remove(queryWrapper3);
                    QueryWrapper<ConArticle> queryWrapper4 = new QueryWrapper<>();
                    queryWrapper4.lambda().eq(ConArticle::getUserId,user.getId());
                    List<ConArticle> articleList = conArticleService.list(queryWrapper4);
                    for (ConArticle conArticle : articleList) {
                        conArticleService.removeById(conArticle.getId());
                        QueryWrapper<ConArticleComment> queryWrapper5 = new QueryWrapper<>();
                        queryWrapper5.lambda().eq(ConArticleComment::getArticleId,conArticle.getId());
                        conArticleCommentService.remove(queryWrapper5);
                    }
                    QueryWrapper<ConMake> queryWrapper5 = new QueryWrapper<>();
                    queryWrapper5.lambda().eq(ConMake::getUserId,user.getId());
                    conMakeService.remove(queryWrapper5);
                    QueryWrapper<ConMedicine> queryWrapper6 = new QueryWrapper<>();
                    queryWrapper6.lambda().eq(ConMedicine::getUserId,user.getId());
                    conMedicineService.remove(queryWrapper6);
                    QueryWrapper<ConPrescription> queryWrapper7 = new QueryWrapper<>();
                    queryWrapper7.lambda().eq(ConPrescription::getConsultId,user.getId());
                    List<ConPrescription> prescriptionList = conPrescriptionService.list(queryWrapper7);
                    for (ConPrescription conPrescription : prescriptionList) {
                        conPrescriptionService.removeById(conPrescription.getId());
                        QueryWrapper<ConPrescriptionItem> queryWrapper8 = new QueryWrapper<>();
                        queryWrapper8.lambda().eq(ConPrescriptionItem::getPrescriptionId,conPrescription.getId());
                        conPrescriptionItemService.remove(queryWrapper8);
                    }
                    QueryWrapper<ConFavor> queryWrapper9 = new QueryWrapper<>();
                    queryWrapper9.lambda().eq(ConFavor::getConsultingId,user.getId());
                    conFavorService.remove(queryWrapper9);
                }
                QueryWrapper<ConAi> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.lambda().eq(ConAi::getUserId,user.getId());
                conAiService.remove(queryWrapper2);
                QueryWrapper<ConArticleComment> queryWrapper3 = new QueryWrapper<>();
                queryWrapper3.lambda().eq(ConArticleComment::getUserId,user.getId());
                conArticleCommentService.remove(queryWrapper3);
                QueryWrapper<ConAsk> queryWrapper4 = new QueryWrapper<>();
                queryWrapper4.lambda().eq(ConAsk::getUserId,user.getId());
                conAskService.remove(queryWrapper4);
                QueryWrapper<ConAskComment> queryWrapper5 = new QueryWrapper<>();
                queryWrapper5.lambda().eq(ConAskComment::getUserId,user.getId());
                conAskCommentService.remove(queryWrapper5);
                QueryWrapper<ConFavor> queryWrapper6 = new QueryWrapper<>();
                queryWrapper6.lambda().eq(ConFavor::getUserId,user.getId());
                conFavorService.remove(queryWrapper6);
                QueryWrapper<ConPrescription> queryWrapper7 = new QueryWrapper<>();
                queryWrapper7.lambda().eq(ConPrescription::getUserId,user.getId());
                List<ConPrescription> prescriptionList = conPrescriptionService.list(queryWrapper7);
                for (ConPrescription conPrescription : prescriptionList) {
                    conPrescriptionService.removeById(conPrescription.getId());
                    QueryWrapper<ConPrescriptionItem> wrapper = new QueryWrapper<>();
                    wrapper.lambda().eq(ConPrescriptionItem::getPrescriptionId,conPrescription.getId());
                    conPrescriptionItemService.remove(wrapper);
                }
                QueryWrapper<ConResult> queryWrapper8 = new QueryWrapper<>();
                queryWrapper8.lambda().eq(ConResult::getUserId,user.getId());
                conResultService.remove(queryWrapper8);
            }
            return Result.success();
        } else {
            return Result.fail("角色id不能为空！");
        }
    }

    /** 重置密码 */
    @PostMapping("resetPassword")
    public Result resetPassword(@RequestBody JSONObject json) {
        String id = json.getString("id");
        String newPassword = json.getString("newPassword");
        String encrypt = PasswordUtils.encrypt(newPassword);
        String[] split = encrypt.split("\\$");
        User user = userService.getById(id);
        boolean decrypt = PasswordUtils.decrypt(newPassword, user.getPassword() + "$" + user.getSalt());
        if (decrypt) {
            return Result.fail("新密码不可和旧密码相同！");
        }
        user.setPassword(split[0]);
        user.setSalt(split[1]);
        user.setPwdUpdateDate(new Date());
        boolean update = userService.updateById(user);
        if (update) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 获取登陆用户信息 */
    @GetMapping("getUserInfo")
    public Result getUserInfo() {
        User user = ShiroUtils.getUserInfo();
        return Result.success(user);
    }

    /** 修改个人信息 */
    @PostMapping("setUserInfo")
    public Result setUserInfo(@RequestBody User user) {
        String id = ShiroUtils.getUserInfo().getId();
        user.setId(id);
        userService.updateById(user);
        return Result.success();
    }

    /** 修改个人头像 */
    @PostMapping("setUserAvatar/{id}")
    public Result setUserAvatar(@PathVariable("id") String id, @RequestParam("file") MultipartFile avatar) {
        if(StringUtils.isBlank(id)){
            return Result.fail("用户id为空!");
        }
        User apeUser = userService.getById(id);
        if(avatar.isEmpty()){
            return Result.fail("上传的头像不能为空!");
        }
        String coverType = avatar.getOriginalFilename().substring(avatar.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
        if ("jpeg".equals(coverType)  || "gif".equals(coverType) || "png".equals(coverType) || "bmp".equals(coverType)  || "jpg".equals(coverType)) {
            //文件路径
            String filePath = System.getProperty("user.dir")+System.getProperty("file.separator")+"img";
            //文件名=当前时间到毫秒+原来的文件名
            String fileName = System.currentTimeMillis() + "."+ coverType;
            //如果文件路径不存在，新增该路径
            File file1 = new File(filePath);
            if(!file1.exists()){
                boolean mkdir = file1.mkdir();
            }
            //现在的文件地址
            if (StringUtils.isNotBlank(apeUser.getAvatar())) {
                String s = apeUser.getAvatar().split("/")[2];
                File now = new File(filePath + System.getProperty("file.separator") + s);
                boolean delete = now.delete();
            }
            //实际的文件地址
            File dest = new File(filePath + System.getProperty("file.separator") + fileName);
            //存储到数据库里的相对文件地址
            String storeImgPath = "/img/"+fileName;
            try {
                avatar.transferTo(dest);
                //更新头像
                apeUser.setAvatar(storeImgPath);
                userService.updateById(apeUser);
                return Result.success(storeImgPath);
            } catch (IOException e) {
                return Result.fail("上传失败");
            }
        } else {
            return Result.fail("请选择正确的图片格式");
        }
    }

    @PostMapping("changePassword")
    public Result changePassword(@RequestBody JSONObject json) {
        String id = json.getString("id");
        String password = json.getString("password");
        User user = userService.getById(id);
        boolean decrypt = PasswordUtils.decrypt(password, user.getPassword() + "$" + user.getSalt());
        if (decrypt) {
            String newPassword = json.getString("newPassword");
            String encrypt = PasswordUtils.encrypt(newPassword);
            String[] split = encrypt.split("\\$");
            user.setSalt(split[1]);
            user.setPassword(split[0]);
            user.setPwdUpdateDate(new Date());
            boolean update = userService.updateById(user);
            if (update) {
                return Result.success();
            } else {
                return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
            }
        } else {
            return Result.fail("旧密码不正确");
        }
    }

    @GetMapping("getEmailReg")
    public Result getEmailReg(@RequestParam("email") String email) {
        String str="abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
        Random r=new Random();
        String arr[]=new String [4];
        String reg="";
        for(int i=0;i<4;i++) {
            int n=r.nextInt(62);
            arr[i]=str.substring(n,n+1);
            reg+=arr[i];
        }
        try {
            redisUtils.set(email + "forget",reg.toLowerCase(),60L);
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setPort(465);
            sender.setHost("smtp.qq.com");
            sender.setUsername("1760272627@qq.com");
            sender.setPassword("moguqdleqpazecag");
            sender.setDefaultEncoding("utf-8");
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true"); // 使用TLS
            properties.put("mail.smtp.ssl.enable", "true"); // 启用SSL
            sender.setJavaMailProperties(properties);
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setFrom(sender.getUsername());
            helper.setTo(email);
            helper.setSubject("修改密码验证");
            helper.setText("您的邮箱验证码为："+reg,true);
            sender.send(msg);
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail("邮件发送失败");
        }
        return Result.success();
    }

    @PostMapping("forgetPassword")
    public Result forgetPassword(@RequestBody JSONObject jsonObject) {
        String loginAccount = jsonObject.getString("loginAccount");
        String email = jsonObject.getString("email");
        String password = jsonObject.getString("password");
        String code = jsonObject.getString("code").toLowerCase();
        String s = redisUtils.get(email + "forget");
        if (!code.equals(s)) {
            return Result.fail("验证码错误");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getLoginAccount,loginAccount).last("limit 1");
        User user = userService.getOne(queryWrapper);
        //密码加盐加密
        String encrypt = PasswordUtils.encrypt(password);
        String[] split = encrypt.split("\\$");
        user.setPassword(split[0]);
        user.setSalt(split[1]);
        boolean update = userService.updateById(user);
        if (update) {
            return Result.success();
        } else {
            return Result.fail("密码修改失败");
        }
    }

    @GetMapping("getIndexUser")
    public Result getIndexUser() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUserType,2).
                orderByDesc(User::getCreateTime).
                last("limit 4");
        List<User> list = userService.list(queryWrapper);
        for (User user : list) {
            QueryWrapper<ConSet> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.lambda().eq(ConSet::getUserId,user.getId()).last("limit 1");
            ConSet set = conSetService.getOne(queryWrapper1);
            user.setConSet(set);
        }
        return Result.success(list);
    }

}
