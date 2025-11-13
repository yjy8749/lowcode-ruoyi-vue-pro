package cn.iocoder.yudao.module.lowcode.controller.admin.editor;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lowcode.controller.admin.BaseLowcodeController;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.*;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.deploymenu.DeployMenuDO;
import cn.iocoder.yudao.module.lowcode.service.deploymenu.DeployMenuService;
import cn.iocoder.yudao.module.system.controller.admin.permission.vo.menu.MenuRespVO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.MenuDO;
import cn.iocoder.yudao.module.system.service.permission.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * @author leo
 */
@Tag(name = "管理后台 - 低代码-设计器")
@RestController
@RequestMapping("/lowcode/designer-editor")
@Validated
public class DesignerEditorController extends BaseLowcodeController {

    @Resource
    private DeployMenuService deployMenuService;
    @Resource
    private MenuService menuService;

    @GetMapping("/source-file-ref-menu")
    @Operation(summary = "设计器-获取文件关联菜单")
    public CommonResult<GetSourceFileRefMenuRespVO> getSourceFileRefMenu(@Valid GetSourceFileRefMenuReqVO reqVO) {
        checkSourceAndOperator(EDITOR, reqVO.getSourceFileId());
        return success(this.deployMenuService.getSourceFileRefMenu(reqVO));
    }

    @PostMapping("/deploy-menu/deploy")
    @Operation(summary = "设计器-菜单部署")
    public CommonResult<Boolean> deployMenuDeploy(@Valid @RequestBody DeployMenuDeployReqVO deployReqVO) {
        checkSourceAndOperator(EDITOR, deployReqVO.getSourceFileId());
        return success(this.deployMenuService.deployMenuDeploy(deployReqVO));
    }

    @GetMapping("/deploy-menu/page")
    @Operation(summary = "设计器-菜单部署分页")
    public CommonResult<PageResult<DeployMenuRespVO>> deployMenuPage(@Valid DeployMenuPageReqVO pageReqVO) {
        checkSourceAndOperator(EDITOR, pageReqVO.getSourceFileId());
        PageResult<DeployMenuDO> pageResult = this.deployMenuService.deployMenuPage(pageReqVO);
        var pageResultResp = BeanUtils.toBean(pageResult, DeployMenuRespVO.class);
        if (CollectionUtil.isNotEmpty(pageResultResp.getList())) {
            var menuIds = pageResultResp.getList().stream().map(DeployMenuRespVO::getMenuId).toList();
            var menuMap = menuService.getMenuList(menuIds).stream().collect(Collectors.toMap(MenuDO::getId, v -> v));
            for (DeployMenuRespVO deployMenuRespVO : pageResultResp.getList()) {
                deployMenuRespVO.setSystemMenu(BeanUtils.toBean(menuMap.get(deployMenuRespVO.getMenuId()), MenuRespVO.class));
            }
        }
        return success(pageResultResp);
    }

}
