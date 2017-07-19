package com.bscc.common.service.impl;

import com.bscc.core.base.BaseProvider;
import com.bscc.oauthz.model.OauthClient;
import com.bscc.oauthz.model.OauthToken;
import com.bscc.oauthz.service.OauthClientService;
import com.bscc.oauthz.service.OauthTokenService;
import com.bscc.common.service.OAuthRSService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kent
 * @since 2017-06-22 15:29
 */
@Service("oAuthRSService")
public class OAuthRSServiceImpl implements OAuthRSService {

    private static final Logger LOG = LoggerFactory.getLogger(OAuthRSServiceImpl.class);

    @Override
    public OauthToken loadAccessTokenByTokenId(String tokenId) {
        if(StringUtils.isBlank(tokenId)){
            return null;
        }
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("tokenId",tokenId);
        List<OauthToken> accessTokens = BaseProvider.getBean(OauthTokenService.class).queryList(params);

        if(accessTokens.isEmpty()){
            return null;
        }else{
            LOG.debug("Load AccessToken[{}] from DB, key = {}", accessTokens.get(0), accessTokens.get(0).getId());
            return accessTokens.get(0);
        }
    }

    @Override
    public OauthClient loadOauthClient(String clientId) {
        if(StringUtils.isBlank(clientId)){
            return null;
        }
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("clientId",clientId);
        List<OauthClient> oauthClients = BaseProvider.getBean(OauthClientService.class).queryList(params);

        if(oauthClients.isEmpty()){
            return null;
        }else{
            LOG.debug("Load OauthClient[{}] from DB, key = {}", oauthClients.get(0), oauthClients.get(0).getId());
            return oauthClients.get(0);
        }
    }
}