package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-集成器-Pull请求")
@Data
public class IntegratorEntryPullReqVO {

    @Schema(description = "文件ID")
    @NotNull(message = "文件ID不能为空")
    private Long fileId;

    @Schema(description = "拉取文件版本List")
    private List<Integer> fileVersionList;

}
