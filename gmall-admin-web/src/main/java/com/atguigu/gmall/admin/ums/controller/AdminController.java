package com.atguigu.gmall.admin.ums.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.atguigu.gmall.admin.ums.utils.JwtTokenUtil;
import com.atguigu.gmall.admin.ums.vo.UmsAdminLoginParam;
import com.atguigu.gmall.to.CommonResult;
import com.atguigu.gmall.ums.entity.Admin;
import com.atguigu.gmall.ums.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Api(description = "后台用户管理模块")
@CrossOrigin
@EnableDubbo
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Reference
    private AdminService adminService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Value("${gmall.jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${gmall.jwt.tokenHead}")
    private String tokenHead;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Object adminLogin(@ApiParam(name = "umsAdminLoginParam",value = "用户",required = true)
            @RequestBody UmsAdminLoginParam umsAdminLoginParam){
        Admin admin = adminService.loginByUsername(umsAdminLoginParam.getUsername());

        //登陆成功生成token，此token携带基本用户信息，以后就不用去数据库了
        String token = jwtTokenUtil.generateToken(admin);

        if (token == null) {
            return new CommonResult().validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return new CommonResult().success(tokenMap);
    }


    @ApiOperation(value = "获取当前登录用户信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public Object getAdminInfo(HttpServletRequest request) {
        String oldToken = request.getHeader(tokenHeader);

        String token = oldToken.substring(tokenHead.length());
        String userName = jwtTokenUtil.getUserNameFromToken(token);
        System.out.println("需要去访问的用户名："+userName);

        //MyBatisPlus的service简单方法可以用，复杂的方法（参数是QueryWrapper、参数是IPage的都不要用）
        // Admin umsAdmin = adminService.getOne(new QueryWrapper<Admin>().eq("username",userName));
        //Admin umsAdmin = adminService.getById(1);
        Admin umsAdmin = adminService.getAdminByUsername(userName);


        Map<String, Object> data = new HashMap<>();
        data.put("username", umsAdmin.getUsername());
        data.put("roles", new String[]{"TEST"});
        data.put("icon", umsAdmin.getIcon());
        return new CommonResult().success(data);
    }
}
