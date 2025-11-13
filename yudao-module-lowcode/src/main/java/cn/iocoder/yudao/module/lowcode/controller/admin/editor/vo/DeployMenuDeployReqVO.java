package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import cn.iocoder.yudao.module.system.controller.admin.permission.vo.menu.MenuSaveVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author leo
 */
@Schema(description = "设计器-菜单部署")
@Data
public class DeployMenuDeployReqVO {

    @Schema(description = "菜单源文件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10657")
    @NotNull(message = "菜单源文件ID不能为空")
    private Long sourceFileId;

    @Schema(description = "菜单源文件版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "菜单源文件版本号不能为空")
    private Integer sourceFileVersion;

    @Schema(description = "关联系统菜单VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "菜单信息不能为空")
    private MenuSaveVO systemMenu;
}
