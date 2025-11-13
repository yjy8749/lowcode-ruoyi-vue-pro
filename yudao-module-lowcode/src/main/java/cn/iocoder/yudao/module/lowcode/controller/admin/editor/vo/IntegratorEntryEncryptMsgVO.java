package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-集成器入口-加密消息基础类")
@Data
public class IntegratorEntryEncryptMsgVO {

    @Schema(description = "签名")
    private String sign;

    @Schema(description = "请求时间戳")
    private Long timestamp;

    @Schema(description = "Nonce随机数")
    private String nonce;

    @Schema(description = "JSON 数据")
    private String json;

}
