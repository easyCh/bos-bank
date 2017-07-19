package com.bscc.upms.rest;

import com.bscc.core.base.BaseApi;
import com.bscc.core.base.BaseProvider;
import com.bscc.core.config.BosApi;
import com.bscc.core.constants.RestResult;
import com.bscc.core.constants.RestResultConstant;
import com.bscc.common.utils.DataUtil;
import com.bscc.core.util.WebUtil;
import com.bscc.upms.model.UpmsSystem;
import com.bscc.upms.service.UpmsSystemService;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * @author Kent
 * @since 2017-04-16 19:08
 */
@RestController
@BosApi
@Api(value = "系统接口", description = "用户权限管理-系统接口")
@RequestMapping("/api/v1/upms_system")
public class UpmsSystemApi extends BaseApi<UpmsSystem> {

    @Override
    public UpmsSystemService service() {
        return BaseProvider.getBean(UpmsSystemService.class);
    }

    @ApiOperation(value = "列表查询接口",notes = "列表查询接口")
//    @RequiresPermissions("upms:system:read")
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
    //@RequiresPermissions("upms:system:create")
    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    @Override
    public Map update(@RequestBody UpmsSystem model){
        List<UpmsSystem> list = service().findSysList();
        if (list !=null && !list.isEmpty()){
            for (UpmsSystem upmsSystem:list){
                if (StringUtils.isNotBlank(upmsSystem.getTitle()) && upmsSystem.getTitle().equals(model.getTitle())){
                    return RestResult.status(RestResultConstant.VALIDATE_ERROR).data("系统名称重复").toMap();
                }
                if (StringUtils.isNotBlank(upmsSystem.getName()) && upmsSystem.getName().equals(model.getName())){
                    return RestResult.status(RestResultConstant.VALIDATE_ERROR).data("系统编码重复").toMap();
                }

            }
        }
        return super.save(model);
    }

    @ApiOperation(value = "逻辑删除实体")
    //@RequiresPermissions("upms:system:delete")
    @DeleteMapping("/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string")
    })
    @Override
    public Map deleteLogic(@PathVariable("id")String id){
        return super.delLogic(id);
    }

    @ApiOperation(value = "修改系统状态")
    @PostMapping("/editStatus")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    public Map editStatus(@RequestBody UpmsSystem upmsSystem){
        if(DataUtil.isEmpty(upmsSystem.getId())){
            return RestResult.status(RestResultConstant.BAD_REQUEST).toMap();
        }
        if(DataUtil.isEmpty(upmsSystem.getStatus())){
            return RestResult.status(RestResultConstant.BAD_REQUEST).toMap();
        }
        String userId = WebUtil.getCurrentUser();
        service().editStatus(upmsSystem.getId(),userId,upmsSystem.getStatus());
        return RestResult.status(RestResultConstant.OK).toMap();
    }

    @ApiOperation(value = "逻辑删除实体Post方式")
    //@RequiresPermissions("upms:user:delete")
    @PostMapping("/delete/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string")
    })
    public Map deleteLogicPost(@PathVariable("id")String id){
        return super.delLogic(id);
    }
}