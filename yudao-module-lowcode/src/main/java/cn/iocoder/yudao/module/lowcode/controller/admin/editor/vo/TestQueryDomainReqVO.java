package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainParams;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 查询器-测试查询参数")
@Data
@ToString(callSuper = true)
@Builder
public class TestQueryDomainReqVO {

    @Schema(description = "数据源ID")
    @NotNull(message = "数据源ID不能为空")
    private Long dataSourceId;

    @Schema(description = "查询XML")
    @NotEmpty(message = "查询XML不能为空")
    private String queryXml;

    @Schema(description = "查询参数")
    private QueryDomainParams params;

}
