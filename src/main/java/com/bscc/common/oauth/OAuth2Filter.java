package com.bscc.common.oauth;

import com.bscc.core.base.OAuth2Token;
import com.bscc.oauthz.model.OauthToken;
import com.bscc.oauthz.util.WebUtils;
import com.bscc.common.service.OAuthRSService;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Kent
 * @since 2017-06-18 17:45
 */
public class OAuth2Filter extends AuthenticatingFilter implements InitializingBean {

    private final static Logger _log = LoggerFactory.getLogger(OAuth2Filter.class);

    private OAuthRSService rsService;

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        final String accessToken = httpRequest.getParameter(OAuth.OAUTH_ACCESS_TOKEN);
        final OauthToken token = rsService.loadAccessTokenByTokenId(accessToken);

        String username = null;
        if (token != null) {
            username = token.getUsername();
            _log.debug("Set username[{}] and clientId[{}] to request that from AccessToken: {}", username, token.getClientId(), token);
            httpRequest.setAttribute(OAuth.OAUTH_CLIENT_ID, token.getClientId());
        } else {
            _log.debug("Not found AccessToken by access_token: {}", accessToken);
        }

        return new OAuth2Token(accessToken)
                .setUsername(username);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return executeLogin(request, response);
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException ae, ServletRequest request,
                                     ServletResponse response) {
//        OAuth2Token oAuth2Token = (OAuth2Token) token;

        final OAuthResponse oAuthResponse;
        try {
            oAuthResponse = OAuthRSResponse.errorResponse(401)
                    .setError(OAuthError.ResourceResponse.INVALID_TOKEN)
                    .setErrorDescription(ae.getMessage())
                    .buildJSONMessage();

            WebUtils.writeOAuthJsonResponse((HttpServletResponse) response, oAuthResponse);

        } catch (OAuthSystemException e) {
            _log.error("Build JSON message error", e);
            throw new IllegalStateException(e);
        }


        return false;
    }

    public void setRsService(OAuthRSService rsService) {
        this.rsService = rsService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(rsService, "rsService is null");
    }
}