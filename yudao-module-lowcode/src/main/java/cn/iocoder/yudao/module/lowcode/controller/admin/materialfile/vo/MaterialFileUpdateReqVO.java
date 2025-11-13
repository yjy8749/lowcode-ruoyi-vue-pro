package cn.iocoder.yudao.module.lowcode.controller.admin.materialfile.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-物料文件新增/修改 Request VO")
@Data
public class MaterialFileUpdateReqVO {

    @Schema(description = "主键", example = "14856")
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    private String name;

    @Schema(description = "描述", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @Schema(description = "是否私有", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isPrivate;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sort;

}