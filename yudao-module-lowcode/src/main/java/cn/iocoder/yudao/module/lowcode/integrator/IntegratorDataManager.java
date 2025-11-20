package cn.iocoder.yudao.module.lowcode.integrator;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.IntegratorEntryPullReqVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.IntegratorEntrySyncDataVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.materialfiledata.vo.GetMaterialFileDataReqVO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.deployapi.DeployApiDO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.deploymenu.DeployMenuDO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfile.MaterialFileDO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfiledata.MaterialFileDataDO;
import cn.iocoder.yudao.module.lowcode.dal.mysql.deployapi.DeployApiMapper;
import cn.iocoder.yudao.module.lowcode.dal.mysql.deploymenu.DeployMenuMapper;
import cn.iocoder.yudao.module.lowcode.dal.mysql.materialfile.MaterialFileMapper;
import cn.iocoder.yudao.module.lowcode.dal.mysql.materialfiledata.MaterialFileDataMapper;
import cn.iocoder.yudao.module.lowcode.enums.MaterialFileDataType;
import cn.iocoder.yudao.module.lowcode.enums.MaterialFileSource;
import cn.iocoder.yudao.module.lowcode.service.deployapi.DeployApiService;
import cn.iocoder.yudao.module.lowcode.service.deploymenu.DeployMenuService;
import cn.iocoder.yudao.module.lowcode.service.materialfile.MaterialFileService;
import cn.iocoder.yudao.module.lowcode.service.materialfiledata.MaterialFileDataService;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.MenuDO;
import cn.iocoder.yudao.module.system.dal.mysql.permission.MenuMapper;
import cn.iocoder.yudao.module.system.service.permission.MenuService;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lowcode.enums.ErrorCodeConstants.INTEGRATOR_FILE_SYNC_VERSION_ERROR;

/**
 * @author leo
 */
@Slf4j
@Component
public class IntegratorDataManager {

    @Resource
    private MaterialFileService materialFileService;
    @Resource
    private MaterialFileDataService materialFileDataService;
    @Resource
    private DeployApiService deployApiService;
    @Resource
    private DeployMenuService deployMenuService;
    @Resource
    private MenuService menuService;
    @Resource
    private MaterialFileMapper materialFileMapper;
    @Resource
    private MaterialFileDataMapper materialFileDataMapper;
    @Resource
    private DeployApiMapper deployApiMapper;
    @Resource
    private DeployMenuMapper deployMenuMapper;
    @Resource
    private MenuMapper menuMapper;

    @Transactional(readOnly = true)
    public IntegratorEntrySyncDataVO integratorEntryPull(IntegratorEntryPullReqVO reqVO) {
        Assert.notNull(reqVO.getFileId());
        var fileId = reqVO.getFileId();
        var respVO = new IntegratorEntrySyncDataVO();
        // 文件数据
        respVO.setMaterialFileDO(this.materialFileService.getMaterialFileById(fileId));
        // 文件夹数据
        var parentDirList = new ArrayList<MaterialFileDO>();
        var parentId = respVO.getMaterialFileDO().getParentId();
        while (parentId > 0) {
            var parentDir = this.materialFileService.getMaterialFileById(parentId);
            parentDirList.add(parentDir);
            parentId = parentDir.getParentId();
        }
        respVO.setParentDirList(parentDirList);
        // 文件数据
        var fileSource = respVO.getMaterialFileDO().getSource();
        if (MaterialFileSource.QUERIER.getValue().equals(fileSource)) {
            //查询器文件
            respVO.setDeployApiDOList(this.deployApiService.deployApiList(fileId));
        } else if (MaterialFileSource.DESIGNER.getValue().equals(fileSource)) {
            //设计器文件
            respVO.setDeployMenuDOList(this.deployMenuService.deployMenuList(fileId));
            //关联菜单
            var refMenuIdOpt = this.deployMenuService.getAvailableRefMenuId(fileId);
            if (refMenuIdOpt.isPresent()) {
                respVO.setRefMenu(this.menuService.getMenu(refMenuIdOpt.get()));
                respVO.setRefButtonList(this.deployMenuService.getSystemSubMenuList(refMenuIdOpt.get()));
            }
        }
        var fileVersionSet = new HashSet<Integer>();
        if (CollectionUtil.isNotEmpty(respVO.getDeployApiDOList())) {
            for (DeployApiDO deployApiDO : respVO.getDeployApiDOList()) {
                fileVersionSet.add(deployApiDO.getSourceFileVersion());
            }
        }
        if (CollectionUtil.isNotEmpty(respVO.getDeployMenuDOList())) {
            for (DeployMenuDO deployMenuDO : respVO.getDeployMenuDOList()) {
                fileVersionSet.add(deployMenuDO.getSourceFileVersion());
            }
        }
        if (CollectionUtil.isEmpty(fileVersionSet)) {
            var latestVersionData = this.materialFileDataService.getMaterialFileData(GetMaterialFileDataReqVO.builder()
                    .fileId(fileId).fileSource(fileSource).dataType(MaterialFileDataType.MAIN.getValue()).build());
            fileVersionSet.add(latestVersionData.getVersion());
        }
        if (CollectionUtil.isNotEmpty(reqVO.getFileVersionList())) {
            fileVersionSet.addAll(reqVO.getFileVersionList());
        }
        if (CollectionUtil.isNotEmpty(fileVersionSet)) {
            respVO.setMaterialFileDataDOList(this.materialFileDataService.getMaterialFileDataList(fileId, fileVersionSet));
            if(fileVersionSet.size() !=  respVO.getMaterialFileDataDOList().size()) {
                var allGetVersionSet = respVO.getMaterialFileDataDOList().stream().map(MaterialFileDataDO::getVersion).collect(Collectors.toSet());
                log.error("同步文件缺少指定版本数据, fileId: {}, 同步版本:{}, 缺少版本: {}",
                        fileId, JSON.toJSONString(fileVersionSet), JSON.toJSONString(fileVersionSet.stream().filter(i -> !allGetVersionSet.contains(i)).toList()));
                throw exception(INTEGRATOR_FILE_SYNC_VERSION_ERROR);
            }
        }
        return respVO;
    }

