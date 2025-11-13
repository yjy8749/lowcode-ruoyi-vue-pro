package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import cn.iocoder.yudao.module.lowcode.controller.admin.materialfiledata.vo.MaterialFileDataSaveReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author leo
 */
@Schema(description = "查询器-接口部署")
@Data
public class DeployApiDeployReqVO extends MaterialFileDataSaveReqVO {

    @Schema(description = "是否同时下线其他接口")
    private Boolean isOfflineOther;

}
