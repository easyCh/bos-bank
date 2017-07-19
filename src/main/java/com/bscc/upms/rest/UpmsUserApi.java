package com.bscc.upms.rest;

import com.bscc.common.utils.IdUtil;
import com.bscc.common.utils.MD5Util;
import com.bscc.common.utils.MapUtils;
import com.bscc.core.base.BaseApi;
import com.bscc.core.base.BaseProvider;
import com.bscc.core.base.BaseService;
import com.bscc.core.config.BosApi;
import com.bscc.core.constants.RestResult;
import com.bscc.core.constants.RestResultConstant;
import com.bscc.upms.model.*;
import com.bscc.upms.service.UpmsRoleService;
import com.bscc.upms.service.UpmsUserOrganizationService;
import com.bscc.upms.service.UpmsUserRoleService;
import com.bscc.upms.service.UpmsUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author liuzhen
 * @since 2017-05-16 19:08
 */
@RestController
@BosApi
@Api(value = "用户接口", description = "用户权限管理-用户接口")
@RequestMapping("/api/v1/upms_user")
public class UpmsUserApi extends BaseApi<UpmsUser> {

    @Override
    public UpmsUserService service() {
        return BaseProvider.getBean(UpmsUserService.class);
    }

    public UpmsUserOrganizationService  upmsUserOrganizationService(){return BaseProvider.getBean(UpmsUserOrganizationService.class);}

    @ApiOperation(value = "列表查询接口",notes = "列表查询接口")
//    @RequiresPermissions("upms:user:read")
    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "请求令牌",required = true,dataType = "string"),
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

    @ApiOperation(value = "列表查询接口",notes = "组织关联列表查询接口")
//    @RequiresPermissions("upms:user:read")
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "请求令牌",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "page_num",value = "当前页",required = false,dataType = "int"),
            @ApiImplicitParam(paramType = "query",name = "page_size",value = "每页显示条数",required = false,dataType = "int"),
            @ApiImplicitParam(paramType = "query",name = "sort",value = "排序字段",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "order",value = "升序/降序=>asc/desc",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "fields",value = "显示字段(字段名1,字段名2,...)",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "where",value = "查询条件",required = false,dataType = "string")
    })

    public Map findAllList(HttpServletRequest request, HttpServletResponse response) {
        return service().findList(request);
    }

    @ApiOperation(value = "通过ID查询单个实体",notes = "通过ID查询单个实体")
//    @RequiresPermissions("upms:user:read")
    @GetMapping("/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "请求令牌",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "fields",value = "显示字段(字段名1,字段名2,...)",required = false,dataType = "string")
    })
    @Override
    public Map findById(@PathVariable("id")String id, HttpServletRequest request, HttpServletResponse response) {
//        return findResourceById(id, request, response);
        return RestResult.status(RestResultConstant.OK).data(service().getUpmsUserById(id)).toMap();
    }

    /**
     * editor chensheng
     * editor reason：保存新用户时应该将密码加密
     * @param model
     * @return
     */
    @ApiOperation(value = "保存实体")
//    @RequiresPermissions("upms:user:create")
    @PostMapping
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    @Override
    public Map update(@RequestBody UpmsUser model){
        if(StringUtils.isEmpty(model.getId())){
            model.setSalt(IdUtil.getId());
            String encryPassword = MD5Util.MD5(model.getPassword()+model.getSalt());
            model.setPassword(encryPassword);
        }
        if(StringUtils.isNotBlank(model.getUsername())){
            Map<String,Object> params = MapUtils.getInstance().put("username",model.getUsername()).put("delFlag",'0').get();
            UpmsUser upmsUser = BaseProvider.getBean(UpmsUserService.class).queryOne(params);
            if( null != upmsUser){
                return  RestResult.status(RestResultConstant.VALIDATE_ERROR).data("帐号重复").toMap();
            }
        }
        Map map = super.save(model);
        //保存组织-用户关联信息
        if (StringUtils.isNotBlank(model.getOrganizationId())) {
            UpmsUserOrganization upmsUserOrganization = new UpmsUserOrganization();
            upmsUserOrganization.setUserId(model.getId());
            upmsUserOrganization.setOrganizationId(model.getOrganizationId());
            upmsUserOrganization.validate();
            upmsUserOrganizationService().delByUserId(model.getId());
            upmsUserOrganizationService().save(upmsUserOrganization);
        }
        //保存角色信息
        if(StringUtils.isNoneBlank(model.getRole_name())){
            String[] role_names = model.getRole_name().split(",");
            UpmsUser user = (UpmsUser)map.get("data");
            for(String rn : role_names){
                Map<String,Object> params = MapUtils.getInstance().put("name",rn).put("delFlag",'0').get();
                UpmsRole upmsRole=BaseProvider.getBean(UpmsRoleService.class).queryOne(params);
                if(upmsRole!=null) {
                    Map<String,Object> paramUserRole = MapUtils.getInstance().put("userId",user.getId()).put("roleId",upmsRole.getId()).put("delFlag", '0').get();
                    UpmsUserRole upmsUserRole = BaseProvider.getBean(UpmsUserRoleService.class).queryOne(paramUserRole);
                    if(upmsUserRole == null){
                        upmsUserRole = new UpmsUserRole();
                        upmsUserRole.setRoleId(upmsRole.getId());
                        upmsUserRole.setUserId(user.getId());
                        BaseProvider.getBean(UpmsUserRoleService.class).update(upmsUserRole);
                    }
                }
            }
        }
        return map;
    }

    @ApiOperation(value = "用户是否锁定")
