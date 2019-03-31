package com.atguigu.gmall.admin.sms.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SmsQueryParam {

    @ApiModelProperty("优惠券名称")
    private String name;

    @ApiModelProperty("优惠券类型")
    private Integer type;
}
