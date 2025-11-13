package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 查询器-生成数据表配置XML")
@Data
@ToString(callSuper = true)
@Builder
public class GenTableXmlReqVO {

    @Schema(description = "数据源ID", example = "10133")
    @NotNull(message = "数据源ID不能为空")
    private Long dataSourceId;

    @Schema(description = "表名称")
    @NotEmpty(message = "表名称不能为空")
    private String tableName;

}
