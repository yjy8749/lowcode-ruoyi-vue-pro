package cn.iocoder.yudao.module.lowcode.controller.admin.mediafile.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-媒体库文件批量删除 Request VO")
@Data
public class MediaFileBatchDeleteReqVO {

    @Schema(description = "删除文件id列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[]")
    @NotEmpty(message = "删除文件id列表不能为空")
    private List<Long> ids;

}