package com.project.counseling.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.counseling.domain.Role;
import com.project.counseling.mapper.RoleMapper;
import com.project.counseling.service.RoleService;
import org.springframework.stereotype.Service;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: 角色service实现类
 * @date 2023/8/31 10:18
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
}
