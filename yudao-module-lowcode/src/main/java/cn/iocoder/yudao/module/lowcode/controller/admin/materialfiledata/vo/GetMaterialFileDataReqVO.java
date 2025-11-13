package cn.iocoder.yudao.module.lowcode.controller.admin.materialfiledata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-物料文件数据分页 Request VO")
@Data
@ToString(callSuper = true)
@Builder
public class GetMaterialFileDataReqVO {

    @Schema(description = "文件ID", example = "10133")
    @NotNull(message = "文件ID不能为空")
    private Long fileId;

    @Schema(description = "来源 0-无 1-查询器 2-设计器 3-集成器")
    @NotNull(message = "来源不能为空")
    private Integer fileSource;

    @Schema(description = "数据类型 0-主数据", example = "2")
    @NotNull(message = "数据类型不能为空")
    private Integer dataType;

    @Schema(description = "版本号")
    private Integer version;
}
