package com.primihub.application.controller.sys;

import com.primihub.biz.entity.base.BaseResultEntity;
import com.primihub.biz.entity.base.BaseResultEnum;
import com.primihub.biz.entity.sys.enumeration.OAuthSourceEnum;
import com.primihub.biz.entity.sys.param.LoginParam;
import com.primihub.biz.service.sys.SysOauthService;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthGithubRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import me.zhyd.oauth.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequestMapping("oauth")
@RestController
public class OauthController {

    @Autowired
    private SysOauthService sysOauthService;

    @RequestMapping("getAuthList")
    public BaseResultEntity getOauthList(){
        return BaseResultEntity.success(sysOauthService.getOauthList());
    }

    @RequestMapping("authLogin")
    public BaseResultEntity authLogin(LoginParam loginParam){
        if(loginParam.getAuthPublicKey()==null||loginParam.getAuthPublicKey().trim().equals(""))
            return BaseResultEntity.failure(BaseResultEnum.LACK_OF_PARAM,"authPublicKey");
        return sysOauthService.authLogin(loginParam);
    }

    @RequestMapping("/{source}/render")
    public void renderAuth(HttpServletResponse response, @PathVariable("source") String source) throws IOException {
        if (StringUtils.isEmpty(source) && !OAuthSourceEnum.AUTH_MAP.containsKey(source)){
            oauthError(response,"来源");
        }else {
            AuthRequest authRequest = sysOauthService.getAuthRequest(OAuthSourceEnum.AUTH_MAP.get(source));
            if (authRequest == null){
                oauthError(response, OAuthSourceEnum.GITHUB.getSourceName());
            }else {
                response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
            }
        }
    }

    @RequestMapping("/{source}/callback")
    public void gitHubLogin(AuthCallback callback,HttpServletResponse response,@PathVariable("source") String source) throws IOException {
        if (StringUtils.isEmpty(source) && !OAuthSourceEnum.AUTH_MAP.containsKey(source)){
            oauthError(response,"来源");
        }else {
            if (StringUtils.isEmpty(callback.getCode())) {
                oauthError(response,"code");
            }
            if (StringUtils.isEmpty(callback.getState())) {
                oauthError(response,"state");
            }
            BaseResultEntity baseResultEntity = sysOauthService.authDataLogin(callback, OAuthSourceEnum.AUTH_MAP.get(source));
            if (baseResultEntity.getCode().equals(BaseResultEnum.SUCCESS.getReturnCode())){
                response.sendRedirect(baseResultEntity.getResult().toString());
            }else {
                oauthError(response,baseResultEntity.getMsg());
            }
        }
    }

    public void oauthError(HttpServletResponse response,String message) throws IOException {
        response.reset();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().println(BaseResultEntity.failure(BaseResultEnum.AUTH_LOGIN,message+"-配置异常,请尝试联系我们!"));
    }

    public static void main(String[] args) {
        String val = "https://test2.primihub.com/#/auth?authPublicKey=%s&user=%s";
        System.out.println(String.format(val,"111","2222"));
    }
}
