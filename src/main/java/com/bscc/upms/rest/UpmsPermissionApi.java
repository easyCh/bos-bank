package com.bscc.upms.rest;

import com.bscc.core.base.BaseApi;
import com.bscc.core.base.BaseProvider;
import com.bscc.core.base.Tree;
import com.bscc.core.config.BosApi;
import com.bscc.core.constants.RestResult;
import com.bscc.core.constants.RestResultConstant;
import com.bscc.upms.model.UpmsPermission;
import com.bscc.upms.model.UpmsRolePermission;
import com.bscc.upms.service.UpmsPermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cha0res on 5/25/17.
 */
@RestController
@BosApi
@Api(value = "菜单接口", description = "用户权限管理-菜单管理")
@RequestMapping("/api/v1/upms_permission")
public class UpmsPermissionApi extends BaseApi<UpmsPermission> {
    @Override
    public UpmsPermissionService service() {
        return BaseProvider.getBean(UpmsPermissionService.class);
    }

    @ApiOperation(value = "列表查询接口",notes = "列表查询接口")
//    @RequiresPermissions("upms:permission:read")
    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "fields",value = "显示字段(字段名1,字段名2,...)",required = false,dataType = "string"),
    })
    @Override
    public Map findAll(HttpServletRequest request, HttpServletResponse response) {
        try{
            List<Tree> treeList = service().queryPermissionTree();
            return RestResult.status(RestResultConstant.OK).data(treeList).toMap();
        }catch (Exception e){
            logger.debug(e.toString());
            return RestResult.status(RestResultConstant.INTERNAL_SERVER_ERROR).toMap();
        }
    }

    @ApiOperation(value = "通过ID查询单个实体",notes = "通过ID查询单个实体")
//    @RequiresPermissions("upms:permission:read")
    @GetMapping("/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "fields",value = "显示字段(字段名1,字段名2,...)",required = false,dataType = "string")
    })
    @Override
    public Map findById(@PathVariable("id")String id, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();
        UpmsPermission upmsPermission = service().getById(id);
        if(upmsPermission != null){
            return RestResult.status(RestResultConstant.OK).data(upmsPermission).toMap();
        }else{
            return RestResult.status(RestResultConstant.NO_CONTENT).toMap();
        }
    }

    @ApiOperation(value = "保存实体")
//    @RequiresPermissions("upms:permission:create")
    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    @Override
    public Map update(@RequestBody UpmsPermission model) {
        boolean checkCode = true;
        if(StringUtils.isNotBlank(model.getCode())){
            UpmsPermission searchObj = new UpmsPermission();
            searchObj.setCode(model.getCode());
            List<UpmsPermission> list = service().selectList(searchObj);
            if(list != null && list.size() > 0){
                if(StringUtils.isNotBlank(model.getId())){
                    for(UpmsPermission up:list){
                        if(!model.getId().equals(up.getId())){
                            checkCode = false;
                            break;
                        }
                    }
                }else{
                    checkCode = false;
                }
            }
        }
        if(checkCode){
            return super.save(model);
        }else{
            return RestResult.status(RestResultConstant.PERMISSION_CODE_ERROR).toMap();
        }
    }

    @ApiOperation(value = "逻辑删除实体")
//    @RequiresPermissions("upms:permission:delete")
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
//    @RequiresPermissions("upms:permission:delete")
    @PostMapping("/delete/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string")
    })
    public Map deleteLogicPost(@PathVariable("id")String id){
        return service().deleteAndChildren(id);
    }

    /**
     *
     * @param upmsPermission
     * @param roleId
     * @return
     */
    @ApiOperation(value = "获取选中树结构数据")
//    @RequiresPermissions("upms:permission:create")
    @PostMapping("/getTree/{roleId}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    public Map getTree(@RequestBody UpmsPermission upmsPermission, @PathVariable("roleId") String roleId) {
        long start = System.currentTimeMillis() ;
        List<Tree> treeList = service().getTree(upmsPermission,roleId);
        long end = System.currentTimeMillis() ;
        System.out.println("----------查询树用时：" + (end - start) / 1000.0);
        return RestResult.status(RestResultConstant.OK).data(treeList).toMap();
    }
}
