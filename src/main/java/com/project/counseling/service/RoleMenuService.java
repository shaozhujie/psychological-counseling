package com.project.counseling.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.counseling.domain.RoleMenu;

import java.util.Set;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: 角色菜单关系service
 * @date 2023/8/31 10:57
 */
public interface RoleMenuService extends IService<RoleMenu> {

    /**
     * @description: 根据角色获取权限
     * @param: loginAccount
     * @return:
     * @author shaozhujie
     * @date: 2023/9/7 17:01
     */
    Set<String> getRoleMenusSet(String role);
}
