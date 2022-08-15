package top.flagshen.myqq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.flagshen.myqq.database.userinfo.service.IUserInfoService;
import top.flagshen.myqq.entity.resp.UserInfoResp;

/**
 * @author dc
 * @date 2022/8/12 23:16
 */
@RequestMapping("/user")
@RestController
public class UserInfoController {

    @Autowired
    private IUserInfoService userInfoService;

    @GetMapping("/detail/{qqNum}")
    public UserInfoResp getUserDetail(@PathVariable String qqNum) {
        return userInfoService.getUserDetail(qqNum);
    }
}
