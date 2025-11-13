package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 设计器-获取部署菜单列表请求")
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class GetSourceFileRefMenuReqVO {

    @Schema(description = "菜单源文件ID")
    @NotNull(message = "菜单源文件ID不能为空")
    private Long sourceFileId;

}
