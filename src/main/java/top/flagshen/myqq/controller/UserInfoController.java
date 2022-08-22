package top.flagshen.myqq.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.flagshen.myqq.entity.userinfo.req.BindQQReq;
import top.flagshen.myqq.service.userinfo.IUserInfoService;
import top.flagshen.myqq.entity.userinfo.resp.UserInfoResp;
import top.flagshen.myqq.entity.userinfo.resp.WeiXinResp;

/**
 * @author dc
 * @date 2022/8/12 23:16
 */
@RequestMapping("/user")
@RestController
public class UserInfoController {

    @Autowired
    private IUserInfoService userInfoService;

    /**
     * 查看详情
     * @param qqNum
     * @return
     */
    @GetMapping("/detail/{qqNum}")
    public UserInfoResp getUserDetail(@PathVariable String qqNum) {
        if (StringUtils.isBlank(qqNum)) {
            return new UserInfoResp();
        }
        return userInfoService.getUserDetail(qqNum);
    }

    /**
     * 获取微信小程序openId
     * @param code
     * @return
     */
    @GetMapping("/weixin/login/{code}")
    public WeiXinResp getOpenId(@PathVariable String code) {
        return userInfoService.getOpenId(code);
    }

    /**
     * 根据约定好的qq密码绑定小程序
     * @param req
     */
    @PutMapping("/bind/qq")
    public void bingQQ(@RequestBody BindQQReq req) {
        userInfoService.bingQQ(req);
    }
}
