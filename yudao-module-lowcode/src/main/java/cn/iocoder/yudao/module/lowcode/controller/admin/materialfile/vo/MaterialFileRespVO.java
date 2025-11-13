package cn.iocoder.yudao.module.lowcode.controller.admin.materialfile.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-物料文件 Response VO")
@Data
public class MaterialFileRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "14617")
    private Long id;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    private String name;

    @Schema(description = "描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "随便")
    private String description;

    @Schema(description = "来源 0-无 1-查询器 2-设计器 3-集成器", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer source;

    @Schema(description = "父ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "28383")
    private Long parentId;

    @Schema(description = "是否是文件", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isFile;

    @Schema(description = "是否私有", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isPrivate;

    @Schema(description = "状态 0-禁用 1-正常 2-锁定 3-弃用", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer status;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sort;

    @Schema(description = "来源文件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "6212")
    private Long sourceFileId;

    @Schema(description = "来源文件版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sourceFileVersion;

    @Schema(description = "创建人", requiredMode = Schema.RequiredMode.REQUIRED)
    private String creator;

}