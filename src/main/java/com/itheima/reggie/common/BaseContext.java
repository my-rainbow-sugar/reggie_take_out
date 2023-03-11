package com.itheima.reggie.common;

/**
 * 基于ThreadLocal封装工具类，用于保存和获取当前登录用户id
 */
public class BaseContext {
    private static ThreadLocal<Long> local = new ThreadLocal<>();

    /**
     * 设置值
     *
     * @param id
     */
    public static void setCurrentId(Long id) {
        local.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId() {
        return local.get();
    }
}
