package com.aki.spzx.manager.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aki.spzx.common.config.exception.AkiException;
import com.aki.spzx.manager.aspect.BusinessLog;
import com.aki.spzx.manager.mapper.SysUserMapper;
import com.aki.spzx.manager.service.SysUserService;
import com.aki.spzx.model.dto.system.LoginDto;
import com.aki.spzx.model.dto.system.SysUserDto;

import com.aki.spzx.model.entity.system.SysUser;
import com.aki.spzx.model.vo.common.ResultCodeEnum;
import com.aki.spzx.model.vo.system.LoginVo;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String LOGIN_TOKEN_KEY_PREFIX = "user_login:";

    public static final String CAPTCHA_KEY_PREFIX = "user_validate:";

    //todo 判断用户是否存在的代码重复太多，需要抽取成一个公共方法
    @Override
    public LoginVo login(LoginDto loginDto) {
        //获取输入的验证码和存储到redis的key
        String captcha = loginDto.getCaptcha();
        String key = loginDto.getCodeKey();
        //根据获取到redis的key查询redis里面存储验证码
        String redisCode = redisTemplate.opsForValue().get(CAPTCHA_KEY_PREFIX + key);
        //比较输入的验证码和redis存储验证码是否一致   验证码比较需要忽略大小写
        if (StrUtil.isEmpty(redisCode) || !StrUtil.equalsIgnoreCase(captcha, redisCode)) {
            //校验失败 抛出异常
            throw new AkiException(ResultCodeEnum.VALIDATE_CODE_ERROR);
        }
        //通过验证码校验，删除redis中存储的数据
        redisTemplate.delete(CAPTCHA_KEY_PREFIX + key);
        //获取用户名
        String userName = loginDto.getUserName();
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName, userName);
        SysUser sysUser = sysUserMapper.selectOne(wrapper);
        if (sysUser == null) {
            throw new AkiException(ResultCodeEnum.USER_NOT_EXIST);
        }
        //如果用户名存在则比较密码
        String dbPassword = sysUser.getPassword();
        //比对输入两个结果，如果密码一致则登录成功
//        if (!passwordEncoder.matches(loginDto.getPassword(), dbPassword)) {
//            throw new AkiException(ResultCodeEnum.LOGIN_ERROR);
//        }
        //登录成功，生成用户唯一标识Token
        String token = UUID.randomUUID().toString().replace("-", "");

        //登录成功用户信息存Redis
        redisTemplate.opsForValue().set(LOGIN_TOKEN_KEY_PREFIX + token, JSON.toJSONString(sysUser), 7, TimeUnit.DAYS);
        //返回loginVo对象
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        return loginVo;
    }

    @Override
    public SysUser getUserInfo(String token) {
        //根据token查询redis获取用户信息
        String userJson = redisTemplate.opsForValue().get(LOGIN_TOKEN_KEY_PREFIX + token);
        //把用户信息返回
        return JSON.parseObject(userJson, SysUser.class);
    }

    @Override
    public SysUser logout(String token) {
        redisTemplate.delete(LOGIN_TOKEN_KEY_PREFIX + token);
        return null;
    }

    // 查询用户列表
    @Override
    public Page<SysUser> queryByPage(SysUserDto sysUserDto, Integer current, Integer limit) {
        Page<SysUser> page = new Page<>(current, limit);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        // 关键词查询
        if (StringUtils.hasText(sysUserDto.getKeyword())) {
            wrapper.and(w -> w
                    .like(SysUser::getUserName, sysUserDto.getKeyword())
                    .or()
                    .like(SysUser::getName, sysUserDto.getKeyword())
                    .or()
                    .like(SysUser::getPhone, sysUserDto.getKeyword())
            );
        }
        // 拼接时间查询
        if (StringUtils.hasText(sysUserDto.getCreateTimeBegin())) {
            wrapper.ge(SysUser::getCreateTime, sysUserDto.getCreateTimeBegin());
        }

        if (StringUtils.hasText(sysUserDto.getCreateTimeEnd())) {
            wrapper.le(SysUser::getCreateTime, sysUserDto.getCreateTimeEnd());
        }
        return sysUserMapper.selectPage(page, wrapper);
    }
    @BusinessLog(
            module = "用户管理",
            operation = "新增用户",
            businessType = "INSERT",
            businessId = "#sysUser.id"
    )
    @Override
    public void addSysuser(SysUser sysUser) {
        // 判断用户名是否重复
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName, sysUser.getUserName());
        SysUser user = sysUserMapper.selectOne(wrapper);
        if (user != null) {
            throw new AkiException(ResultCodeEnum.USER_NAME_IS_EXISTS);
        }
        // 密码加密
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        sysUserMapper.insert(sysUser);
    }
    @BusinessLog(
            module = "用户管理",
            operation = "修改用户",
            businessType = "UPDATE"
    )
    @Override
    public void updateSysuser(Long id, SysUser sysUser) {
        // 先根据id判断用户是否存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getId, id);
        SysUser user = sysUserMapper.selectOne(wrapper);
        if (user == null) {
            throw new AkiException(ResultCodeEnum.USER_NOT_EXIST);
        }
        // todo 操作权限认证
        // 用户名不能为空 手机号不能为空
        if (sysUser.getUserName() == null || sysUser.getPhone() == null) {
            throw new AkiException(ResultCodeEnum.USER_NAME_OR_PHONE_EMPTY);
        }
        // 用户名唯一性约束校验
        LambdaQueryWrapper<SysUser> wrapper1 = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName, sysUser.getUserName());
        if (sysUserMapper.selectOne(wrapper1) != null){
            throw new AkiException(ResultCodeEnum.USER_NAME_IS_EXISTS);
        }
        sysUserMapper.updateById(sysUser);
        // todo 记录修改日志

    }

    @Override
    public void deleteSysuser(Long id) {
        // 先判断用户是否存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getId, id);
        if (sysUserMapper.selectOne(wrapper) == null) {
            throw new AkiException(ResultCodeEnum.USER_NOT_EXIST);
        }
        sysUserMapper.deleteById(id);
        // todo 删除用户相关缓存
    }
}
