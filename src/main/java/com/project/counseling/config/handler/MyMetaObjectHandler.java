package com.project.counseling.config.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.project.counseling.config.utils.ShiroUtils;
import com.project.counseling.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: 配置保存时自动插入创建时间和创建账号
 * @date 2023/10/9 16:11
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        User user = ShiroUtils.getUserInfo();
        metaObject.setValue("createTime", new Date());
        metaObject.setValue("updateTime", new Date());
        if (user != null) {
            metaObject.setValue("createBy", user.getUserName());
            metaObject.setValue("updateBy", user.getUserName());
        }
    }


    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]...");
        User user = ShiroUtils.getUserInfo();
        metaObject.setValue("updateTime", new Date());
        if (user != null) {
            metaObject.setValue("updateBy", user.getUserName());
        }
    }

}
