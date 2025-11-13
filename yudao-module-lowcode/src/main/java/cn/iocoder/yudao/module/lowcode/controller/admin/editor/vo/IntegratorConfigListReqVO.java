package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-集成器配置List Request VO")
@Data
@EqualsAndHashCode
@ToString(callSuper = true)
public class IntegratorConfigListReqVO {

    @Schema(description = "配置名称", example = "张三")
    private String configName;

    @Schema(description = "配置类型 0-本机 1-远程", example = "1")
    private Integer configType;

    @Schema(description = "集成器入口")
    private String integrateEntry;

    @Schema(description = "集成校验KEY")
    private String integrateKey;

    @Schema(description = "Id列表")
    private List<Integer> ids;

}