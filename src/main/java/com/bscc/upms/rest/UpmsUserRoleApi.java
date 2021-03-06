package com.bscc.upms.rest;

import com.bscc.core.base.BaseApi;
import com.bscc.core.base.BaseProvider;
import com.bscc.core.base.BaseService;
import com.bscc.core.config.BosApi;
import com.bscc.upms.model.UpmsUserRole;
import com.bscc.upms.service.UpmsUserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 用户角色接口
 *
 * @author chensheng
 * Created by hp on 2017/6/2.
 */
@RestController
@BosApi
@Api(value = "用户角色接口", description = "用户权限管理-用户角色接口")
@RequestMapping("/api/v1/upms_user_role")
public class UpmsUserRoleApi extends  BaseApi<UpmsUserRole>{
    @Override
    public BaseService service() {
        return BaseProvider.getBean(UpmsUserRoleService.class);
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
    public Map update(@RequestBody UpmsUserRole model){
        //long time = System.currentTimeMillis();
        //model.setSort(time);
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

    /**
     * author chensheng
     * @param id
     * @return
     */
    @ApiOperation(value = "物理删除实体Post方式")
    // @RequiresPermissions("upms:system:delete")
    @PostMapping("/deleteTrue/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string")
    })
    public Map deleteTrue(@PathVariable("id")String id){
        return ((UpmsUserRoleService)service()).deleteTrue(id);
    }

    /**
     * author chensheng
     * @param roleId
     * @param userIds
     * @return
     */
    @ApiOperation(value = "批量配置保存角色Post方式")
    // @RequiresPermissions("upms:system:delete")
    @PostMapping("/addList")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "roleId",value = "角色id",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "userIds",value = "用户ids",required = true,dataType = "string")
    })
    public Map addList(@RequestParam String roleId,@RequestParam String userIds){
        return ((UpmsUserRoleService)service()).addList(roleId,userIds);
    }
}
