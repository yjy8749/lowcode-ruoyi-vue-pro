package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.iocoder.yudao.module.system.controller.admin.permission.vo.menu.MenuRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-部署菜单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class DeployMenuRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "5950")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "菜单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "15177")
    @ExcelProperty("菜单ID")
    private Long menuId;

    @Schema(description = "菜单源文件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10657")
    @ExcelProperty("菜单源文件ID")
    private Long sourceFileId;

    @Schema(description = "菜单源文件版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("菜单源文件版本号")
    private Integer sourceFileVersion;

    @Schema(description = "菜单状态 1-已上线 2-已下线", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("菜单状态 1-已上线 2-已下线")
    private Integer menuStatus;

    @Schema(description = "关联系统菜单VO", requiredMode = Schema.RequiredMode.REQUIRED)
    private MenuRespVO systemMenu;

}