package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-集成器配置新增/修改 Request VO")
@Data
public class IntegratorConfigSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "2415")
    private Long id;

    @Schema(description = "配置名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "配置名称不能为空")
    private String configName;

    @Schema(description = "集成器入口", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "集成器入口不能为空")
    private String integrateEntry;

    @Schema(description = "集成校验KEY", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "集成校验KEY不能为空")
    private String integrateKey;

    @Schema(description = "备注", requiredMode = Schema.RequiredMode.REQUIRED)
    private String comment;

}