package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import cn.iocoder.yudao.module.lowcode.querier.xml.QueryDomain;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 查询器-数据表配置对象生成XML")
@Data
@ToString(callSuper = true)
@Builder
public class ToQueryDomainXmlReqVO {

    @Schema(description = "查询对象")
    @NotNull(message = "查询对象不能为空")
    private QueryDomain queryDomain;

}
