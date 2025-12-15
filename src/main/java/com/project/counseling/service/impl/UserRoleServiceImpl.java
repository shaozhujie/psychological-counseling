package com.project.counseling.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.counseling.domain.UserRole;
import com.project.counseling.mapper.UserRoleMapper;
import com.project.counseling.service.UserRoleService;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: 用户角色关系service实现类
 * @date 2023/8/31 14:37
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    /**
     * 根据账号获取角色
     */
    @Override
    public Set<String> getUserRolesSet(String loginAccount) {
        return baseMapper.getUserRolesSet(loginAccount);
    }

}
