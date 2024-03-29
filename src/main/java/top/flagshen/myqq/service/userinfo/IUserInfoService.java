package top.flagshen.myqq.service.userinfo;

import com.baomidou.mybatisplus.extension.service.IService;
import top.flagshen.myqq.dao.userinfo.entity.UserInfoDO;
import top.flagshen.myqq.entity.userinfo.req.BindQQReq;
import top.flagshen.myqq.entity.userinfo.req.CreateUserReq;
import top.flagshen.myqq.entity.userinfo.resp.RankResp;
import top.flagshen.myqq.entity.userinfo.resp.UserInfoResp;
import top.flagshen.myqq.entity.userinfo.resp.WeiXinResp;

import java.util.List;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author 17460
 * @since 2022-08-14
 */
public interface IUserInfoService extends IService<UserInfoDO> {

    /**
     * 获取用户详情
     * @param qqNum
     * @return
     */
    UserInfoResp getUserDetail(String qqNum);

    /**
     * 探查，和详情功能一样，只不过探查的qq号是加密的
     * @param qqNum
     * @return
     */
    UserInfoResp getSearchUserDetail(String qqNum);

    /**
     *
     * @param code
     * @return
     */
    WeiXinResp getOpenId(String code);

    /**
     * 绑定qq
     * @param req
     */
    void bingQQ(BindQQReq req);

    /**
     * 解除绑定
     * @param qq
     */
    void unbind(String qq);

    /**
     * 创建用户信息
     * x系的五维  就是 x 7点 其他的在3-6之间随机
     * 总和等于25
     * 比如  7 3 6 6 3
     * @param userReq
     * @return
     */
    UserInfoResp createUser(CreateUserReq userReq);

    /**
     * 打工
     */
    String zhanbu(String qq, Integer isTest);

    /**
     * 打工
     */
    String work(String qq, Integer isTest);

    /**
     * 获取排行榜
     * @return
     */
    List<RankResp> getRank(String rankType);

    /**
     * 获取我的排名
     * @return
     */
    String getMyRank(String qqNum, String rankType);

    /**
     * 修改昵称
     * @param req
     */
    void updateName(BindQQReq req);
}
