package com.bscc.upms.rest;

import com.bscc.common.utils.MapUtils;
import com.bscc.core.base.BaseProvider;
import com.bscc.core.base.Tree;
import com.bscc.core.config.BosApi;
import com.bscc.core.constants.RestResult;
import com.bscc.core.constants.RestResultConstant;
import com.bscc.oauthz.model.OauthToken;
import com.bscc.oauthz.service.OauthTokenService;
import com.bscc.upms.model.UpmsPermission;
import com.bscc.upms.model.UpmsSystem;
import com.bscc.upms.model.UpmsUser;
import com.bscc.upms.service.UpmsPermissionService;
import com.bscc.upms.service.UpmsSystemService;
import com.bscc.upms.service.UpmsUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author Kent
 * @since 2017-06-29 01:53
 */
@RestController
@BosApi
@Api(value = "用户权限公共接口", description = "用户权限管理-公共接口")
@RequestMapping("/api/v1/upms")
public class UpmsApi {
    @ApiOperation(value = "验证access_token",notes = "验证access_token是否正确")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "请求令牌",required = true,dataType = "string"),
    })
    @GetMapping
    public Map checkAccessToken(HttpServletRequest request, HttpServletResponse response) {
        return RestResult.status(RestResultConstant.OK).toMap();
    }

    @ApiOperation(value = "获取当前令牌用户",notes = "通过access_token获取当前用户")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "请求令牌",required = true,dataType = "string"),
    })
    @GetMapping("/current_user")
    public Map currentUser(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = request.getParameter("access_token");
        Map<String, Object> params = MapUtils.getInstance()
                .put("tokenId",accessToken)
                .put("delFlag","0").get();
        OauthToken oauthToken=BaseProvider.getBean(OauthTokenService.class).queryOne(params);
        UpmsUser upmsUser = BaseProvider.getBean(UpmsUserService.class).queryById(oauthToken.getUsername());
        return RestResult.status(RestResultConstant.OK).data(upmsUser).toMap();
    }

    @ApiOperation(value = "获取当前令牌用户系统",notes = "通过access_token获取当前用户系统")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "请求令牌",required = true,dataType = "string"),
    })
    @GetMapping("/system")
    public Map system(HttpServletRequest request, HttpServletResponse response) {
        UpmsUser upmsUser = (UpmsUser) currentUser(request,response).get("data");
        if(upmsUser!=null) {
            List<UpmsSystem> upmsSystemList= BaseProvider.getBean(UpmsSystemService.class).findSystemByUserId(upmsUser.getId());
            return RestResult.status(RestResultConstant.OK).data(upmsSystemList).toMap();
        }else{
            return RestResult.status(RestResultConstant.NO_CONTENT).toMap();
        }
    }

    @ApiOperation(value = "获取当前令牌用户菜单",notes = "通过access_token获取当前用户菜单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "请求令牌",required = true,dataType = "string"),
    })
    @GetMapping("/menu")
    public Map menu(HttpServletRequest request, HttpServletResponse response) {
        UpmsUser upmsUser = (UpmsUser) currentUser(request,response).get("data");
        String systemId = request.getParameter("system_id");
        if(upmsUser!=null) {
            List<UpmsPermission> upmsPermissions= BaseProvider.getBean(UpmsPermissionService.class).selectPermissionsByUserIdAndSystemId(upmsUser.getId(), systemId);
            List<Tree> permissionTree = BaseProvider.getBean(UpmsPermissionService.class).getPermissionTree(upmsPermissions);
            return RestResult.status(RestResultConstant.OK).data(permissionTree).toMap();
        }else{
            return RestResult.status(RestResultConstant.NO_CONTENT).toMap();
        }
    }
}