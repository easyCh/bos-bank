package com.bscc.upms.rest;

import com.bscc.core.base.BaseApi;
import com.bscc.core.base.BaseProvider;
import com.bscc.core.base.BaseService;
import com.bscc.core.config.BosApi;
import com.bscc.core.constants.RestResult;
import com.bscc.core.constants.RestResultConstant;
import com.bscc.upms.model.UpmsDict;
import com.bscc.upms.service.UpmsDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典接口类
 *
 * @author niu
 * @create 2017-05-25 上午 09:56
 **/
@RestController
@BosApi
@Api(value = "字典接口" ,description = "字典管理-字典接口")
@RequestMapping("/api/v1/upms_dict")
public class UpmsDictApi extends BaseApi<UpmsDict> {
    @Override
    public UpmsDictService service() {
        return BaseProvider.getBean(UpmsDictService.class);
    }
    @ApiOperation(value = "列表查询接口",notes = "列表查询接口")
//    @RequiresPermissions("upms:user:read")
    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
//            @ApiImplicitParam(paramType = "query",name = "username",value = "用户名",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "page_num",value = "当前页",required = false,dataType = "int"),
            @ApiImplicitParam(paramType = "query",name = "page_size",value = "每页显示条数",required = false,dataType = "int"),
            @ApiImplicitParam(paramType = "query",name = "sort",value = "排序字段",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "order",value = "升序/降序=>asc/desc",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "fields",value = "显示字段(字段名1,字段名2,...)",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "where",value = "查询条件",required = false,dataType = "string")
    })
    @Override
    public Map findAll(HttpServletRequest request, HttpServletResponse response) {
        return super.findAllResources(request,response);
    }


    @ApiOperation(value = "字典类型列表")
    //    @RequiresPermissions("upms:user:read")
    @GetMapping("/type/")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    public Map findTypeList(){
        return RestResult.status(RestResultConstant.OK).data(service().findTypeList()).toMap();
    }

    @ApiOperation(value = "校验同类型字典键值")
    @PostMapping("/verify/")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })

    public Map verify(@RequestBody UpmsDict model){
        UpmsDict upmsDict = new UpmsDict();
        upmsDict = model;
        Map<String,Boolean> map = new HashMap<String, Boolean>();
        map.put("valid",true);
        List<UpmsDict> upmsDictList = new ArrayList<UpmsDict>();
        upmsDictList = service().findList(upmsDict);
        if (upmsDictList !=null && !upmsDictList.isEmpty()) {
            for (UpmsDict upmsDict1 :upmsDictList){
                if (StringUtils.isNotBlank(upmsDict1.getValue()) && upmsDict1.getValue().equals(model.getValue())){
                    map.put("valid",false);
                }
            }
        }
        return map;
    }

    @ApiOperation(value = "通过ID查询单个实体",notes = "通过ID查询单个实体")
//    @RequiresPermissions("upms:user:read")
    @GetMapping("/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "fields",value = "显示字段(字段名1,字段名2,...)",required = false,dataType = "string")
    })
    @Override
    public Map findById(@PathVariable("id") String id, HttpServletRequest request, HttpServletResponse response) {
        return findResourceById(id,request,response);
    }


    @ApiOperation(value = "保存实体")
//    @RequiresPermissions("upms:user:create")
    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    @Override
    public Map update(@RequestBody UpmsDict model) {
        List<UpmsDict> upmsDictList = new ArrayList<UpmsDict>();
        upmsDictList = service().findList(model);
        if (model !=null && StringUtils.isBlank(model.getId())){
            if (upmsDictList !=null && !upmsDictList.isEmpty()) {
                for (UpmsDict upmsDict :upmsDictList){
                    if (StringUtils.isNotBlank(upmsDict.getValue()) && upmsDict.getValue().equals(model.getValue())){
                        return RestResult.status(RestResultConstant.DICT_VALUE_ERROR).toMap();
                    }
                }
            }
        }else {
            if (upmsDictList !=null && !upmsDictList.isEmpty()) {
                for (UpmsDict upmsDict :upmsDictList){
                    if (!model.getId().equals(upmsDict.getId())&& StringUtils.isNotBlank(upmsDict.getValue()) && upmsDict.getValue().equals(model.getValue())){
                        return RestResult.status(RestResultConstant.DICT_VALUE_ERROR).toMap();
                    }
                }
            }
        }
        return save(model);
    }


    @ApiOperation(value = "逻辑删除实体")
//    @RequiresPermissions("upms:user:delete")
    @DeleteMapping("/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string")
    })
    @Override
    public Map deleteLogic(@PathVariable("id") String id) {
        return super.delLogic(id);
    }

    @ApiOperation(value = "逻辑删除实体Post方式")
//    @RequiresPermissions("upms:user:delete")
    @PostMapping("/delete/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string")
    })
    public Map deleteLogicPost(@PathVariable("id")String id){
        return super.delLogic(id);
    }

}
