package com.bscc.common.oauth;

import com.bscc.common.utils.MD5Util;
import com.bscc.core.base.BaseProvider;
import com.bscc.core.base.OAuth2Token;
import com.bscc.oauthz.model.OauthClient;
import com.bscc.oauthz.model.OauthToken;
import com.bscc.common.service.OAuthRSService;
import com.bscc.upms.model.UpmsPermission;
import com.bscc.upms.model.UpmsRole;
import com.bscc.upms.model.UpmsUser;
import com.bscc.upms.service.UpmsApiService;
import com.bscc.upms.service.UpmsUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户认证和授权
 * @author Kent
 * @since 2017-03-25 02:16
 */
public class OAuth2Realm extends AuthorizingRealm {

    private static Logger _log = LoggerFactory.getLogger(OAuth2Realm.class);

    @Autowired
    private UpmsApiService upmsApiService;

    private OAuthRSService rsService;

    public OAuth2Realm(){
        super();
        setAuthenticationTokenClass(OAuth2Token.class);
    }

    private void validateToken(String token, OauthToken accessToken) throws OAuth2AuthenticationException {
        if (accessToken == null) {
            _log.debug("Invalid access_token: {}, because it is null", token);
            throw new OAuth2AuthenticationException("Invalid access_token: " + token);
        }
        if (accessToken.tokenExpired()) {
            _log.debug("Invalid access_token: {}, because it is expired", token);
            throw new OAuth2AuthenticationException("Invalid access_token: " + token);
        }
    }

    private void validateOauthClient(String token, OauthToken accessToken, OauthClient oauthClient) throws OAuth2AuthenticationException {
        if (oauthClient == null || oauthClient.getDelFlag().equals("1")) {
            _log.debug("Invalid ClientDetails: {} by client_id: {}, it is null or archived", oauthClient, accessToken.getClientId());
            throw new OAuth2AuthenticationException("Invalid client by token: " + token);
        }
    }

    /**
     * 授权：验证权限时调用
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            throw new OAuth2AuthenticationException("PrincipalCollection method argument cannot be null.");
        }

        String userId = (String) getAvailablePrincipal(principals);

        // 当前用户所有角色
        List<?> upmsRoles = upmsApiService.selectUpmsRoleByUpmsUserId(userId);
        Set<String> roles = new HashSet<String>();
        for (Object o : upmsRoles) {
            UpmsRole upmsRole = (UpmsRole) o;
            if (StringUtils.isNotBlank(upmsRole.getName())) {
                roles.add(upmsRole.getName());
            }
        }

        // 当前用户所有权限
        List<?> upmsPermissions = upmsApiService.selectUpmsPermissionByUpmsUserId(userId);
        Set<String> permissions = new HashSet<String>();
        for (Object o : upmsPermissions) {
            UpmsPermission upmsPermission = (UpmsPermission) o;
            if (StringUtils.isNotBlank(upmsPermission.getPermissionValue())) {
                permissions.add(upmsPermission.getPermissionValue());
            }
        }

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setStringPermissions(permissions);
        simpleAuthorizationInfo.setRoles(roles);
        return simpleAuthorizationInfo;
    }

    /**
     * 认证：登录时调用
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        OAuth2Token upToken = (OAuth2Token)token;
        if(upToken.isOAuth2()){
            return doGetOauth2AuthenticationInfo(upToken);
        }else{
            return doGetUsernamePasswordAuthenticationInfo(upToken);
        }
    }

    public AuthenticationInfo doGetOauth2AuthenticationInfo(OAuth2Token token) throws AuthenticationException{
        OAuth2Token upToken = token;
        final String accessToken = (String) upToken.getCredentials();

        if (org.springframework.util.StringUtils.isEmpty(accessToken)) {
            throw new OAuth2AuthenticationException("Invalid access_token: " + accessToken);
        }
        //Validate access token
        OauthToken aToken = rsService.loadAccessTokenByTokenId(accessToken);
        validateToken(accessToken, aToken);

        //Validate client details by resource-id
        final OauthClient oauthClient = rsService.loadOauthClient(aToken.getClientId());
        validateOauthClient(accessToken, aToken, oauthClient);

        String username = aToken.getUsername();

        // Null username is invalid
        if (username == null) {
            throw new AccountException("Null usernames are not allowed by this realm.");
        }

        return new SimpleAuthenticationInfo(username, accessToken, getName());
    }

    public AuthenticationInfo doGetUsernamePasswordAuthenticationInfo(OAuth2Token token) throws AuthenticationException{
        String username = (String) token.getPrincipal();
        String password = new String((char[]) token.getCredentials());
        // 查询用户信息
        UpmsUser upmsUser = upmsApiService.selectUpmsUserByUsername(username);

        if (upmsUser==null) {
            throw new UnknownAccountException();
        }
        if (!upmsUser.getPassword().equals(MD5Util.MD5(password + upmsUser.getSalt()))) {
            throw new IncorrectCredentialsException();
        }
        if (upmsUser.getLocked().equals("1")) {
            throw new LockedAccountException();
        }

//        WebUtil.saveCurrentUser(upmsUser.getId());
        return new SimpleAuthenticationInfo(username, password, getName());
    }

    public void setRsService(OAuthRSService rsService) {
        this.rsService = rsService;
    }


}