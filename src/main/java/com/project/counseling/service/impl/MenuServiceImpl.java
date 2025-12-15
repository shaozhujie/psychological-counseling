package com.project.counseling.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.counseling.domain.Menu;
import com.project.counseling.mapper.MenuMapper;
import com.project.counseling.service.MenuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: 菜单service实现类
 * @date 2023/8/30 9:24
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    /**
    * 根据用户获取菜单权限
    */
    @Override
    public List<Menu> getMenuByUser(String id) {
        return baseMapper.getMenuByUser(id);
    }
}
