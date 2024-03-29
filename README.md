# 小柒
基于[Simpler Robot](https://github.com/ForteScarlet/simpler-robot/tree/v2-dev)的机器人项目

## 机器人功能简介

### 设置更新提示模板，并且查看效果

![2.png](https://s2.loli.net/2022/07/23/GjnvxQDi5wYoZkt.png)

### 小说更新后自动群发提示

![3.png](https://s2.loli.net/2022/07/23/9ZqgGhNpdQ3jiUb.png)

### 对连续发图成员进行禁言，有对应的开关
### 敏感词撤回功能，有对应的开关
### 入群欢迎

### 游戏
#### /开号 xx系
##### 注册对应一个属性的账号
#### /打工
##### 随机开始一项工作，根据属性与熟练度获取对应银币，一天一次

#### 决斗游戏
##### 『/开启决斗』并输入［/决斗］并使用@功能给指定对象，指定对象在10分钟内回复［/接受］即可开始决斗，若不愿意参加游戏，则『/关闭决斗』将不会再受到打扰。
##### 决斗发起方：/决斗@某某某
##### 被决斗方：/接受或/拒绝
##### 若被决斗方十分钟内没有答复，默认为拒绝
##### 由小柒得出胜利结果（输赢与当天幸运值有关，建议先占卜再做决策）
##### 决斗结果为失败方，住院一天，当天胜场清零，当天无法再次决斗和接受决斗。
##### 获胜者胜场加1，并因打伤他人坐牢（禁言）5-10分钟，出狱后可继续决斗
##### 如果某人同时被多人发起决斗，除第一人的外，其余决斗不生效
##### 每天晚上12点小柒会公告当天胜场最多者为决斗冠军
##### 每周胜场前三可获得荣誉头衔，第一为决斗王，第二为决斗宗师，第三为决斗大师

##### 使用指令 /购买修复液 可以扣除10银币购买一包修复液，这个指令一天只能发一次，第二次发就没有反应了
##### 修复液保质期30天，过期就消失
##### 每日最多使用3份修复液，第四次决斗失败将会直接清空胜场住院
##### 拥有修复液，并且当日使用次数还没超过3次的玩家在决斗战败后不需要住院一天，自动消耗一袋修复液，等待10分钟后就可继续战斗，并且连胜次数不会被清空
##### 在决斗连胜到对应场次时也会有修复液掉落

### 微信小程序
#### 微信小程序搜索“游真信息查看”，绑定群里qq号，即可查看在qq群中创建的账号属性，累计银币，以及银币排行榜

### 接入[alibaba Sentinel](https://github.com/alibaba/Sentinel)进行流量控制
#### 虽然知道这个微信小程序累计只有300人登录过，但是还是增加了限流控制，防止被突然打爆