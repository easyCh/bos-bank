package com.bscc.upms.rest;

import com.bscc.common.utils.MapUtils;
import com.bscc.core.base.BaseApi;
import com.bscc.core.base.BaseProvider;
import com.bscc.core.config.BosApi;
import com.bscc.core.constants.RestResult;
import com.bscc.core.constants.RestResultConstant;
import com.bscc.upms.model.UpmsOrganization;
import com.bscc.upms.model.UpmsRole;
import com.bscc.upms.service.UpmsOrganizationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cha0res on 6/1/17.
 */
@RestController
@BosApi
@Api(value = "组织接口", description = "组织机构管理")
@RequestMapping("/api/v1/upms_organization")
public class UpmsOrganizationApi extends BaseApi<UpmsOrganization> {
    @Override
    public UpmsOrganizationService service() {return BaseProvider.getBean(UpmsOrganizationService.class);}

    @ApiOperation(value = "列表查询接口",notes = "列表查询接口")
//    @RequiresPermissions("upms:organization:read")
    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "page_num",value = "当前页",required = false,dataType = "int"),
            @ApiImplicitParam(paramType = "query",name = "page_size",value = "每页显示条数",required = false,dataType = "int"),
            @ApiImplicitParam(paramType = "query",name = "pid",value = "父节点",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "name",value = "机构名",required = false,dataType = "string")
    })
    @Override
    public Map findAll(HttpServletRequest request, HttpServletResponse response) {
        return findAllResources(request,response);
    }


    @ApiOperation(value = "通过ID查询单个实体",notes = "通过ID查询单个实体")
//    @RequiresPermissions("upms:organization:read")
    @GetMapping("/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string")
    })
    @Override
    public Map findById(@PathVariable("id")String id, HttpServletRequest request, HttpServletResponse response) {
        return service().getById(id);
    }

    @ApiOperation(value = "保存实体")
//    @RequiresPermissions("upms:organization:create")
    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    @Override
    public Map update(@RequestBody UpmsOrganization model){
        Map<String, Object> params = new HashMap<String, Object>();
        params = MapUtils.getInstance().put("code", model.getCode()).put("delFlag", '0').get();
        if(StringUtils.isEmpty(model.getId())) {
            UpmsOrganization upmsOrganization = BaseProvider.getBean(UpmsOrganizationService.class).queryOne(params);
            if (upmsOrganization != null) {
                return RestResult.status(RestResultConstant.BAD_REQUEST).data("编码重复").toMap();
            }
        }else{
            List<UpmsOrganization> upmsOrganization = BaseProvider.getBean(UpmsOrganizationService.class).queryList(params);
            if (upmsOrganization != null && upmsOrganization.size()>0) {
                for(UpmsOrganization uo:upmsOrganization){
                    if(!model.getId().equals(uo.getId())){
                        return RestResult.status(RestResultConstant.BAD_REQUEST).data("编码重复").toMap();
                    }
                }
            }
        }

        model = BaseProvider.getBean(UpmsOrganizationService.class).update(model);

        if(StringUtils.isBlank(model.getPid()) || "0".equals(model.getPid())){
            model.setPid("0");
            model.setPids("0,"+model.getId());
        }else{
            params = MapUtils.getInstance().put("id",model.getPid()).get();
            UpmsOrganization parent = BaseProvider.getBean(UpmsOrganizationService.class).queryOne(params);
            model.setPids(parent.getPids()+","+model.getId());
        }
        return super.save(model);
    }

    @ApiOperation(value = "逻辑删除实体")
//    @RequiresPermissions("upms:organization:delete")
    @DeleteMapping("/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string")
    })
    @Override
    public Map deleteLogic(@PathVariable("id")String id){
        return service().deleteAndChildren(id);
    }

    @ApiOperation(value = "逻辑删除实体Post方式")
//    @RequiresPermissions("upms:organization:delete")
    @PostMapping("/delete/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string")
    })
    public Map deleteLogicPost(@PathVariable("id")String id){
        return service().deleteAndChildren(id);
    }


    /**
     * 查询树结构数据
     * author chensheng
     * @return
     */
    @ApiOperation(value = "查询树结构")
    @GetMapping("/orgTree")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "selectedId",value = "选中项",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "checkedIds",value = "勾选项",required = false,dataType = "string")
   })
    public Map findOrgTree(HttpServletRequest request){
        return service().queryOrganizationTree(request);
    }


    /**
     * 查询单选树结构数据
     * author chensheng
     * @return
     */
    @ApiOperation(value = "查询树结构")
    @GetMapping("/getTree")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    public Map getTree(){
        UpmsOrganization upmsOrganization = new UpmsOrganization();
        upmsOrganization.setPid("0");
        return service().getTree(upmsOrganization);
    }
}
