package top.flagshen.myqq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.flagshen.myqq.common.context.LocalInvocationContext;
import top.flagshen.myqq.entity.userinfo.req.BindQQReq;
import top.flagshen.myqq.entity.userinfo.resp.UserInfoResp;
import top.flagshen.myqq.entity.userinfo.resp.WeiXinResp;
import top.flagshen.myqq.service.userinfo.IUserInfoService;

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
    @Deprecated
    @GetMapping("/detail/{qqNum}")
    public UserInfoResp getUserDetail(@PathVariable String qqNum) {
        return userInfoService.getUserDetail(qqNum);
    }

    /**
     * 查看详情
     * @return
     */
    @GetMapping("/detail")
    public UserInfoResp getDetail() {
        return userInfoService.getUserDetail(LocalInvocationContext.getContext().getQqNum());
    }

    /**
     * 探查，和详情功能一样，只不过探查的qq号是加密的
     * @param qqNum
     * @return
     */
    @GetMapping("/search/{qqNum}")
    public UserInfoResp getSearchUserDetail(@PathVariable String qqNum) {
        return userInfoService.getSearchUserDetail(qqNum);
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
