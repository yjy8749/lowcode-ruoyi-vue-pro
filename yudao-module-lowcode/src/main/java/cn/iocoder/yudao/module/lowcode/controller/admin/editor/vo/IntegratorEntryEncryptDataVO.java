package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-集成器入口-加密数据")
@Data
public class IntegratorEntryEncryptDataVO {

    @Schema(description = "会话ID")
    @NotEmpty(message = "会话ID不能为空")
    private String sessionId;

    @Schema(description = "加密数据")
    @NotEmpty(message = "加密数据不能为空")
    private String encryptData;

}
