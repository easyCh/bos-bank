package com.bscc.common.service;

import com.bscc.oauthz.model.OauthClient;
import com.bscc.oauthz.model.OauthToken;

/**
 * @author Kent
 * @since 2017-06-22 15:25
 */
public interface OAuthRSService {

    OauthToken loadAccessTokenByTokenId(String tokenId);

    OauthClient loadOauthClient(String clientId);
}
