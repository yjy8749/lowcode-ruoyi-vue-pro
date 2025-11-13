package cn.iocoder.yudao.module.lowcode.controller.admin.materialfilelog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-物料文件操作日志 Response VO")
@Data
public class MaterialFileLogRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "9506")
    private Long id;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "文件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "22863")
    private Long fileId;

    @Schema(description = "来源 0-无 1-查询器 2-设计器 3-集成器", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer fileSource;

    @Schema(description = "数据类型 0-主数据", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer dataType;

    @Schema(description = "操作类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer opType;

    @Schema(description = "操作描述", requiredMode = Schema.RequiredMode.REQUIRED)
    private String opDesc;

    @Schema(description = "详细信息", requiredMode = Schema.RequiredMode.REQUIRED)
    private String opDetail;

    @Schema(description = "版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer opVersion;

    @Schema(description = "文件数据", requiredMode = Schema.RequiredMode.REQUIRED)
    private String opData;

}