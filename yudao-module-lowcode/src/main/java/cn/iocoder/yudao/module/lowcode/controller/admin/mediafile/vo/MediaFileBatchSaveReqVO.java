package cn.iocoder.yudao.module.lowcode.controller.admin.mediafile.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-媒体库文件批量新增 Request VO")
@Data
public class MediaFileBatchSaveReqVO {

    @Schema(description = "目录id", requiredMode = Schema.RequiredMode.REQUIRED, example = "5597")
    @NotNull(message = "目录id不能为空")
    private Long dirId;

    @Schema(description = "目录id路径", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "目录id路径不能为空")
    private String dirIdPath;

    @Schema(description = "文件Url列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[]")
    @NotEmpty(message = "文件Url列表不能为空")
    private List<String> urls;

}