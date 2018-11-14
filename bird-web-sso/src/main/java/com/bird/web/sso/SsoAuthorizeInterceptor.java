package com.bird.web.sso;

import com.bird.core.session.BirdSession;
import com.bird.core.session.SessionContext;
import com.bird.web.sso.permission.IUserPermissionChecker;
import com.bird.web.sso.ticket.TicketHandler;
import com.bird.web.sso.ticket.TicketInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author liuxx
 * @date 2017/5/18
 *
 * 权限验证拦截器
 */
public class SsoAuthorizeInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private TicketHandler ticketHandler;

    @Autowired
    private IUserPermissionChecker permissionChecker;

    /**
     * 拦截器处理方法
     *
     * @param request  request
     * @param response response
     * @param handler  跨域第一次OPTIONS请求时handler为AbstractHandlerMapping.PreFlightHandler，不拦截
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) return true;
        TicketInfo ticketInfo = ticketHandler.getTicket(request);
        this.initializeRequest(request, ticketInfo);

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        SsoAuthorize methodAuthorize = handlerMethod.getMethodAnnotation(SsoAuthorize.class);
        if (methodAuthorize != null && methodAuthorize.anonymous()) return true;

        Class clazz = handlerMethod.getBeanType();
        SsoAuthorize typeAuthorize = (SsoAuthorize) clazz.getDeclaredAnnotation(SsoAuthorize.class);
        if (typeAuthorize != null && typeAuthorize.anonymous()) return true;

        if (methodAuthorize != null || typeAuthorize != null) {
            if (ticketInfo == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"用户信息已失效");
            }
            if (!checkAllowHosts(request, ticketInfo)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"用户没有当前站点的登录权限");
            }
            if (methodAuthorize == null || methodAuthorize.permissions().length == 0) return true;

            List<String> permissions = new ArrayList<>();
            if (typeAuthorize == null || typeAuthorize.permissions().length == 0) {
                permissions = Arrays.asList(methodAuthorize.permissions());
            } else {
                for (String typePermission : typeAuthorize.permissions()) {
                    for (String methodPermission : methodAuthorize.permissions()) {
                        permissions.add(typePermission + methodPermission);
                    }
                }
            }

            boolean isCheckAll = methodAuthorize.isCheckAll();
            if (!permissionChecker.hasPermissions(ticketInfo.getUserId(), permissions.toArray(new String[permissions.size()]), isCheckAll)) {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,"用户没有当前操作的权限");
            }
        }
        return true;
    }

    /**
     * 检查用户是否允许登录当前站点
     *
     * @param request    请求
     * @param ticketInfo 票据信息
     * @return
     */
    private boolean checkAllowHosts(HttpServletRequest request, TicketInfo ticketInfo) {
        //TODO：取消注释
//        String host = request.getHeader("host");
//        List<String> allowHosts = (List<String>) ticketInfo.getClaim(IUserClientStore.CLAIM_KEY);
//        if (allowHosts == null) return false;
//        return allowHosts.contains(host);
        return true;
    }

    private void initializeRequest(HttpServletRequest request, TicketInfo ticketInfo) {
        if (ticketInfo == null) return;

        BirdSession session = new BirdSession();
        session.setUserId(ticketInfo.getUserId());
        session.setTenantId(ticketInfo.getTenantId());
        session.setName(ticketInfo.getName());
        session.setClaims(ticketInfo.getClaims());

        SessionContext.setSession(session);
        request.setAttribute(SsoSessionResolvor.SESSION_ATTRIBUTE_KEY, session);
        request.setAttribute(SsoAuthorizeManager.TICKET_ATTRIBUTE_KEY, ticketInfo);
    }
}
