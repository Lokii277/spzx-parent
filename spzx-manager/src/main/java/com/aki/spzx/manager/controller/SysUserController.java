package com.aki.spzx.manager.controller;

import com.aki.spzx.manager.service.SysUserService;
import com.aki.spzx.model.dto.system.SysUserDto;
import com.aki.spzx.model.entity.system.SysUser;
import com.aki.spzx.model.vo.common.Result;
import com.aki.spzx.model.vo.common.ResultCodeEnum;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/admin/system/sysUser")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Operation(summary = "查询用户列表")
    @GetMapping
    public Result sysUser(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer limit,
            SysUserDto sysUserDto) {
        Page<SysUser> page = sysUserService.queryByPage(sysUserDto, current, limit);
        return Result.build(page, ResultCodeEnum.SUCCESS);
    }

    @Operation(summary = "添加用户")
    @PostMapping
    public Result sysUser(SysUser sysUser) {
        sysUserService.addSysuser(sysUser);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }


    @Operation(summary = "修改用户")
    @PutMapping("/{id}")
    public Result sysUser(@PathVariable Long id, @RequestBody SysUser sysUser) {
        sysUserService.updateSysuser(id, sysUser);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result sysUser(@PathVariable Long id) {
        sysUserService.deleteSysuser(id);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }
}
