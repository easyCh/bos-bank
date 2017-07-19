package com.bscc.common.oauth;

import org.apache.shiro.authc.AuthenticationException;

/**
 * @author Kent
 * @since 2017-06-23 01:03
 */
public class OAuth2AuthenticationException extends AuthenticationException {

    public OAuth2AuthenticationException() {
    }

    public OAuth2AuthenticationException(String message) {
        super(message);
    }

    public OAuth2AuthenticationException(Throwable cause) {
        super(cause);
    }

    public OAuth2AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}