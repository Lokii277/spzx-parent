package com.aki.spzx.manager.service;

import com.aki.spzx.model.dto.system.LoginDto;
import com.aki.spzx.model.dto.system.SysUserDto;
import com.aki.spzx.model.entity.system.SysUser;
import com.aki.spzx.model.vo.system.LoginVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface SysUserService {
    LoginVo login(LoginDto loginDto);

    SysUser getUserInfo(String token);

    SysUser logout(String token);

    Page<SysUser> queryByPage(SysUserDto sysUserDto, Integer current, Integer limit);

    void addSysuser(SysUser sysUser);

    void updateSysuser(Long id, SysUser sysUser);

    void deleteSysuser(Long id);
}