    @Transactional(rollbackFor = Exception.class)
    public void integratorEntrySync(IntegratorEntrySyncDataVO syncDataVO) {
        Assert.notNull(syncDataVO.getMaterialFileDO());
        // 1. 同步 MaterialFileDO (文件信息)
        var materialFileDO = syncDataVO.getMaterialFileDO();
        {
            var exist = materialFileMapper.selectById(materialFileDO.getId());
            if (exist != null) {
                materialFileDO.setName(exist.getName());
                materialFileDO.setStatus(exist.getStatus());
                materialFileMapper.updateById(materialFileDO);
            } else {
                materialFileMapper.insert(materialFileDO);
            }
        }
        // 2. 同步 ParentDirList (文件夹信息)
        var parentDirList = syncDataVO.getParentDirList();
        {
            if(CollectionUtil.isNotEmpty(parentDirList)){
                for (MaterialFileDO fileDO : parentDirList) {
                    var exist = materialFileMapper.selectById(fileDO.getId());
                    if (exist != null) {
                        fileDO.setName(exist.getName());
                        fileDO.setStatus(exist.getStatus());
                        materialFileMapper.updateById(fileDO);
                    } else {
                        materialFileMapper.insert(fileDO);
                    }
                }
            }
        }
        // 3. 同步 MaterialFileDataDOList (文件数据)
        if (CollectionUtil.isNotEmpty(syncDataVO.getMaterialFileDataDOList())) {
            for (var data : syncDataVO.getMaterialFileDataDOList()) {
                if (materialFileDataMapper.selectById(data.getId()) != null) {
                    materialFileDataMapper.updateById(data);
                } else {
                    materialFileDataMapper.insert(data);
                }
            }
        }
        // 4. 同步 DeployApiDOList (部署接口)
        if (CollectionUtil.isNotEmpty(syncDataVO.getDeployApiDOList())) {
            for (DeployApiDO api : syncDataVO.getDeployApiDOList()) {
                var exist = deployApiMapper.selectById(api.getId());
                if (exist != null) {
                    api.setDeleted(exist.getDeleted());
                    api.setApiStatus(exist.getApiStatus());
                    deployApiMapper.updateById(api);
                } else {
                    deployApiMapper.insert(api);
                }
            }
        }
        // 5. 同步 RefMenu (关联菜单)
        var fileId = syncDataVO.getMaterialFileDO().getId();
        var refMenuIdOpt = this.deployMenuService.getAvailableRefMenuId(fileId);
        if (syncDataVO.getRefMenu() != null) {
            var menu = syncDataVO.getRefMenu();
            if (refMenuIdOpt.isPresent()) {
                var exist = this.menuService.getMenu(refMenuIdOpt.get());
                if (exist != null) {
                    menu.setId(exist.getId());
                    menu.setDeleted(exist.getDeleted());
                    menu.setParentId(exist.getParentId());
                    menu.setStatus(exist.getStatus());
                    this.menuMapper.updateById(menu);
                } else {
                    menu.setId(null);
                    this.menuMapper.insert(menu);
                }
            }
            refMenuIdOpt = Optional.ofNullable(menu.getId());
        }
        // 6. 同步 RefButton (关联按钮)
        if (CollectionUtil.isNotEmpty(syncDataVO.getRefButtonList())) {
            var refButtonNameMap = new HashMap<String, MenuDO>();
            if (refMenuIdOpt.isPresent()) {
                var refButtionList = this.deployMenuService.getSystemSubMenuList(refMenuIdOpt.get());
                for (var button : refButtionList) {
                    refButtonNameMap.put(button.getName(), button);
                }
            }
            for (MenuDO button : syncDataVO.getRefButtonList()) {
                var exist = refButtonNameMap.get(button.getName());
                if (exist != null) {
                    button.setId(exist.getId());
                    button.setDeleted(exist.getDeleted());
                    button.setStatus(exist.getStatus());
                    button.setParentId(refMenuIdOpt.orElse(button.getParentId()));
                    this.menuMapper.updateById(button);
                } else {
                    button.setId(null);
                    button.setParentId(refMenuIdOpt.orElse(button.getParentId()));
                    this.menuMapper.insert(button);
                }
            }
        }
        // 7. 同步 DeployMenuDOList (部署菜单)
        if (CollectionUtil.isNotEmpty(syncDataVO.getDeployMenuDOList())) {
            for (DeployMenuDO menu : syncDataVO.getDeployMenuDOList()) {
                menu.setMenuId(refMenuIdOpt.orElse(menu.getMenuId()));
                var exist = deployMenuMapper.selectById(menu.getId());
                if (exist != null) {
                    menu.setMenuStatus(exist.getMenuStatus());
                    deployMenuMapper.updateById(menu);
                } else {
                    deployMenuMapper.insert(menu);
                }
            }
        }
    }

}
