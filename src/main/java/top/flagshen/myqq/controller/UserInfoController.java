package top.flagshen.myqq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.flagshen.myqq.common.constants.SystemConstants;
import top.flagshen.myqq.common.constants.YesOrNoConstants;
import top.flagshen.myqq.common.context.InvocationContext;
import top.flagshen.myqq.common.context.LocalInvocationContext;
import top.flagshen.myqq.entity.userinfo.req.BindQQReq;
import top.flagshen.myqq.entity.userinfo.req.CreateUserReq;
import top.flagshen.myqq.entity.userinfo.resp.RankResp;
import top.flagshen.myqq.entity.userinfo.resp.UserInfoResp;
import top.flagshen.myqq.entity.userinfo.resp.WeiXinResp;
import top.flagshen.myqq.service.userinfo.IUserInfoService;

import java.util.List;

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
        InvocationContext context = LocalInvocationContext.getContext();
        String qq = context.getQqNum();
        if (YesOrNoConstants.YES.equals(context.getIsTest())) {
            qq += SystemConstants.TEST;
        }
        return userInfoService.getUserDetail(qq);
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
        if (YesOrNoConstants.YES.equals(LocalInvocationContext.getContext().getIsTest())) {
            req.setOpenId(req.getOpenId() + SystemConstants.TEST);
        }
        userInfoService.bingQQ(req);
    }

    /**
     * 开号
     * @param req
     */
    @PutMapping("/create")
    public void bingQQ(@RequestBody CreateUserReq req) {
        InvocationContext context = LocalInvocationContext.getContext();
        if (YesOrNoConstants.YES.equals(context.getIsTest())) {
            req.setOpenId(req.getOpenId() + SystemConstants.TEST);
        }
        req.setIsTest(context.getIsTest());
        userInfoService.createUser(req);
    }

    /**
     * 微信小程序占卜
     * @return
     */
    @GetMapping("/zhanbu")
    public String zhanbu() {
        InvocationContext context = LocalInvocationContext.getContext();
        return userInfoService.zhanbu(context.getQqNum(), context.getIsTest());
    }

    /**
     * 微信小程序赚钱
     * @return
     */
    @GetMapping("/zhuanqian")
    public String zhuanqian() {
        InvocationContext context = LocalInvocationContext.getContext();
        return userInfoService.work(context.getQqNum(), context.getIsTest());
    }

    @GetMapping("/rank")
    public List<RankResp> getRank(String rankType) {
        return userInfoService.getRank(rankType);
    }

    @GetMapping("/myRank")
    public String getMyRank(String rankType) {
        InvocationContext context = LocalInvocationContext.getContext();
        return userInfoService.getMyRank(context.getQqNum(), rankType);
    }

    @PutMapping("/name")
    public void updateName(@RequestBody BindQQReq req) {
        InvocationContext context = LocalInvocationContext.getContext();
        req.setQqNum(context.getQqNum());
        userInfoService.updateName(req);
    }
}
