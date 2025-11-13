package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 低代码-集成器配置 Response VO")
@Data
@ExcelIgnoreUnannotated
public class IntegratorConfigRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "2415")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "配置名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("配置名称")
    private String configName;

    @Schema(description = "配置类型 0-本机 1-远程", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("配置类型 0-本机 1-远程")
    private Integer configType;

    @Schema(description = "集成器入口", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("集成器入口")
    private String integrateEntry;

    @Schema(description = "集成校验KEY", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("集成校验KEY")
    private String integrateKey;

    @Schema(description = "备注", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("备注")
    private String comment;

}