package com.project.counseling.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.counseling.domain.Menu;

import java.util.List;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: 菜单service
 * @date 2023/8/30 9:23
 */
public interface MenuService extends IService<Menu> {

    /**
    * @description: 根据用户获取菜单权限
    * @param: id
    * @return:
    * @author shaozhujie
    * @date: 2023/9/13 9:39
    */
    List<Menu> getMenuByUser(String id);

}
