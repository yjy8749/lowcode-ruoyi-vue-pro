package cn.iocoder.yudao.module.lowcode.controller.admin.materialfile.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-物料文件新增/修改 Request VO")
@Data
public class MaterialFileSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "14617")
    private Long id;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "名称不能为空")
    private String name;

    @Schema(description = "描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "随便")
    private String description;

    @Schema(description = "父ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "28383")
    @NotNull(message = "父ID不能为空")
    private Long parentId;

    @Schema(description = "来源 0-无 1-查询器 2-设计器 3-集成器", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "来源不能为空")
    private Integer source;

    @Schema(description = "是否是文件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否是文件不能为空")
    private Boolean isFile;

    @Schema(description = "是否私有", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否私有不能为空")
    private Boolean isPrivate;

    @Schema(description = "状态 0-禁用 1-正常 2-锁定 3-弃用", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "状态 0-禁用 1-正常 2-锁定 3-弃用不能为空")
    private Integer status;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    private Integer sort;

    @Schema(description = "来源文件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "6212")
    private Long sourceFileId;

    @Schema(description = "来源文件版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sourceFileVersion;

    @Schema(description = "创建人", example = "王五")
    private String creator;

}