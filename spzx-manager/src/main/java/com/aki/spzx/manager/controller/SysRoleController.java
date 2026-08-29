package com.aki.spzx.manager.controller;

import com.aki.spzx.manager.service.SysRoleService;
import com.aki.spzx.model.dto.system.SysRoleDto;
import com.aki.spzx.model.entity.system.SysRole;
import com.aki.spzx.model.vo.common.Result;
import com.aki.spzx.model.vo.common.ResultCodeEnum;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/admin/system/sysRole")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    //查询角色列表
    @Operation(summary = "查询角色列表")
    @PostMapping("queryRoleList/{current}/{limit}")
    public Result queryRoleList(@PathVariable("current") Integer current, @PathVariable("limit") Integer limit, @RequestBody SysRoleDto sysRoleDto) {
        Page<SysRole> pageInfo = sysRoleService.queryByPage(sysRoleDto, current, limit);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    //添加角色
    @Operation(summary = "添加角色")
    @PostMapping("addSysRole")
    public Result addSysRole(@RequestBody SysRoleDto sysRoleDto) {
        boolean success = sysRoleService.addSysRole(sysRoleDto);
        if (success) {
            return Result.build(success, ResultCodeEnum.SUCCESS.getCode(), "角色添加成功");
        }
        return Result.build(success, ResultCodeEnum.ERROR.getCode(), "角色添加失败");
    }

    @Operation(summary = "修改角色")
    @PutMapping("updateSysRole")
    public Result updateSysRole(@RequestBody SysRoleDto sysRoleDto) {
        Integer count = sysRoleService.updateSysRole(sysRoleDto);
        if (count > 0) {
            return Result.build(count, ResultCodeEnum.SUCCESS.getCode(), "角色修改成功");
        }
        return Result.build(count, ResultCodeEnum.ERROR.getCode(), "角色修改失败");
    }

}
