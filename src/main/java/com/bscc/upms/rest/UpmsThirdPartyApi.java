package com.bscc.upms.rest;

import com.alibaba.fastjson.JSON;
import com.bscc.common.utils.HttpclientUtils;
import com.bscc.common.utils.MapUtils;
import com.bscc.core.base.BaseApi;
import com.bscc.core.base.BaseProvider;
import com.bscc.core.base.BaseService;
import com.bscc.core.config.BosApi;
import com.bscc.core.constants.RestResult;
import com.bscc.core.constants.RestResultConstant;
import com.bscc.upms.model.UpmsThirdParty;
import com.bscc.upms.model.UpmsUser;
import com.bscc.upms.service.UpmsRoleService;
import com.bscc.upms.service.UpmsThirdPartyService;
import com.bscc.upms.service.UpmsUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2017/7/17.
 */
@RestController
@BosApi
@Api(value = "第三方接口", description = "用户第三方管理-第三方接口")
@RequestMapping("/api/v1/upms_third_party")
public class UpmsThirdPartyApi extends BaseApi<UpmsThirdParty> {
    @Override
    public UpmsThirdPartyService service() {
        return BaseProvider.getBean(UpmsThirdPartyService.class);
    }

    @ApiOperation(value = "列表查询接口",notes = "列表查询接口")
    // @RequiresPermissions("upms:system:read")
    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "page_num",value = "当前页",required = false,dataType = "int"),
            @ApiImplicitParam(paramType = "query",name = "page_size",value = "每页显示条数",required = false,dataType = "int"),
            @ApiImplicitParam(paramType = "query",name = "sort",value = "排序字段",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "order",value = "升序/降序=>asc/desc",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "fields",value = "显示字段(字段名1,字段名2,...)",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "where",value = "查询条件",required = false,dataType = "string")
    })
    @Override
    public Map findAll(HttpServletRequest request, HttpServletResponse response) {
        return findAllResources(request,response);
    }

    @ApiOperation(value = "通过ID查询单个实体",notes = "通过ID查询单个实体")
    //@RequiresPermissions("upms:system:read")
    @GetMapping("/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "fields",value = "显示字段(字段名1,字段名2,...)",required = false,dataType = "string")
    })
    @Override
    public Map findById(@PathVariable("id")String id, HttpServletRequest request, HttpServletResponse response) {
        return findResourceById(id, request, response);
    }

    @ApiOperation(value = "保存实体")
    // @RequiresPermissions("upms:system:create")
    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    @Override
    public Map update(@RequestBody UpmsThirdParty model){
        if(StringUtils.isBlank(model.getId())){
            if(StringUtils.isBlank(model.getOpenId()) || StringUtils.isBlank(model.getType()) ||StringUtils.isBlank(model.getUserId())){
                return RestResult.status(RestResultConstant.BAD_REQUEST).data("缺少参数").toMap();
            }
            Map<String,Object> params = MapUtils.getInstance().put("openId",model.getOpenId()).put("type",model.getType()).put("userId",model.getUserId()).get();
            UpmsThirdParty utp = service().queryOne(params);
            if(null != utp){
                model.setId(utp.getId());
            }
        }
        return super.save(model);
    }

    @ApiOperation(value = "逻辑删除实体")
    // @RequiresPermissions("upms:system:delete")
    @DeleteMapping("/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string")
    })
    @Override
    public Map deleteLogic(@PathVariable("id")String id){
        return super.delLogic(id);
    }

    @ApiOperation(value = "逻辑删除实体Post方式")
    // @RequiresPermissions("upms:system:delete")
    @PostMapping("/delete/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string")
    })
    public Map deleteLogicPost(@PathVariable("id")String id){
        return super.delLogic(id);
    }

    @ApiOperation(value = "第三方登录",notes = "第三方登录")
    //@RequiresPermissions("upms:system:read")
    @PostMapping("/login")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "请求令牌",required = true,dataType = "string")
    })
    public Map login(@RequestBody UpmsThirdParty upmsThirdParty) {
        if(StringUtils.isBlank(upmsThirdParty.getType()) || StringUtils.isBlank(upmsThirdParty.getOpenId())){
           return RestResult.status(RestResultConstant.BAD_REQUEST).data("第三方类型或Open ID 为空").toMap();
        }
        Map<String,Object> params = MapUtils.getInstance().put("type",upmsThirdParty.getType()).put("openId",upmsThirdParty.getOpenId()).get();
        UpmsThirdParty utp = service().queryOne(params);
        if (utp != null && StringUtils.isNotBlank(utp.getUserId())) {
            UpmsUser user = BaseProvider.getBean(UpmsUserService.class).queryById(utp.getUserId());

            // 通过用户名和密码进行模拟登录

            try {
                //调用登录获取token 地址
                String url = "http://127.0.0.1:8080/oauth/token";
                //请求入参
                List<NameValuePair> formparams = new ArrayList<NameValuePair>();
                formparams.add(new BasicNameValuePair("redirect_uri", "http://www.baidu.com"));
                formparams.add(new BasicNameValuePair("client_id", "57de371e63894f29aa073ddb55f28373"));
                formparams.add(new BasicNameValuePair("client_secret", "c71496ba18c346f8a5139176cd98f0e7"));
                formparams.add(new BasicNameValuePair("grant_type", "password"));
                formparams.add(new BasicNameValuePair("scope", "read"));
                formparams.add(new BasicNameValuePair("username", user.getUsername()));
                formparams.add(new BasicNameValuePair("password", user.getPlainCode()));

                System.out.println("------> formparams : "+JSON.toJSONString(formparams));
                String result = HttpclientUtils.post(url, formparams);
                System.out.println("------> result : " + result);
                return RestResult.status(RestResultConstant.OK).data(JSON.parseObject(result, Map.class)).toMap();
            } catch (Exception e) {
                e.printStackTrace();
                return RestResult.status(RestResultConstant.BAD_REQUEST).data("登录失败").toMap();
            }
        } else {
            return RestResult.status(RestResultConstant.BAD_REQUEST).data("第三方信息不存在").toMap();
        }

    }
}
