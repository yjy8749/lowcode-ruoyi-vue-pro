package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-集成器-同步请求")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegratorEntrySyncRespVO {

    @Schema(description = "同步结果")
    private Boolean result;

}