//    @RequiresPermissions("upms:user:create")
    @PostMapping("/isLock/{id}/{isLock}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "请求令牌",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "用户编号",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "isLock",value = "是否锁定（0：正常；1：锁定）",required = true,dataType = "string")
    })
    public Map isLock(@PathVariable("id") String id ,@PathVariable("isLock")String isLock){
        if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(isLock)){
            return service().isLock(id,isLock);
        }else {
            return RestResult.status(RestResultConstant.VALIDATE_ERROR).toMap();
        }
    }

    @ApiOperation(value = "逻辑删除实体")
    @RequiresPermissions("upms:user:delete")
    @DeleteMapping("/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "请求令牌",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string")
    })
    @Override
    public Map deleteLogic(@PathVariable("id")String id){
        //删除组织—用户关联信息
        upmsUserOrganizationService().delByUserId(id);
        return super.delLogic(id);
    }

    @ApiOperation(value = "逻辑删除实体Post方式")
//    @RequiresPermissions("upms:user:delete")
    @PostMapping("/delete/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "请求令牌",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "主键ID",required = true,dataType = "string")
    })
    public Map deleteLogicPost(@PathVariable("id")String id){
        Map map = delLogic(id);
        //删除组织—用户关联信息
        upmsUserOrganizationService().delByUserId(id);
        return map;
    }

    /**
     * author chensheng
     * @param id
     * @return
     */
    @ApiOperation(value = "根据组织机构id查询用户")
    //@RequiresPermissions("upms:user:delete")
    @PostMapping("/getUsersByOrId/{id}/{roleId}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "请求令牌",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "机构id",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "roleId",value = "角色Id",required = true,dataType = "string")
    })
    public Map getUserByOrId(@PathVariable("id")String id,@PathVariable("roleId")String roleId){
        return ((UpmsUserService)service()).getUsersByOrId(id,roleId);
    }

    /**
     * author chensheng
     * @param request
     *  @param response
     * @return
     */
    @ApiOperation(value = "根据角色id查询用户")
    //@RequiresPermissions("upms:user:delete")
    @GetMapping("/getUsersByRoleId")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "请求令牌",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "page_num",value = "当前页",required = false,dataType = "int"),
            @ApiImplicitParam(paramType = "query",name = "page_size",value = "每页显示条数",required = false,dataType = "int"),
            @ApiImplicitParam(paramType = "query",name = "sort",value = "排序字段",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "order",value = "升序/降序=>asc/desc",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "fields",value = "显示字段(字段名1,字段名2,...)",required = false,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "where",value = "查询条件",required = false,dataType = "string")
    })
    public Map getUsersByRoleId(HttpServletRequest request, HttpServletResponse response){
        return ((UpmsUserService)service()).getUsersByRoleId(request);
    }

    /**
     * author chensheng
     * @param id
     * @return
     */
    @ApiOperation(value = "根据角色id查询用户")
    //@RequiresPermissions("upms:user:delete")
    @GetMapping("/getAllUsersByRoleId/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "请求令牌",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "path",name = "id",value = "角色id",required = false,dataType = "int")
    })
    public Map getUsersByRoleId(@PathVariable("id")String id){
        return ((UpmsUserService)service()).selectAllUsersByRoleId(id);
    }

