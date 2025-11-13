package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 查询器-获取接口查询配置Bean")
@Data
@ToString(callSuper = true)
@Builder
public class GetQueryDomainReqVO {

    @Schema(description = "接口名称")
    @NotEmpty(message = "接口名称不能为空")
    private String apiName;

    @Schema(description = "API Code")
    private String apiCode;

}
