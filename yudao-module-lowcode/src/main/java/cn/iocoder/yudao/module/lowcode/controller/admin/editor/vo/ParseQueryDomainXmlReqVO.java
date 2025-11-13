package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 查询器-解析数据表配置XML")
@Data
@ToString(callSuper = true)
@Builder
public class ParseQueryDomainXmlReqVO {

    @Schema(description = "查询XML")
    @NotEmpty(message = "查询XML不能为空")
    private String queryXml;

}
