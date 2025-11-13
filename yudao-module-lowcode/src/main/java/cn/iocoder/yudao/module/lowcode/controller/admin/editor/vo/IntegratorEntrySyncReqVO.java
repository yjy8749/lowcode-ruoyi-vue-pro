package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-集成器-同步请求")
@Data
public class IntegratorEntrySyncReqVO extends IntegratorEntryPullReqVO {

    @Schema(description = "同步配置")
    @NotNull(message = "同步配置Id不能为空")
    private Long configId;

    @Schema(description = "是否拉取")
    private Boolean isPull;

}
