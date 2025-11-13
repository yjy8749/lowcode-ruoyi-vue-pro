package cn.iocoder.yudao.module.lowcode.controller.admin.mediadir.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-媒体库目录新增/修改 Request VO")
@Data
public class MediaDirSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "15117")
    private Long id;

    @Schema(description = "目录名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "目录名称不能为空")
    private String name;

    @Schema(description = "父id", requiredMode = Schema.RequiredMode.REQUIRED, example = "26302")
    @NotNull(message = "父id不能为空")
    private Long parentId;

}