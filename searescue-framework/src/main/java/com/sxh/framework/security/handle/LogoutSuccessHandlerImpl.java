package com.sxh.framework.security.handle;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sxh.common.core.domain.Response;
import com.sxh.common.core.domain.ResponseEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import com.alibaba.fastjson.JSON;
import com.sxh.common.constant.Constants;
import com.sxh.common.core.domain.model.LoginUser;
import com.sxh.common.utils.ServletUtils;
import com.sxh.common.utils.StringUtils;
import com.sxh.framework.manager.AsyncManager;
import com.sxh.framework.manager.factory.AsyncFactory;
import com.sxh.framework.web.service.TokenService;

/**
 * 自定义退出处理类 返回成功
 *
 * @author sxh
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler
{
    @Autowired
    private TokenService tokenService;

    /**
     * 退出处理
     *
     * @return
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser))
        {
            String userName = loginUser.getUsername();
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
            // 记录用户退出日志
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(userName, Constants.LOGOUT, ResponseEnum.LOGOUT_SUCCESS.getMessage()));
        }
        ServletUtils.renderString(response, JSON.toJSONString(Response.success(ResponseEnum.LOGOUT_SUCCESS)));
    }
}
