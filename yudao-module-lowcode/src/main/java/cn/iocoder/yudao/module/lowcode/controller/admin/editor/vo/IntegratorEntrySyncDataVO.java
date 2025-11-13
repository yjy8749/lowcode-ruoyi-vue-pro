package cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo;

import cn.iocoder.yudao.module.lowcode.dal.dataobject.deployapi.DeployApiDO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.deploymenu.DeployMenuDO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfile.MaterialFileDO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfiledata.MaterialFileDataDO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.MenuDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author leo
 */
@Schema(description = "管理后台 - 低代码-集成器入口-拉取请求响应")
@Data
public class IntegratorEntrySyncDataVO {

    @Schema(description = "文件")
    private MaterialFileDO materialFileDO;

    @Schema(description = "文件数据List")
    private List<MaterialFileDataDO> materialFileDataDOList;

    @Schema(description = "部署的接口")
    private List<DeployApiDO> deployApiDOList;

    @Schema(description = "部署的菜单")
    private List<DeployMenuDO> deployMenuDOList;

    @Schema(description = "关联菜单")
    private MenuDO refMenu;

    @Schema(description = "关联按钮")
    private List<MenuDO> refButtonList;

}
