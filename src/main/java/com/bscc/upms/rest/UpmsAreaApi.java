package com.bscc.upms.rest;

import com.bscc.common.utils.PinYinUtils;
import com.bscc.core.base.BaseApi;
import com.bscc.core.base.BaseProvider;
import com.bscc.core.config.BosApi;
import com.bscc.core.constants.RestResult;
import com.bscc.core.constants.RestResultConstant;
import com.bscc.common.utils.IdUtil;
import com.bscc.core.service.redis.RedisService;
import com.bscc.upms.model.UpmsArea;
import com.bscc.upms.service.UpmsAreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cha0res on 6/5/17.
 */
@RestController
@BosApi
@Api(value = "地区接口" ,description = "地区管理-地区接口")
@RequestMapping("/api/v1/upms_area")
public class UpmsAreaApi extends BaseApi<UpmsArea> {
    @Override
    public UpmsAreaService service() {
        return BaseProvider.getBean(UpmsAreaService.class);
    }

    @Autowired
    private RedisService redisService;

    @ApiOperation(value = "列表查询接口",notes = "列表查询接口")
    //    @RequiresPermissions("upms:area:read")
    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "sort",value = "排序字段",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "order",value = "升序/降序=>asc/desc",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "where",value = "查询条件",required = false,dataType = "string")
    })
    @Override
    public Map findAll(HttpServletRequest request, HttpServletResponse response) {
        return service().queryAreaTreeGrid(request);
    }

    @ApiOperation(value = "通过ID查询单个实体",notes = "通过ID查询单个实体")
//    @RequiresPermissions("upms:area:read")
    @GetMapping("/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "fields",value = "显示字段(字段名1,字段名2,...)",required = false,dataType = "string")
    })
    @Override
    public Map findById(@PathVariable("id") String id, HttpServletRequest request, HttpServletResponse response) {
        UpmsArea upmsArea = service().getById(id);
        if(upmsArea != null)
            return RestResult.status(RestResultConstant.OK).data(upmsArea).toMap();
        else
            return RestResult.status(RestResultConstant.NO_CONTENT).toMap();
    }

    @ApiOperation(value = "保存实体")
//    @RequiresPermissions("upms:area:create")
    @PostMapping
    @ApiImplicitParams({
    @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    @Override
    public Map update(@RequestBody UpmsArea model){
        if(StringUtils.isBlank(model.getId()))
            model.setId(IdUtil.getId());

        model.setPinyin(PinYinUtils.getQuanPin(model.getName()));
        if(StringUtils.isNotBlank(model.getPid()) && !"0".equals(model.getPid())){
            UpmsArea parent = service().getById(model.getPid());
            model.setLevel(parent.getLevel()+1);
            model.setFullName(parent.getFullName() + " " + model.getName());
            model.setPids(parent.getPids()+","+model.getId());
        }else{
            model.setLevel(1);
            model.setFullName(model.getName());
            model.setPids("0,"+model.getId());
        }
        return super.save(model);
    }


    @ApiOperation(value = "逻辑删除实体")
//    @RequiresPermissions("upms:area:delete")
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
//    @RequiresPermissions("upms:area:delete")
    @PostMapping("/delete/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string")
    })
    public Map deleteLogicPost(@PathVariable("id")String id){
        return service().deleteAndChild(id);
    }

    @ApiOperation(value = "树结构查询接口",notes = "树结构查询接口")
    //    @RequiresPermissions("upms:area:read")
    @GetMapping("/areaTree")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "type",value = "城市类型(1：普通城市，2：省会城市，3：热门城市)",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "level",value = "等级(1：国际，2：省份/直辖市，3：城市，4：地区)",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "pids",value = "父ID集合",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "pid",value = "父ID",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "maxChar",value = "首字母最大字符",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "minChar",value = "首字母最小字符",required = false,dataType = "string"),
    })
    public Map findAreaTree(HttpServletRequest request, HttpServletResponse response) {
        return service().queryAreaTree(request);
    }

    @ApiOperation(value = "区域导入")
//    @RequiresPermissions("upms:user:create")
    @PostMapping("/importArea")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    public Map importArea(@RequestParam("url") String url,@RequestParam("type") String type ) throws Exception{
        url = "D:\\intellijWork\\bos-upms\\target\\classes\\webapp\\pictrues\\2017\\7\\28e70842-7e9d-474e-b0af-264328eaccdb.xls";
        return RestResult.status(RestResultConstant.OK).data(service().importAreaThread(url,type)).toMap();
    }

    @ApiOperation(value = "获取导入状态")
//    @RequiresPermissions("upms:user:create")
    @PostMapping("/importStatus")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    public Map importStatus() throws Exception{
        Map<String, Object> map = new HashMap<String, Object>();

        if ("2".equals(redisService.get( "isImport")) ) {	//正在导入数据
            map.put( "state", 2 );
            map.put( "total", Integer.parseInt(redisService.get( "nImportTotal")) -1);
            map.put( "now", redisService.get( "nImportNow") );
        } else if ( "1".equals(redisService.get( "isImport"))) {	//正在整理数据
            map.put( "state", 1 );
        } else {	//未导入数据
            map.put( "state", 0 );
            map.put( "url", redisService.get( "sImportResult") );
            if( redisService.get( "sImportResult" ) != null )
                redisService.del( "sImportResult" );
            if( redisService.get( "isImport" ) != null )
                redisService.del( "isImport" );
            if( redisService.get( "nImportTotal" ) != null )
                redisService.del( "nImportTotal" );
            if( redisService.get( "nImportNow" ) != null )
                redisService.del( "nImportNow" );
        }
        return RestResult.status(RestResultConstant.OK).data(map).toMap();
    }

}
