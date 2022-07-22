package top.flagshen.myqq.common;

/**
 * @author 小棽
 * @date 2021/6/20 23:15
 */
public class TypeConstant {
    public static final int MSGTYPE_NODEFINE = -1;//消息类型_未定义	-1
    public static final int MSGTYPE_FRIEDN = 1;//消息类型_好友	1
    public static final int MSGTYPE_GROUP = 2;//消息类型_群	2
    public static final int MSGTYPE_DISCUSS = 3;//消息类型_讨论组	3
    public static final int MSGTYPE_GROUP_TEMP = 4;//消息类型_群临时会话	4
    public static final int MSGTYPE_DISCUSS_TEMP = 5;//消息类型_讨论组临时会话	5
    public static final int MSGTYPE_ONLINE_TEMP = 6;//消息类型_在线临时会话	6
    public static final int DEAL_IGNORE_CANCEL = 0;//处理_忽略或取消	0
    public static final int DEAL_AGREE_CONFIRM = 10;//处理_同意或确定	10
    public static final int DEAL_REFUSE = 20;//处理_拒绝	20
    public static final int DEAL_AGREE_SINGLE = 10;//处理_单向同意	10
    public static final int MSGTYPE_BE_ADD_FRIEND_SINGLE = 1000;//消息类型_被单向添加好友	1000
    public static final int MSGTYPE_BE_ADD_FRIEND = 1001;//消息类型_被请求添加好友	1001
    public static final int MSGTYPE_FRIEND_STATUS_CHANGE = 1002;//消息类型_好友状态改变	1002
    public static final int MSGTYPE_BE_DEL_FRIEND = 1003;//消息类型_被删除好友	1003
    public static final int MSGTYPE_FRIEND_SIGN_CHANGE = 1004;//消息类型_好友签名变更	1004
    public static final int MSGTYPE_POST_BE_COMMENT = 1005;//消息类型_说说被评论	1005
    public static final int MSGTYPE_FRIEND_INPUTING = 1006;//消息类型_好友正在输入	1006
    public static final int MSGTYPE_FRIEND_FRIEND_CHAT = 1007;//消息类型_好友今天首次发起会话	1007
    public static final int MSGTYPE_BE_SHIVER = 1008;//消息类型_被好友抖动	1008
    public static final int MSGTYPE_FRIEND_FILE_RECEIVE = 1009;//消息类型_好友文件接收	1009
    public static final int MSGTYPE_FRIEND_VIDEO_RECEIVE = 1010;//消息类型_好友视频接收	1010
    public static final int MSGTYPE_BE_INVITE_TO_GROUP = 2001;//消息类型_某人申请加入群	2001
    public static final int MSGTYPE_ONE_BE_INVITE_GROUP = 2002;//消息类型_某人被邀请加入群	2002
    public static final int MSGTYPE_HAS_BE_INVITE_GROUP = 20021;//消息类型_某人已被邀请入群	20021
    public static final int MSGTYPE_ME_BE_INVITE_GROUP = 2003;//消息类型_我被邀请加入群	2003
    public static final int MSGTYPE_ONE_BE_AGREE_GROUP = 2005;//消息类型_某人被批准加入了群	2005
    public static final int MSGTYPE_ONE_QUIT_GROUP = 2006;//消息类型_某人退出群	2006
    public static final int MSGTYPE_ONE_BE_QUIT = 2007;//消息类型_某人被管理移除群	2007
    public static final int MSGTYPE_GROUP_BE_QUI = 2008;//消息类型_某群被解散	2008
    public static final int MSGTYPE_ONE_BECOME_MANAGER = 2009;//消息类型_某人成为管理	2009
    public static final int MSGTYPE_ONE_BE_CANCEL_MANAGER = 2010;//消息类型_某人被取消管理	2010
    public static final int MSGTYPE_GROUPCARD_CHANGE = 2011;//消息类型_群名片变动	2011
    public static final int MSGTYPE_GROUPNICK_CHANGE = 2012;//消息类型_群名变动	2012
    public static final int MSGTYPE_GROUPNOTICE_CHANGE = 2013;//消息类型_群公告变动	2013
    public static final int MSGTYPE_ONE_BE_BAN = 2014;//消息类型_对象被禁言	2014
    public static final int MSGTYPE_ONE_QUIT_BE_BAN = 2015;//消息类型_对象被解除禁言	2015
    public static final int MSGTYPE_OPEN_GROUP_ALL_BAN = 2016;//消息类型_群管开启全群禁言	2016
    public static final int MSGTYPE_CLOSE_GROUP_ALL_BAN = 2017;//消息类型_群管关闭全群禁言	2017
    public static final int MSGTYPE_OPEN_ANON_CHAT = 2018;//消息类型_群管开启匿名聊天	2018
    public static final int MSGTYPE_CLOSE_ANON_CHAT = 2019;//消息类型_群管关闭匿名聊天	2019
    public static final int MSGTYPE_GROUP_RECALL_MSG = 2020;//消息类型_群撤回消息	2020
    public static final int MSGTYPE_GROUP_FILE_RECEIVE = 2021;//消息类型_群文件接收	2021
    public static final int MSGTYPE_GROUP_VIDEO_RECEIVE = 2022;//消息类型_群视频接收	2022
    public static final int MSGTYPE_FRIEND_VOICE_RECEIVE = 3001;//消息类型_好友语音接收	3001
    public static final int MSGTYPE_GROUP_VOICE_RECEIVE = 3002;//消息类型_群语音接收	3002
    public static final int MSGTYPE_FRAME_WILL_REBOOT = 10001;//消息类型_框架即将重启	10001
    public static final int MSGTYPE_FRAME_LOAD_OK = 10000;//消息类型_框架加载完成	10000
    public static final int MSGTYPE_FRAME_ADD_NEW_QQ = 11000;//消息类型_添加了一个新的帐号	11000
    public static final int MSGTYPE_FRAME_QQ_LOGIN_OK = 11001;//消息类型_QQ登录完成	11001
    public static final int MSGTYPE_FRAME_QQ_LEAVE_HAND = 11002;//消息类型_QQ被手动离线	11002
    public static final int MSGTYPE_FRAME_QQ_FORCE_LEAVE = 11003;//消息类型_QQ被强制离线	11003
    public static final int MSGTYPE_QQ_LONG_NORESPONSE = 11004;//消息类型_QQ长时间无响应或掉线	11004
    public static final int MSGTYPE_PLUGIN_LOAD = 12000;//消息类型_本插件载入	12000
    public static final int MSGTYPE_PLUGIN_BE_QI = 12001;//消息类型_插件被启用	12001
    public static final int MSGTYPE_PLUGIN_BE_BAN = 12002;//消息类型_插件被禁用	12002
    public static final int MSGTYPE_PLUGIN_BE_CLICK = 12003;//消息类型_插件被点击	12003
    public static final int MSGTYPE_RECEIVE_ACCOUNT = 80001;//消息类型_收到财付通转账	80001
    public static final int MSGDEAL_NOTDEAL = 0;//消息处理_不处理	0
    public static final int MSGDEAL_CONTINUE = 1;//消息处理_继续	1
    public static final int MSGDEAL_INTERCEPT = 2;//消息处理_拦截	2
    public static final int STATUS_ONLINE = 10;//状态_在线	10
    public static final int STATUS_LEAVE = 30;//状态_离开	30
    public static final int STATUS_BUSY = 50;//状态_忙碌	50
    public static final int STATUS_QME = 60;//状态_Q我吧	60
    public static final int STATUS_NOT_DISTURB = 70;//状态_勿扰	70
    public static final int STATUS_INVISIBLE = 40;//状态_隐身	40
}
