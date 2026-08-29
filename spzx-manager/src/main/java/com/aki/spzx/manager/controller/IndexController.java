package com.aki.spzx.manager.controller;

import com.aki.spzx.manager.service.SysUserService;
import com.aki.spzx.manager.service.ValidateCodeService;
import com.aki.spzx.model.dto.system.LoginDto;
import com.aki.spzx.model.vo.common.Result;
import com.aki.spzx.model.vo.common.ResultCodeEnum;
import com.aki.spzx.model.vo.system.LoginVo;
import com.aki.spzx.model.vo.system.ValidateCodeVo;
import com.aki.spzx.utils.AuthContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Tag(name = "用户接口")
@RestController
@RequestMapping(value = "/admin/system/index")
public class IndexController {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ValidateCodeService validateCodeService;

    //生成验证码
    @Operation(summary = "生成验证码")
    @GetMapping("generateValidateCode")
    public Result<ValidateCodeVo> generateValidateCode() {
        ValidateCodeVo validateCodeVo = validateCodeService.generateValidateCode();
        return Result.build(validateCodeVo, ResultCodeEnum.SUCCESS);
    }

    @Operation(summary = "登录")
    @PostMapping("login")
    public Result<ValidateCodeVo> login(@RequestBody LoginDto loginDto) {
        LoginVo loginVo = sysUserService.login(loginDto);
        return Result.build(loginVo, ResultCodeEnum.SUCCESS);
    }

//    @Operation(summary = "获取用户信息")
//    @GetMapping(value = "/getUserInfo")
//    public Result<ValidateCodeVo> getUserInfo(@RequestHeader(name = "token") String token) {
//        SysUser user = sysUserService.getUserInfo(token);
//        return Result.build(user, ResultCodeEnum.SUCCESS);
//    }

    @Operation(summary = "获取用户信息")
    @GetMapping(value = "/getUserInfo")
    public Result<ValidateCodeVo> getUserInfo() {
        return Result.build(AuthContextUtil.get(), ResultCodeEnum.SUCCESS);
    }

    @Operation(summary = "登出")
    @GetMapping(value = "/logout")
    public Result<ValidateCodeVo> logout(@RequestHeader(name = "token") String token) {
        sysUserService.logout(token);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }
}
