package cn.iocoder.yudao.module.lowcode.service.materialfile;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.lowcode.controller.admin.materialfile.vo.*;
import cn.iocoder.yudao.module.lowcode.controller.admin.materialfiledata.vo.GetMaterialFileDataReqVO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfile.MaterialFileDO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfiledata.MaterialFileDataDO;
import cn.iocoder.yudao.module.lowcode.dal.mysql.materialfile.MaterialFileMapper;
import cn.iocoder.yudao.module.lowcode.dal.mysql.materialfiledata.MaterialFileDataMapper;
import cn.iocoder.yudao.module.lowcode.enums.MaterialFileDataType;
import cn.iocoder.yudao.module.lowcode.enums.MaterialFileLogOpType;
import cn.iocoder.yudao.module.lowcode.enums.MaterialFileStatus;
import cn.iocoder.yudao.module.lowcode.service.materialfiledata.MaterialFileDataService;
import cn.iocoder.yudao.module.lowcode.service.materialfilelog.MaterialFileLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lowcode.enums.ErrorCodeConstants.*;

/**
 * 低代码-物料文件 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MaterialFileServiceImpl implements MaterialFileService {

    @Resource
    private MaterialFileMapper materialFileMapper;
    @Resource
    private MaterialFileLogService materialFileLogService;
    @Resource
    private MaterialFileDataService materialFileDataService;
    @Resource
    private MaterialFileDataMapper materialFileDataMapper;

    public void collectAllChild(Long parentId, List<MaterialFileDO> results) {
        List<MaterialFileDO> children = this.materialFileMapper.selectList(new LambdaQueryWrapperX<MaterialFileDO>().eqIfPresent(MaterialFileDO::getParentId, parentId));
        if (CollectionUtil.isNotEmpty(children)) {
            for (MaterialFileDO child : children) {
                results.add(child);
                collectAllChild(child.getId(), results);
            }
        }
    }

    public List<MaterialFileDO> getAllChild(Long parentId, boolean includeParent) {
        List<MaterialFileDO> results = new ArrayList<>();
        if (includeParent) {
            results.add(this.materialFileMapper.selectById(parentId));
        }
        collectAllChild(parentId, results);
        return results;
    }

    @Override
    public PageResult<MaterialFileDO> getMaterialFilePage(MaterialFilePageReqVO reqVO) {
        return materialFileMapper.selectPage(reqVO, new LambdaQueryWrapperX<MaterialFileDO>().betweenIfPresent(MaterialFileDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(MaterialFileDO::getParentId, reqVO.getParentId())
                .likeIfPresent(MaterialFileDO::getName, reqVO.getName())
                .eqIfPresent(MaterialFileDO::getSource, reqVO.getSource())
                .eqIfPresent(MaterialFileDO::getIsFile, reqVO.getIsFile())
                .eqIfPresent(MaterialFileDO::getStatus, reqVO.getStatus())
                .inIfPresent(MaterialFileDO::getId, reqVO.getIds())
                .and((wrapper) -> {
                    if (Boolean.TRUE.equals(reqVO.getIntegratorSelectable())) {
                        wrapper.isNotNull(MaterialFileDO::getCreator);
                    } else {
                        wrapper.eq(MaterialFileDO::getCreator, reqVO.getCreator()).or().eq(MaterialFileDO::getIsPrivate, Boolean.FALSE);
                    }
                }).orderByAsc(MaterialFileDO::getIsFile).orderByAsc(MaterialFileDO::getSort).orderByDesc(MaterialFileDO::getId));
    }

    @Override
    public MaterialFileDO getMaterialFileById(Long id) {
        return validateIdAndGet(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMaterialFile(MaterialFileSaveReqVO createReqVO) {
        MaterialFileDO materialFile = BeanUtils.toBean(createReqVO, MaterialFileDO.class);
        // 校验
        validateNameNotExists(createReqVO.getCreator(), createReqVO.getParentId(), null, createReqVO.getName(), createReqVO.getSource(), createReqVO.getIsFile());
        // 插入
        materialFileMapper.insert(materialFile);
        // 记录日志
        materialFileLogService.saveFileLog(materialFile, MaterialFileLogOpType.CREATE);
        // 返回
        return materialFile.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMaterialFile(MaterialFileUpdateReqVO updateReqVO) {
        var fileDO = validateIdAndGet(updateReqVO.getId());
        // 校验 文件是否可更新
        validateFileCanUpdate(fileDO);
        // 校验 名称是否重复
        validateNameNotExists(fileDO.getCreator(), fileDO.getParentId(), fileDO.getId(), updateReqVO.getName(), fileDO.getSource(), fileDO.getIsFile());
        // 更新
        MaterialFileDO updateObj = BeanUtils.toBean(updateReqVO, MaterialFileDO.class);
        materialFileMapper.updateById(updateObj);
        // 记录日志
        List<String> opDetails = new ArrayList<>();
        if (StrUtil.isNotEmpty(updateReqVO.getName()) && ObjUtil.notEqual(fileDO.getName(), updateReqVO.getName())) {
            opDetails.add("名称修改为" + updateReqVO.getName());
        }
        if (StrUtil.isNotEmpty(updateReqVO.getDescription()) && ObjUtil.notEqual(fileDO.getDescription(), updateReqVO.getDescription())) {
            opDetails.add("描述修改为" + updateReqVO.getDescription());
        }
        if (updateReqVO.getIsPrivate() != null && ObjUtil.notEqual(fileDO.getIsPrivate(), updateReqVO.getIsPrivate())) {
            opDetails.add("设置为" + (updateReqVO.getIsPrivate() ? "私有" : "公有"));
        }
        if (updateReqVO.getSort() != null && ObjUtil.notEqual(fileDO.getSort(), updateReqVO.getSort())) {
            opDetails.add("排序修改为" + updateReqVO.getSort());
        }
        if (CollectionUtil.isNotEmpty(opDetails)) {
            materialFileLogService.saveFileLog(fileDO, MaterialFileLogOpType.UPDATE, CollectionUtil.join(opDetails, "\n"));
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMaterialFileStatus(MaterialFileUpdateStatusReqVO updateReqVO) {
        var fileDO = validateIdAndGet(updateReqVO.getId());
        if (ObjUtil.notEqual(updateReqVO.getStatus(), MaterialFileStatus.NORMAL.getValue())) {
            // 校验 文件是否可更新
            validateFileCanUpdate(fileDO);
        }
        // 更新
        MaterialFileDO updateObj = BeanUtils.toBean(updateReqVO, MaterialFileDO.class);
        materialFileMapper.updateById(updateObj);
        // 记录日志
        String opDetail = "";
        if (ObjUtil.notEqual(fileDO.getStatus(), updateReqVO.getStatus())) {
            opDetail += "状态修改为" + MaterialFileStatus.valueOf(updateReqVO.getStatus()).getName();
        }
        if (StrUtil.isNotEmpty(opDetail)) {
            materialFileLogService.saveFileLog(fileDO, MaterialFileLogOpType.UPDATE, opDetail);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMaterialFile(MaterialFileDeleteReqVO deleteReqVO) {
        var fileDO = validateIdAndGet(deleteReqVO.getId());
        // 校验 文件是否可更新
        validateFileCanUpdate(fileDO);
        // 删除
        var allIds = getAllChild(fileDO.getId(), true).stream().map(MaterialFileDO::getId).toList();
        materialFileMapper.delete(new LambdaQueryWrapperX<MaterialFileDO>().inIfPresent(MaterialFileDO::getId, allIds));
        // 记录日志
        materialFileLogService.saveFileLog(fileDO, MaterialFileLogOpType.DELETE);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean transferMaterialFile(MaterialFileTransferReqVO transferReqVO) {
        var receiverId = String.valueOf(transferReqVO.getReceiverId());
        var fileDO = validateIdAndGet(transferReqVO.getId());
        // 校验 文件是否可更新
        validateFileCanUpdate(fileDO);
        // 校验 名称是否重复
        validateNameNotExists(receiverId, fileDO.getParentId(), fileDO.getId(), fileDO.getName(), fileDO.getSource(), fileDO.getIsFile());
        // 更新 创建人
        var allIds = getAllChild(fileDO.getId(), true).stream().map(MaterialFileDO::getId).toList();
        materialFileMapper.transferMaterialFile(allIds, transferReqVO.getCreator(), receiverId);
        // 记录日志
        String opDetail = "转移所有权给" + transferReqVO.getReceiverName();
        materialFileLogService.saveFileLog(fileDO, MaterialFileLogOpType.TRANSFER, opDetail);
        return true;
    }

    @Override
    public boolean moveMaterialFile(MaterialFileMoveReqVO moveReqVO) {
        var fileDO = validateIdAndGet(moveReqVO.getId());
        // 校验 文件是否可更新
        validateFileCanUpdate(fileDO);
        // 校验 名称是否重复
        validateNameNotExists(fileDO.getCreator(), moveReqVO.getParentId(), fileDO.getId(), fileDO.getName(), fileDO.getSource(), fileDO.getIsFile());
        // 更新 parentId
        if (!ObjUtil.equal(fileDO.getParentId(), moveReqVO.getParentId())) {
            materialFileMapper.updateById(MaterialFileDO.builder().id(fileDO.getId()).parentId(moveReqVO.getParentId()).build());
            // 记录日志
            String opDetail = "移动位置到" + moveReqVO.getParentName();
            materialFileLogService.saveFileLog(fileDO, MaterialFileLogOpType.MOVE, opDetail);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MaterialFileDO copyMaterialFile(MaterialFileCopyReqVO copyReqVO) {
        var fileDO = validateIdAndGet(copyReqVO.getId());
        // 校验 文件是否可更新
        validateFileCanUpdate(fileDO);
        // 复制文件数据
        return copyMaterialFile(fileDO, fileDO.getParentId());
    }

    private MaterialFileDO copyMaterialFile(MaterialFileDO fileDO, Long parentId) {
        // 复制文件
        var toFile = BeanUtils.toBean(fileDO, MaterialFileDO.class);
        toFile.setId(null);
        toFile.setParentId(parentId);
        // 生成复制文件名称
        var isCopyNameExist = true;
        while (isCopyNameExist) {
            isCopyNameExist = isNameExists(toFile.getCreator(), toFile.getParentId(), toFile.getId(), toFile.getName(), toFile.getSource(), toFile.getIsFile());
            if(isCopyNameExist) {
                toFile.setName(toFile.getName() + "（副本）");
            }
        }
        this.materialFileMapper.insert(toFile);
        materialFileLogService.saveFileLog(toFile, MaterialFileLogOpType.COPY);
        // 复制文件数据
        if (Boolean.TRUE.equals(toFile.getIsFile())) {
            var latestVersionData = this.materialFileDataService.getMaterialFileData(GetMaterialFileDataReqVO.builder()
                    .fileId(fileDO.getId()).fileSource(fileDO.getSource()).dataType(MaterialFileDataType.MAIN.getValue()).build());
            var toFileData = BeanUtils.toBean(latestVersionData, MaterialFileDataDO.class);
            toFileData.setId(null);
            toFileData.setFileId(toFile.getId());
            toFileData.setVersion(0);
            this.materialFileDataMapper.insert(toFileData);
        }
        // 复制子文件
        if (Boolean.FALSE.equals(toFile.getIsFile())) {
            List<MaterialFileDO> children = this.materialFileMapper.selectList(new LambdaQueryWrapperX<MaterialFileDO>().eqIfPresent(MaterialFileDO::getParentId, fileDO.getId()));
            if (CollectionUtil.isNotEmpty(children)) {
                for (MaterialFileDO child : children) {
                    copyMaterialFile(child,toFile.getId());
                }
            }
        }
        return toFile;
    }


    // 校验 ID 是否存在并返回物料文件
    private MaterialFileDO validateIdAndGet(Long id) {
        Assert.notNull(id);
        var fileDO = materialFileMapper.selectById(id);
        if (ObjUtil.isNull(fileDO)) {
            throw exception(MATERIAL_FILE_NOT_EXISTS);
        }
        return fileDO;
    }

    // 校验 物料文件 是否已未锁定
    private void validateFileCanUpdate(MaterialFileDO fileDO) {
        Assert.notNull(fileDO);
        if (ObjUtil.equal(fileDO.getStatus(), MaterialFileStatus.LOCKED.getValue())) {
            throw exception(MATERIAL_FILE_IS_LOCKED);
        }
    }

    // 父文件下子文件 名称 是否存在
    private boolean isNameExists(String creator, Long parentId, Long id, String name, Integer source, Boolean isFile) {
        if (StrUtil.isNotEmpty(name)) {
            return materialFileMapper.exists(new LambdaQueryWrapperX<MaterialFileDO>()
                    .eqIfPresent(MaterialFileDO::getCreator, creator)
                    .eqIfPresent(MaterialFileDO::getParentId, parentId)
                    .neIfPresent(MaterialFileDO::getId, id)
                    .eqIfPresent(MaterialFileDO::getSource, source)
                    .eqIfPresent(MaterialFileDO::getName, name)
                    .eqIfPresent(MaterialFileDO::getIsFile, isFile));
        }
        return false;
    }

    // 校验 父文件下子文件 名称 是否存在
    private void validateNameNotExists(String creator, Long parentId, Long id, String name, Integer source, Boolean isFile) {
        if (isNameExists(creator, parentId, id, name, source, isFile)) {
            throw exception(MATERIAL_FILE_NAME_EXISTS);
        }
    }

}