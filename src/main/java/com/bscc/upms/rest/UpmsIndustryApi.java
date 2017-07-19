package com.bscc.upms.rest;

import com.bscc.core.base.BaseApi;
import com.bscc.core.base.BaseProvider;
import com.bscc.core.config.BosApi;
import com.bscc.core.constants.RestResult;
import com.bscc.core.constants.RestResultConstant;
import com.bscc.common.utils.IdUtil;
import com.bscc.upms.model.UpmsIndustry;
import com.bscc.upms.service.UpmsIndustryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by cha0res on 6/19/17.
 */
@RestController
@BosApi
@Api(value = "行业接口" ,description = "行业管理-行业接口")
@RequestMapping("/api/v1/upms_industry")
public class UpmsIndustryApi extends BaseApi<UpmsIndustry> {
    @Override
    public UpmsIndustryService service(){
        return BaseProvider.getBean(UpmsIndustryService.class);
    }

    @ApiOperation(value = "列表查询接口",notes = "列表查询接口")
    //    @RequiresPermissions("upms:industry:read")
    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "sort",value = "排序字段",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "order",value = "升序/降序=>asc/desc",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "where",value = "查询条件",required = false,dataType = "string")
    })
    @Override
    public Map findAll(HttpServletRequest request, HttpServletResponse response) {
        return service().queryIndustryTreeGrid(request);
    }

    @ApiOperation(value = "通过ID查询单个实体",notes = "通过ID查询单个实体")
//    @RequiresPermissions("upms:industry:read")
    @GetMapping("/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "fields",value = "显示字段(字段名1,字段名2,...)",required = false,dataType = "string")
    })
    @Override
    public Map findById(@PathVariable("id") String id, HttpServletRequest request, HttpServletResponse response) {
        UpmsIndustry upmsIndustry = service().getById(id);
        if(upmsIndustry != null)
            return RestResult.status(RestResultConstant.OK).data(upmsIndustry).toMap();
        else
            return RestResult.status(RestResultConstant.NO_CONTENT).toMap();
    }

    @ApiOperation(value = "保存实体")
//    @RequiresPermissions("upms:industry:create")
    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    @Override
    public Map update(@RequestBody UpmsIndustry model){
        if(StringUtils.isBlank(model.getId()))
            model.setId(IdUtil.getId());

        if(StringUtils.isNotBlank(model.getParentId()) && !"0".equals(model.getParentId())){
            UpmsIndustry parent = service().getById(model.getParentId());
            model.setLevel(parent.getLevel()+1);
            model.setParentIds(parent.getParentIds()+","+model.getId());
        }else{
            model.setLevel(1);
            model.setParentId("0");
            model.setParentIds("0,"+model.getId());
        }
        return super.save(model);
    }

    @ApiOperation(value = "逻辑删除实体")
//    @RequiresPermissions("upms:industry:delete")
    @DeleteMapping("/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string")
    })
    @Override
    public Map deleteLogic(@PathVariable("id") String id) {
        return service().deleteAndChild(id);
    }

    @ApiOperation(value = "逻辑删除实体Post方式")
//    @RequiresPermissions("upms:industry:delete")
    @PostMapping("/delete/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string")
    })
    public Map deleteLogicPost(@PathVariable("id")String id){
        return service().deleteAndChild(id);
    }

    @ApiOperation(value = "树结构查询接口",notes = "树结构查询接口")
    //    @RequiresPermissions("upms:industry:read")
    @GetMapping("/industryTree")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "level",value = "等级(1~4)",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "parentIds",value = "父ID集合",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "parentId",value = "父ID",required = false,dataType = "string")
    })
    public Map findAreaTree(HttpServletRequest request, HttpServletResponse response) {
        return service().queryIndustryTree(request);
    }
}
