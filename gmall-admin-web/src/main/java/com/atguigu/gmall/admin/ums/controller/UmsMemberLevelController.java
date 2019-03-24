package com.atguigu.gmall.admin.ums.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.to.CommonResult;
import com.atguigu.gmall.ums.entity.MemberLevel;
import com.atguigu.gmall.ums.service.MemberLevelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(description = "会员等级管理")
@CrossOrigin
@RestController
@RequestMapping("/memberLevel")
public class UmsMemberLevelController {

    @Reference
    private MemberLevelService memberLevelService;

    @ApiOperation("查询所有会员等级")
    @GetMapping("/list")
    public Object getMemberLevelList(@RequestParam(value = "defaultStatus",defaultValue = "0")String defaultStatus){

        List<MemberLevel> memberLevels = memberLevelService.getMemberLevelByStatus(defaultStatus);
        return new CommonResult().success(memberLevels);
    }
}
