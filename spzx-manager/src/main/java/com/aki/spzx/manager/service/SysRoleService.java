package com.aki.spzx.manager.service;


import com.aki.spzx.model.dto.system.SysRoleDto;
import com.aki.spzx.model.entity.system.SysRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


public interface SysRoleService {
    Page<SysRole> queryByPage(SysRoleDto sysRoleDto, Integer current, Integer limit);

    boolean addSysRole(SysRoleDto sysRoleDto);

    Integer updateSysRole(SysRoleDto sysRoleDto);
}
