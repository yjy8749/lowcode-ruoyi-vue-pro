package cn.iocoder.yudao.module.lowcode.controller.admin.materialfile.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 获取低代码-物料文件 Request VO")
@Data
public class GetMaterialFileReqVO {

    @Schema(description = "ID", example = "10133")
    @NotNull(message = "ID不能为空")
    private Long id;

}
