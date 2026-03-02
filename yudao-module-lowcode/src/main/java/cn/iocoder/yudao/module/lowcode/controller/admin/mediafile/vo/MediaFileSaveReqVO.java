package cn.iocoder.yudao.module.lowcode.controller.admin.mediafile.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-媒体库文件新增/修改 Request VO")
@Data
public class MediaFileSaveReqVO {

    @Schema(description = "文件名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "文件名称不能为空")
    private String name;

    @Schema(description = "文件Url", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn")
    @NotEmpty(message = "文件Url不能为空")
    private String url;

    @Schema(description = "文件类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotEmpty(message = "文件类型不能为空")
    private String type;

    @Schema(description = "内容类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "内容类型不能为空")
    private String contentType;

    @Schema(description = "文件大小", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件大小不能为空")
    private Integer size;

}