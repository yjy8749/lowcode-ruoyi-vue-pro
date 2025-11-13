package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-集成器入口-会话 VO")
@Data
public class IntegratorEntrySessionVO {

    @Schema(description = "会话ID")
    private String sessionId;

    @Schema(description = "会话加密KEY")
    private String sessionKey;

    @Schema(description = "会话有效期, 单位秒")
    private Long sessionTtl;

}
