package com.aki.spzx.utils;


import com.aki.spzx.model.entity.system.SysUser;

public class AuthContextUtil {
    private static final ThreadLocal<SysUser> threadLocal = new ThreadLocal<>();

    public static void set(SysUser user){
        threadLocal.set(user);
    }

    public static SysUser get(){
        return threadLocal.get();
    }

    public static void remove(){
        threadLocal.remove();
    }

}
