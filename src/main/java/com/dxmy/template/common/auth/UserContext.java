package com.dxmy.template.common.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户上下文
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {

    private static final ThreadLocal<UserContext> THREAD_LOCAL_USER_CONTEXT = new ThreadLocal<>();

    /** 用户相关信息 */
    private Long id;

    /**
     * 获取当前登录用户
     */
    public static UserContext getCurrentUser() {
        return THREAD_LOCAL_USER_CONTEXT.get();
    }

    /**
     * 设置当前登录用户
     */
    public static void setCurrentUser(UserContext userContext) {
        THREAD_LOCAL_USER_CONTEXT.set(userContext);
    }

    /**
     * 清除用户上下文
     */
    public static void clear() {
        THREAD_LOCAL_USER_CONTEXT.remove();
    }

    /**
     * 判断用户是否登录
     */
    public static boolean isLogin() {
        return getCurrentUser() != null;
    }

    /**
     * 获取当前登录用户 ID
     */
    public static Long getCurrentUserId() {
        return isLogin() ? getCurrentUser().getId() : null;
    }

}