//    @ApiOperation(value = "列表查询接口",notes = "列表查询接口")
//    @RequiresPermissions("upms:user:read")
//    @GetMapping
//    @ApiImplicitParams({
////            @ApiImplicitParam(paramType = "query",name = "username",value = "用户名",required = false,dataType = "string"),
//            @ApiImplicitParam(paramType = "query",name = "page_num",value = "当前页",required = false,dataType = "int"),
//            @ApiImplicitParam(paramType = "query",name = "page_size",value = "每页显示条数",required = false,dataType = "int"),
//            @ApiImplicitParam(paramType = "query",name = "sort",value = "排序字段",required = false,dataType = "string"),
//            @ApiImplicitParam(paramType = "query",name = "order",value = "升序/降序=>asc/desc",required = false,dataType = "string"),
//            @ApiImplicitParam(paramType = "query",name = "fields",value = "显示字段(字段名1,字段名2,...)",required = false,dataType = "string"),
//            @ApiImplicitParam(paramType = "query",name = "where",value = "查询条件",required = false,dataType = "string")
//    })
//    public Map findList(HttpServletRequest request, HttpServletResponse response){
//        return service().query() ;
//    }

//    @ApiOperation(value = "登录接口",notes = "登录接口")
//    @PostMapping("/login")
//    public Map login(@RequestBody UpmsLogin model){
//        return service().login(model);
//    }

    @ApiOperation(value = "用户注册接口",notes = "用户注册接口")
    @PostMapping("/regist")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    public Map regist(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role_name = request.getParameter("role_name");
        return service().regist(username,password,role_name) ;

        /*if(StringUtils.isBlank(username)||StringUtils.isBlank(password)||StringUtils.isBlank(role_name)){
            return RestResult.status(RestResultConstant.BAD_REQUEST).toMap();
        }
        String[] role_names = role_name.split(",");
        Map<String,Object> paramsUser = MapUtils.getInstance().put("username",username).put("delFlag",'0').get();
        UpmsUser upmsUser=BaseProvider.getBean(UpmsUserService.class).queryOne(paramsUser);
        if(upmsUser==null){
            upmsUser = new UpmsUser();
        }
        upmsUser.setUsername(username);
        upmsUser.setSalt(IdUtil.getId());
        upmsUser.setPassword(MD5Util.MD5(password+upmsUser.getSalt()));
        upmsUser = BaseProvider.getBean(UpmsUserService.class).update(upmsUser);
        for(String rn : role_names){
            Map<String,Object> params = MapUtils.getInstance().put("name",rn).put("delFlag",'0').get();
            UpmsRole upmsRole=BaseProvider.getBean(UpmsRoleService.class).queryOne(params);
            if(upmsRole!=null) {
                Map<String,Object> paramUserRole = MapUtils.getInstance().put("userId",upmsUser.getId()).put("roleId",upmsRole.getId()).put("delFlag", '0').get();
                UpmsUserRole upmsUserRole=BaseProvider.getBean(UpmsUserRoleService.class).queryOne(paramUserRole);
                if(upmsUserRole==null){
                    upmsUserRole = new UpmsUserRole();
                    upmsUserRole.setRoleId(upmsRole.getId());
                    upmsUserRole.setUserId(upmsUser.getId());
                    BaseProvider.getBean(UpmsUserRoleService.class).update(upmsUserRole);
                }
            }
        }
        return RestResult.status(RestResultConstant.OK).data(upmsUser).toMap();*/
    }

    @ApiOperation(value = "修改密码")
    // @RequiresPermissions("upms:system:delete")
    @PostMapping("/updatePassword")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    public Map updatePassword(@RequestParam("id") String  id,@RequestParam("password") String password,@RequestParam("newpassword") String newpassword,@RequestParam("passwordagin") String passwordagin){
        UpmsUser upmsUser = new UpmsUser();
        upmsUser.setId(id);
        upmsUser.setPassword(password);
        return service().updatePassword(upmsUser,newpassword,passwordagin);
    }

    @ApiOperation(value = "修改基本信息")
    // @RequiresPermissions("upms:system:delete")
    @PostMapping("/editUser")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string")
    })
    public Map editUser(@RequestBody UpmsUser upmsUser){
        return super.save(upmsUser);
    }

    @ApiOperation(value = "检测用户名是否重复")
    // @RequiresPermissions("upms:system:delete")
    @GetMapping("checkUsernameRepeat")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "query",name = "access_token",value = "access_token",required = true,dataType = "string"),
            @ApiImplicitParam(paramType = "userId",name = "userId",value = "用户编号",dataType = "string"),
            @ApiImplicitParam(paramType = "username",name = "username",value = "账号",required = true,dataType = "string")
    })
    public boolean checkUsernameRepeat(@RequestParam("userId")String userId,@RequestParam("username")String username){
        return service().checkUsernameRepeat(userId,username);
    }
}