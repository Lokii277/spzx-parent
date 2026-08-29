package com.aki.spzx.manager.service;

import com.aki.spzx.model.dto.system.LoginDto;
import com.aki.spzx.model.entity.system.SysUser;
import com.aki.spzx.model.vo.system.LoginVo;

public interface SysUserService {
    LoginVo login(LoginDto loginDto);

    SysUser getUserInfo(String token);

    SysUser logout(String token);
}
