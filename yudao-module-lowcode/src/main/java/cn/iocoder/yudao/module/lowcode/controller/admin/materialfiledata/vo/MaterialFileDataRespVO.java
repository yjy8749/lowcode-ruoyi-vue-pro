package cn.iocoder.yudao.module.lowcode.controller.admin.materialfiledata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author leo
 */

@Schema(description = "管理后台 - 低代码-物料文件数据 Response VO")
@Data
public class MaterialFileDataRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "4417")
    private Long id;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "文件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10928")
    private Long fileId;

    @Schema(description = "来源 0-无 1-查询器 2-设计器 3-集成器", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer fileSource;

    @Schema(description = "数据类型 0-主数据", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer dataType;

    @Schema(description = "版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer version;

    @Schema(description = "文件数据", requiredMode = Schema.RequiredMode.REQUIRED)
    private String data;

}