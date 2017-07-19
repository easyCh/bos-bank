package com.bscc.common.aop;

import com.alibaba.fastjson.JSON;
import com.bscc.common.service.OAuthRSService;
import com.bscc.core.base.BaseProvider;
import com.bscc.oauthz.model.OauthToken;
import com.google.common.collect.Maps;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Map;

/**
 * <p>Title: TestAop</p>
 * <p>Description: 当前用户切面类</p>
 *
 * @author LiuZhen
 * @Company 博视创诚
 * @data 2017-07-12 12:13
 */
@Aspect
@Configuration
public class CurrentUserAop {

    /*
     * 定义一个切入点
     */
    @Pointcut("execution(public * com.bscc.core.util.CurrentUser.getCurrentUserId()) ")
    public void excudeService(){}

    /*
     * 拦截获取当前用户编号
     */
    @Before("excudeService()")
    public void beforeGetCurrentUserId(JoinPoint joinPoint){
        Signature signature = joinPoint.getSignature();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        Enumeration<String> enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()){
            String parameter = enumeration.nextElement();
            if ("access_token".equals(parameter)) {
                String accessToken = request.getParameter(parameter);
                final OauthToken token = BaseProvider.getBean(OAuthRSService.class).loadAccessTokenByTokenId(accessToken);
                if (token != null) {
                    try {
                        Object object = joinPoint.getThis();
                        Method method = signature.getDeclaringType().getMethod("setCurrentUserId", new Class[]{String.class});
                        Object[] args = new Object[]{token.getUsername()};
                        method.invoke(object, args);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                break ;
            }
        }


    }

    /*
     * 至前拦截测试
     */
    /*@Before("excudeService()")
    public void beforeGetCurrentUserId(JoinPoint joinPoint){
        System.err.println ("切面before执行了。。。。id==");
        System.out.println("我是前置通知!!!");
        //获取目标方法的参数信息
        Object[] obj = joinPoint.getArgs();
        //AOP代理类的信息
        joinPoint.getThis();
        //代理的目标对象
        joinPoint.getTarget();
        //用的最多 通知的签名
        Signature signature = joinPoint.getSignature();
        //代理的是哪一个方法
        System.out.println(signature.getName());
        //AOP代理类的名字
        System.out.println(signature.getDeclaringTypeName());
        //AOP代理类的类（class）信息
        signature.getDeclaringType();
        //获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        //如果要获取Session信息的话，可以这样写：
        //HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        Enumeration<String> enumeration = request.getParameterNames();
        Map<String,String> parameterMap = Maps.newHashMap();
        while (enumeration.hasMoreElements()){
            String parameter = enumeration.nextElement();
            parameterMap.put(parameter,request.getParameter(parameter));

            if ("access_token".equals(parameter)) {
                String accessToken = request.getParameter(parameter);
                final OauthToken token = BaseProvider.getBean(OAuthRSService.class).loadAccessTokenByTokenId(accessToken);
                if (token != null) {
                    try {
                        Object object = joinPoint.getThis();
                        Method method = signature.getDeclaringType().getMethod("setCurrentUserId", new Class[]{String.class});
                        Object[] args = new Object[]{token.getUsername()};
                        method.invoke(object, args);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        String str = JSON.toJSONString(parameterMap);
        if(obj.length > 0) {
            System.out.println("请求的参数信息为："+str);
        }


    }*/
    /*@Around("excudeService()")
    public Object twiceAsOld(ProceedingJoinPoint thisJoinPoint){
        System.err.println ("切面执行了。。。。");

        return null;
    }*/

}