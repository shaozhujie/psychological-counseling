package com.project.counseling.controller.role;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.counseling.domain.*;
import com.project.counseling.service.RoleMenuService;
import com.project.counseling.service.RoleService;
import com.project.counseling.service.UserRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: TODO
 * @date 2023/8/31 10:21
 */
@Controller
@ResponseBody
@RequestMapping("role")
public class RoleController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleMenuService roleMenuService;
    @Autowired
    private UserRoleService apeUserRoleService;

    /** 分页获取角色 */
    @PostMapping("getRolePage")
    public Result getRolePage(@RequestBody Role apeRole) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(StringUtils.isNotBlank(apeRole.getRoleName()),Role::getRoleName,apeRole.getRoleName())
                .like(StringUtils.isNotBlank(apeRole.getRoleKey()),Role::getRoleKey,apeRole.getRoleKey())
                .eq(apeRole.getStatus() != null,Role::getStatus,apeRole.getStatus());
        Page<Role> page = new Page<>(apeRole.getPageNumber(),apeRole.getPageSize());
        Page<Role> rolePage = roleService.page(page, queryWrapper);
        return Result.success(rolePage);
    }

    /** 获取角色列表 */
    @PostMapping("getRoleList")
    public Result getRoleList(@RequestBody Role apeRole) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(StringUtils.isNotBlank(apeRole.getRoleName()),Role::getRoleName,apeRole.getRoleName())
                .like(StringUtils.isNotBlank(apeRole.getRoleKey()),Role::getRoleKey,apeRole.getRoleKey())
                .eq(apeRole.getStatus() != null,Role::getStatus,apeRole.getStatus());
        List<Role> RoleList = roleService.list(queryWrapper);
        return Result.success(RoleList);
    }

    /** 根据id获取角色 */
    @GetMapping("getRoleById")
    public Result getRoleById(@RequestParam("id")String id) {
        Role Role = roleService.getById(id);
        QueryWrapper<RoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(RoleMenu::getRoleId,Role.getId());
        List<RoleMenu> RoleMenus = roleMenuService.list(queryWrapper);
        List<String> menuIds = new ArrayList<>();
        for (RoleMenu RoleMenu : RoleMenus) {
            menuIds.add(RoleMenu.getMenuId());
        }
        Role.setMenuIds(menuIds);
        return Result.success(Role);
    }

    /** 保存角色 */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("saveRole")
    public Result saveRole(@RequestBody Role apeRole) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Role::getRoleKey,apeRole.getRoleKey());
        int count = roleService.count(queryWrapper);
        if (count > 0) {
            return Result.fail("权限字符已存在！");
        }
        //先保存角色
        String uuid = IdWorker.get32UUID();
        apeRole.setId(uuid);
        roleService.save(apeRole);
        //再保存角色菜单关系
        List<String> menuIds = apeRole.getMenuIds();
        List<RoleMenu> RoleMenuList = new ArrayList<>();
        for (String menuId : menuIds) {
            RoleMenu RoleMenu = new RoleMenu();
            RoleMenu.setRoleId(uuid);
            RoleMenu.setMenuId(menuId);
            RoleMenuList.add(RoleMenu);
        }
        roleMenuService.saveBatch(RoleMenuList);
        return Result.success();
    }

    /** 编辑角色 */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("editRole")
    public Result editRole(@RequestBody Role apeRole) {
        Role role = roleService.getById(apeRole.getId());
        if (!role.getRoleKey().equals(apeRole.getRoleKey())) {
            QueryWrapper<Role> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(Role::getRoleKey,apeRole.getRoleKey());
            int count = roleService.count(wrapper);
            if (count > 0) {
                return Result.fail("权限字符已存在！");
            }
        }
        //更新角色
        roleService.updateById(apeRole);
        //把角色菜单关系删除
        QueryWrapper<RoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(RoleMenu::getRoleId,apeRole.getId());
        roleMenuService.remove(queryWrapper);
        //删除之后再重新保存
        List<String> menuIds = apeRole.getMenuIds();
        List<RoleMenu> RoleMenuList = new ArrayList<>();
        for (String menuId : menuIds) {
            RoleMenu RoleMenu = new RoleMenu();
            RoleMenu.setRoleId(apeRole.getId());
            RoleMenu.setMenuId(menuId);
            RoleMenuList.add(RoleMenu);
        }
        roleMenuService.saveBatch(RoleMenuList);
        return Result.success();
    }

    /** 删除角色 */
    @Transactional(rollbackFor = Exception.class)
    @GetMapping("removeRole")
    public Result removeRole(@RequestParam("ids") String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                //先查有没有分配给用户
                QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(UserRole::getRoleId,id);
                int count = apeUserRoleService.count(wrapper);
                if (count > 0) {
                    return Result.fail("角色已分配给用户，请先解除用户角色！");
                }
                //删除角色和菜单关系
                roleService.removeById(id);
                QueryWrapper<RoleMenu> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(RoleMenu::getRoleId,id);
                roleMenuService.remove(queryWrapper);
            }
            return Result.success();
        } else {
            return Result.fail("角色id不能为空！");
        }
    }

}
