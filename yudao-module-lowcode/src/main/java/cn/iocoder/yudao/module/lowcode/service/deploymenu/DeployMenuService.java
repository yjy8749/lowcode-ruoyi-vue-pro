package cn.iocoder.yudao.module.lowcode.service.deploymenu;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.DeployMenuDeployReqVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.DeployMenuPageReqVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.GetSourceFileRefMenuReqVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.GetSourceFileRefMenuRespVO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.deploymenu.DeployMenuDO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.MenuDO;

import java.util.List;
import java.util.Optional;

/**
 * 低代码-部署菜单 Service 接口
 *
 * @author 芋道源码
 */
public interface DeployMenuService {

    List<MenuDO> getSystemSubMenuList(Long parentId);
    Optional<Long> getAvailableRefMenuId(Long sourceFileId);
    GetSourceFileRefMenuRespVO getSourceFileRefMenu(GetSourceFileRefMenuReqVO reqVO);
    Boolean deployMenuDeploy(DeployMenuDeployReqVO deployReqVO);
    PageResult<DeployMenuDO> deployMenuPage(DeployMenuPageReqVO pageReqVO);
    List<DeployMenuDO> deployMenuList(Long sourceFileId);

}