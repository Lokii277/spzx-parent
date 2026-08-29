package com.aki.spzx.manager.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import com.aki.spzx.manager.service.ValidateCodeService;
import com.aki.spzx.model.vo.system.ValidateCodeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ValidateCodeImpl implements ValidateCodeService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public static final String CAPTCHA_KEY_PREFIX = "user_validate:";

    @Override
    public ValidateCodeVo generateValidateCode() {
        // 1 通过工具生成图片验证码
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(150, 48, 4, 2);
        String code = circleCaptcha.getCode();
        String image = circleCaptcha.getImageBase64();
        // 2 验证码存入Redis 设置过期时间
        String key = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(CAPTCHA_KEY_PREFIX + key, code,5, TimeUnit.MINUTES);
        ValidateCodeVo validateCodeVo = new ValidateCodeVo();
        //返回时给KEY不要给KEY前缀
        validateCodeVo.setCodeKey(key);
        validateCodeVo.setCodeValue("data:image/png;base64," + image);
        return validateCodeVo;
    }
}
