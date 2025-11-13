package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-部署接口 Response VO")
@Data
@ExcelIgnoreUnannotated
public class DeployApiRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "27319")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "接口编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("接口编码")
    private String apiCode;

    @Schema(description = "接口名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("接口名称")
    private String apiName;

    @Schema(description = "接口源文件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "723")
    @ExcelProperty("接口源文件ID")
    private Long sourceFileId;

    @Schema(description = "接口源文件版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("接口源文件版本号")
    private Integer sourceFileVersion;

    @Schema(description = "接口状态 0-待上线 1-已上线 2-已下线", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("接口状态 0-待上线 1-已上线 2-已下线")
    private Integer apiStatus;

}