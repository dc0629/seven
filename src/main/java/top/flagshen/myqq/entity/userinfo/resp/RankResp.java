package top.flagshen.myqq.entity.userinfo.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import top.flagshen.myqq.util.EncryptionSerializer;

/**
 * <p>
 * 用户基本信息
 * </p>
 *
 * @author 17460
 * @since 2022-08-14
 */
@Data
public class RankResp {

    /**
     * 账号
     *
     */
    @JsonSerialize(using = EncryptionSerializer.class)
    private String qqNum;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 数量
     */
    private Integer number;

}
