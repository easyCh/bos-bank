package com.bscc.upms.rest;

import com.bscc.core.base.BaseApi;
import com.bscc.core.base.BaseProvider;
import com.bscc.core.base.BaseService;
import com.bscc.core.config.BosApi;
import com.bscc.upms.model.UpmsFile;
import com.bscc.upms.service.UpmsFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import netscape.javascript.JSObject;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author liuzhen
 * @since 2017-05-16 19:08
 */
@RestController
@BosApi
@Api(value = "文件接口", description = "用户权限管理-用户接口")
@RequestMapping("/api/v1/upms_file")
public class UpmsFileApi extends BaseApi<UpmsFile> {

    @Override
    public BaseService service() {
        return BaseProvider.getBean(UpmsFileService.class);
    }

    @ApiOperation(value = "列表查询接口",notes = "列表查询接口")
//    @RequiresPermissions("upms:user:read")
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
//    @RequiresPermissions("upms:user:read")
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
//    @RequiresPermissions("upms:user:create")
    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    @Override
    public Map update(@RequestBody UpmsFile model){
        return super.save(model);
    }

    @ApiOperation(value = "逻辑删除实体")
//    @RequiresPermissions("upms:user:delete")
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
//    @RequiresPermissions("upms:user:delete")
    @PostMapping("/delete/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string")
    })
    public Map deleteLogicPost(@PathVariable("id")String id){
        return super.delLogic(id);
    }

    @ApiOperation(value = "文件上传")
//    @RequiresPermissions("upms:user:create")
    @PostMapping("/uploadFileBase64")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    public Map uploadFile(@RequestBody UpmsFile model ) throws Exception{
        MultipartFile file = null ;
        return ((UpmsFileService)service()).uploadFile(file,model);
    }
    @ApiOperation(value = "文件上传")
//    @RequiresPermissions("upms:user:create")
    @PostMapping("/uploadFile")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    public Map uploadFile(@RequestParam("upload") MultipartFile file) throws Exception{
        return ((UpmsFileService)service()).uploadFile(file,null);
    }

    @ApiOperation(value = "文件上传")
//    @RequiresPermissions("upms:user:create")
    @PostMapping("/uploadFileOn")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    public Map uploadFile(@RequestParam("upload") MultipartFile file,@RequestParam("uploadType") String uploudType ) throws Exception{
        UpmsFile upmsFile = new UpmsFile();
        upmsFile.setUploadType(uploudType);
        return ((UpmsFileService)service()).uploadFile(file,upmsFile);
    }
}