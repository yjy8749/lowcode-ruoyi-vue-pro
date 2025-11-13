package cn.iocoder.yudao.module.lowcode.controller.admin.materialfile.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-物料文件 Request VO")
@Data
public class MaterialFileMoveReqVO {

    @Schema(description = "文件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "文件ID 不能为空")
    private Long id;

    @Schema(description = "父ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "21763")
    @NotNull(message = "父ID 不能为空")
    private Long parentId;

    @Schema(description = "父名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "21763")
    @NotNull(message = "父名称 不能为空")
    private String parentName;

}