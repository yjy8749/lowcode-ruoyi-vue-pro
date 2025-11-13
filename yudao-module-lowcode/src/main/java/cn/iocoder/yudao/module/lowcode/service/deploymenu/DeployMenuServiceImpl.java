package cn.iocoder.yudao.module.lowcode.service.deploymenu;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.DeployMenuDeployReqVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.DeployMenuPageReqVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.GetSourceFileRefMenuReqVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.GetSourceFileRefMenuRespVO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.deploymenu.DeployMenuDO;
import cn.iocoder.yudao.module.lowcode.dal.mysql.deploymenu.DeployMenuMapper;
import cn.iocoder.yudao.module.lowcode.enums.DeployMenuStatus;
import cn.iocoder.yudao.module.system.controller.admin.permission.vo.menu.MenuRespVO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.MenuDO;
import cn.iocoder.yudao.module.system.dal.mysql.permission.MenuMapper;
import cn.iocoder.yudao.module.system.enums.permission.MenuTypeEnum;
import cn.iocoder.yudao.module.system.service.permission.MenuService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lowcode.enums.ErrorCodeConstants.DEPLOY_MENU_NOT_EXISTS;

/**
 * 低代码-部署菜单 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class DeployMenuServiceImpl implements DeployMenuService {

    @Resource
    private DeployMenuMapper deployMenuMapper;
    @Resource
    private MenuService menuService;

    @Resource
    private MenuMapper menuMapper;

    @Override
    public List<MenuDO> getSystemSubMenuList(Long parentId) {
        return menuMapper.selectList(MenuDO::getParentId, parentId);
    }


    @Override
    public Optional<Long> getAvailableRefMenuId(Long sourceFileId) {
        return Optional.ofNullable(this.deployMenuMapper.selectRefMenuId(sourceFileId, null, DeployMenuStatus.ONLINE.getValue()));
    }

    @Override
    public GetSourceFileRefMenuRespVO getSourceFileRefMenu(GetSourceFileRefMenuReqVO reqVO) {
        var resp = new GetSourceFileRefMenuRespVO();
        var refMenuIdOpt = getAvailableRefMenuId(reqVO.getSourceFileId());
        if (refMenuIdOpt.isPresent()) {
            var menuId = refMenuIdOpt.get();
            resp.setRefMenu(BeanUtils.toBean(this.menuService.getMenu(menuId), MenuRespVO.class));
            resp.setRefButtonList(BeanUtils.toBean(this.getSystemSubMenuList(menuId), MenuRespVO.class));
        }
        return resp;
    }

    @Override
    @Transactional
    public Boolean deployMenuDeploy(DeployMenuDeployReqVO reqVO) {
        var menu = reqVO.getSystemMenu();
        // 非 MENU 菜单原菜单创建或修改逻辑
        var refMenuIdOpt = getAvailableRefMenuId(reqVO.getSourceFileId());
        if (MenuTypeEnum.DIR.getType().equals(menu.getType()) || MenuTypeEnum.BUTTON.getType().equals(menu.getType())) {
            if (MenuTypeEnum.BUTTON.getType().equals(menu.getType())) {
                if (refMenuIdOpt.isEmpty()) {
                    throw exception(DEPLOY_MENU_NOT_EXISTS);
                }
                menu.setParentId(refMenuIdOpt.get());
            }
            if (menu.getId() == null) {
                this.menuService.createMenu(menu);
            } else {
                this.menuService.updateMenu(menu);
            }
            return true;
        }
        Long menuId;
        if (refMenuIdOpt.isPresent()) {
            menu.setId(refMenuIdOpt.get());
            menuId = refMenuIdOpt.get();
            this.menuService.updateMenu(menu);
        } else {
            menuId = this.menuService.createMenu(menu);
        }
        Long dbMenuId;
        var dbMenu = this.deployMenuMapper.selectFirstOne(DeployMenuDO::getSourceFileId, reqVO.getSourceFileId(),
                DeployMenuDO::getSourceFileVersion, reqVO.getSourceFileVersion());
        if (dbMenu == null) {
            var deployMenu = new DeployMenuDO();
            deployMenu.setMenuId(menuId);
            deployMenu.setSourceFileId(reqVO.getSourceFileId());
            deployMenu.setSourceFileVersion(reqVO.getSourceFileVersion());
            deployMenu.setMenuStatus(DeployMenuStatus.ONLINE.getValue());
            this.deployMenuMapper.insert(deployMenu);
            dbMenuId = deployMenu.getId();
        } else {
            var updateMenu = new DeployMenuDO();
            updateMenu.setId(dbMenu.getId());
            updateMenu.setMenuId(menuId);
            updateMenu.setSourceFileVersion(reqVO.getSourceFileVersion());
            updateMenu.setMenuStatus(DeployMenuStatus.ONLINE.getValue());
            this.deployMenuMapper.updateById(updateMenu);
            dbMenuId = updateMenu.getId();
        }
        this.deployMenuMapper.update(DeployMenuDO.builder().menuStatus(DeployMenuStatus.OFFLINE.getValue()).build(),
                new LambdaQueryWrapperX<DeployMenuDO>().ne(DeployMenuDO::getId, dbMenuId)
                        .eq(DeployMenuDO::getMenuStatus, DeployMenuStatus.ONLINE.getValue())
                        .eq(DeployMenuDO::getSourceFileId, reqVO.getSourceFileId()));
        return true;
    }

    @Override
    public PageResult<DeployMenuDO> deployMenuPage(DeployMenuPageReqVO pageReqVO) {
        return this.deployMenuMapper.selectPage(pageReqVO);
    }

    @Override
    public List<DeployMenuDO> deployMenuList(Long sourceFileId) {
        return this.deployMenuMapper.selectList(DeployMenuDO::getSourceFileId, sourceFileId);
    }

}