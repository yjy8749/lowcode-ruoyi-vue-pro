package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 查询器-接口删除")
@Data
@ToString(callSuper = true)
public class DeployApiDeleteReqVO {

    @Schema(description = "接口源文件ID", example = "723")
    @NotNull(message = "接口源文件ID不能为空")
    private Long sourceFileId;

    @Schema(description = "接口源文件版本号", example = "723")
    @NotNull(message = "接口源文件版本号不能为空")
    private Integer sourceFileVersion;

}