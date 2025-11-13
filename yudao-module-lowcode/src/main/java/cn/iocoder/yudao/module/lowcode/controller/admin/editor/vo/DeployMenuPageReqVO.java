package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 查询器-接口部署分页")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DeployMenuPageReqVO extends PageParam {

    @Schema(description = "菜单源文件ID")
    @NotNull(message = "菜单源文件ID不能为空")
    private Long sourceFileId;

}