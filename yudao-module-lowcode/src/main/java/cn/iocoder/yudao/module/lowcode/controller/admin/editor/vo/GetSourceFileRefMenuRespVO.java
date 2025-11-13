package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import cn.iocoder.yudao.module.system.controller.admin.permission.vo.menu.MenuRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 设计器-获取部署菜单列表响应")
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class GetSourceFileRefMenuRespVO {

    @Schema(description = "关联菜单")
    private MenuRespVO refMenu;

    @Schema(description = "关联按钮")
    private List<MenuRespVO> refButtonList;

}
