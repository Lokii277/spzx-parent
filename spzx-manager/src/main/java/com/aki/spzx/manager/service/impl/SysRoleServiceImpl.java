package com.aki.spzx.manager.service.impl;

import com.aki.spzx.common.config.exception.AkiException;
import com.aki.spzx.manager.mapper.SysRoleMapper;
import com.aki.spzx.manager.mapper.SysUserMapper;
import com.aki.spzx.manager.service.SysRoleService;
import com.aki.spzx.model.dto.system.SysRoleDto;
import com.aki.spzx.model.entity.system.SysRole;

import com.aki.spzx.model.entity.system.SysUser;
import com.aki.spzx.model.vo.common.ResultCodeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public Page<SysRole> queryByPage(SysRoleDto sysRoleDto, Integer current, Integer limit) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(sysRoleDto.getRoleName()), SysRole::getRoleName, sysRoleDto.getRoleName())
                .orderByDesc(SysRole::getId);
        return sysRoleMapper.selectPage(new Page<>(current, limit), wrapper);
    }

    @Override
    public boolean addSysRole(SysRoleDto sysRoleDto) {
        SysRole sysRole = new SysRole();
        sysRole.setRoleName(sysRoleDto.getRoleName());
        sysRole.setRoleCode(sysRoleDto.getRoleCode());
        return save(sysRole);
    }

    @Override
    public Integer updateSysRole(SysRoleDto sysRoleDto) {

        Long id = sysRoleDto.getId();

        //先查询角色，再组装更新条件
        SysRole existingRole = sysRoleMapper.selectById(id);
        if (existingRole == null) {
            // 角色不存在，返回 0 或抛出异常（推荐抛异常）
            return 0; //
        }
        LambdaUpdateWrapper<SysRole> updateWrapper = new LambdaUpdateWrapper<>();
        //如果查询结果正确 再更新
        SysRole sysRole = new SysRole();
        sysRole.setId(sysRoleDto.getId());
        sysRole.setRoleName(sysRoleDto.getRoleName());
        sysRole.setRoleCode(sysRoleDto.getRoleCode());

        updateWrapper.eq(SysRole::getId, sysRole.getId())
                .set(SysRole::getRoleName, sysRole.getRoleName())
                .set(SysRole::getRoleCode, sysRole.getRoleCode());
        return sysRoleMapper.update(null, updateWrapper);
    }

    @Override
    public void deleteSysRoleById(Long id) {
        // 先查询是否有角色
        System.out.print(id);
        SysRole sysRole = sysRoleMapper.selectById(id);
        if (sysRole == null) {
            throw new AkiException(ResultCodeEnum.DATA_NOT_EXIST);
        }
        //todo 增加业务判断 如果角色绑定用户 则不允许删除

        // 如果存在角色且角色未绑定用户，则调用删除方法
        sysRoleMapper.deleteById(id);
    }
}
