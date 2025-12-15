package com.project.counseling.controller.menu;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.counseling.common.enums.ResultCode;
import com.project.counseling.config.utils.ShiroUtils;
import com.project.counseling.domain.Menu;
import com.project.counseling.domain.Result;
import com.project.counseling.domain.RoleMenu;
import com.project.counseling.domain.User;
import com.project.counseling.service.MenuService;
import com.project.counseling.service.RoleMenuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: 菜单controller
 * @date 2023/8/30 9:25
 */
@Controller
@ResponseBody
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private MenuService menuService;
    @Autowired
    private RoleMenuService apeRoleMenuService;

    /** 获取菜单列表 */
    @PostMapping("getMenuList")
    public Result getMenuList(@RequestBody Menu menu) {
        //构造查询条件
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(StringUtils.isNotBlank(menu.getMenuName()),Menu::getMenuName,menu.getMenuName())
                .eq(menu.getStatus() != null,Menu::getStatus,menu.getStatus()).orderByAsc(Menu::getOrderNum);
        //查询
        List<Menu> Menus = menuService.list(queryWrapper);
        //筛选出第一级
        List<Menu> first = Menus.stream().filter(item -> "0".equals(item.getParentId())).collect(Collectors.toList());
        if (first.size() <= 0) {
            return Result.success(Menus);
        } else {
            for (Menu menuItem : first) {
                filterMenu(menuItem,Menus);
            }
            return Result.success(first);
        }
    }

    /** 递归查询下级菜单 */
    public void filterMenu(Menu Menu,List<Menu> Menus) {
        List<Menu> menus = new ArrayList<>();
        for (Menu menu : Menus) {
            if (Menu.getId().equals(menu.getParentId())) {
                menus.add(menu);
                filterMenu(menu,Menus);
            }
        }
        Menu.setChildren(menus);
    }

    @GetMapping("getById")
    public Result getById(@RequestParam("id")String id) {
        Menu Menu = menuService.getById(id);
        return Result.success(Menu);
    }

    /** 保存菜单 */
    @PostMapping("saveMenu")
    public Result saveMenu(@RequestBody Menu menu) {
        if (menu.getMenuType() != 0 ) {
            QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(Menu::getPerms,menu.getPerms());
            int count = menuService.count(queryWrapper);
            if (count > 0) {
                return Result.fail("权限字符已存在！");
            }
        }
        boolean save = menuService.save(menu);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑菜单 */
    @PostMapping("editMenu")
    public Result editMenu(@RequestBody Menu menuEdit) {
        if (StringUtils.isNotBlank(menuEdit.getIdArrary())) {
            menuEdit.setParentId("0");
        }
        Menu menu = menuService.getById(menuEdit.getId());
        if (menu.getMenuType() != 0 ) {
            if (!menu.getPerms().equals(menu.getPerms())) {
                QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(Menu::getPerms,menu.getPerms());
                int count = menuService.count(queryWrapper);
                if (count > 0) {
                    return Result.fail("权限字符已存在！");
                }
            }
        }
        boolean update = menuService.updateById(menu);
        if (update) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除菜单 */
    @GetMapping("removeMenu")
    public Result removeMenu(@RequestParam("id")String id) {
        //先查有没有分配给用户
        QueryWrapper<RoleMenu> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RoleMenu::getMenuId,id);
        int num = apeRoleMenuService.count(wrapper);
        if (num > 0) {
            return Result.fail("菜单已分配给角色，请先解除角色菜单！");
        }
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Menu::getParentId,id);
        int count = menuService.count(queryWrapper);
        if (count > 0) {
            return Result.fail("存在下级菜单,请先删除下级菜单！");
        }
        boolean remove = menuService.removeById(id);
        if (remove) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 根据用户获取菜单权限 */
    @GetMapping("getMenuByUser")
    public Result getMenuByUser() {
        User apeUser = ShiroUtils.getUserInfo();
        List<Menu> menus = menuService.getMenuByUser(apeUser.getId());
        return Result.success(menus);
    }

}
