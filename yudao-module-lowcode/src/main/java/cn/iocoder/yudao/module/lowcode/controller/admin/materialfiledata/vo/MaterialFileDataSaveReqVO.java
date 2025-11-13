package cn.iocoder.yudao.module.lowcode.controller.admin.materialfiledata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-物料文件数据新增/修改 Request VO")
@Data
public class MaterialFileDataSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "14617")
    private Long id;
    
    @Schema(description = "文件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10928")
    @NotNull(message = "文件ID不能为空")
    private Long fileId;

    @Schema(description = "来源 0-无 1-查询器 2-设计器 3-集成器", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "来源不能为空")
    private Integer fileSource;

    @Schema(description = "数据类型 0-主数据", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "数据类型不能为空")
    private Integer dataType;

    @Schema(description = "版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "版本号不能为空")
    private Integer version;

    @Schema(description = "文件数据", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "文件数据不能为空")
    private String data;

}