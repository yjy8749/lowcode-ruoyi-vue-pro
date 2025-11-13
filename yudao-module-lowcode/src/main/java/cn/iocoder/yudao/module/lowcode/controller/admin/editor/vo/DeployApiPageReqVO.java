package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class DeployApiPageReqVO extends PageParam {

    @Schema(description = "接口源文件ID", example = "723")
    private Long sourceFileId;

    @Schema(description = "接口状态 0-待上线 1-已上线 2-已下线", example = "1")
    private Integer apiStatus;

}