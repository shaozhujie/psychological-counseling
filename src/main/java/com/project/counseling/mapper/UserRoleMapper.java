package com.project.counseling.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.counseling.domain.UserRole;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: 用户角色关系mapper
 * @date 2023/8/31 14:34
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 根据账号获取角色
     */
    Set<String> getUserRolesSet(@Param("loginAccount") String loginAccount);
}
