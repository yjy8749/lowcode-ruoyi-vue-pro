package cn.iocoder.yudao.module.lowcode.controller.admin.materialfile.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-物料文件 Request VO")
@Data
public class MaterialFileTransferReqVO {

    @Schema(description = "主键", example = "14856")
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "接收人", requiredMode = Schema.RequiredMode.REQUIRED, example = "21763")
    @NotNull(message = "接收人ID 不能为空")
    private Long receiverId;

    @Schema(description = "接收人名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "21763")
    @NotNull(message = "接收人名称 不能为空")
    private String receiverName;

    @Schema(description = "创建人", example = "王五")
    private String creator;

}