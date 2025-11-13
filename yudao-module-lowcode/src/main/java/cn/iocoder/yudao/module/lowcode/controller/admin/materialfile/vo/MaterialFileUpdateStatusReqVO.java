package cn.iocoder.yudao.module.lowcode.controller.admin.materialfile.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-物料文件新增/修改 Request VO")
@Data
public class MaterialFileUpdateStatusReqVO {

    @Schema(description = "主键", example = "14856")
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "状态 0-禁用 1-正常 2-锁定 3-弃用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

}