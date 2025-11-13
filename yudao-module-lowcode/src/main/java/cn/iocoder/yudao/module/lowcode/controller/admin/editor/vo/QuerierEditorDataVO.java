package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 查询器数据 VO")
@Data
public class QuerierEditorDataVO {

    @Schema(description = "数据源ID")
    private Long dataSourceId;

    @Schema(description = "查询XML")
    private String queryXml;

    @Schema(description = "接口渠道")
    private List<Integer> channels;

    @Schema(description = "查询类型")
    private List<String> queryTypes;

    @Schema(description = "接口名称")
    private String apiName;

}
