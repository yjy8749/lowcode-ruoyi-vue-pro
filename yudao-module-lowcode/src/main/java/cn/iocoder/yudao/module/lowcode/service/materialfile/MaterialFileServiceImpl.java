package cn.iocoder.yudao.module.lowcode.service.materialfile;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lowcode.controller.admin.materialfile.vo.*;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfile.MaterialFileDO;
import cn.iocoder.yudao.module.lowcode.dal.mysql.materialfile.MaterialFileMapper;
import cn.iocoder.yudao.module.lowcode.enums.MaterialFileLogOpType;
import cn.iocoder.yudao.module.lowcode.enums.MaterialFileStatus;
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
        var fileDO = validateIdAndGet(transferReqVO.getId());
        // 校验 文件是否可更新
        validateFileCanUpdate(fileDO);
        // 更新 创建人
        var allIds = getAllChild(fileDO.getId(), true).stream().map(MaterialFileDO::getId).toList();
        materialFileMapper.transferMaterialFile(allIds, transferReqVO.getCreator(), String.valueOf(transferReqVO.getReceiverId()));
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
        if (ObjUtil.equal(fileDO.getParentId(), moveReqVO.getParentId())) {
            return true;
        }
        // 更新 parentId
        materialFileMapper.updateById(MaterialFileDO.builder().id(fileDO.getId()).parentId(moveReqVO.getParentId()).build());
        // 记录日志
        String opDetail = "移动位置到" + moveReqVO.getParentName();
        materialFileLogService.saveFileLog(fileDO, MaterialFileLogOpType.MOVE, opDetail);
        return true;
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

    // 校验 父文件下子文件 名称 是否存在
    private void validateNameNotExists(String creator, Long parentId, Long id, String name, Integer source, Boolean isFile) {
        if (StrUtil.isNotEmpty(name)) {
            var isExists = materialFileMapper.exists(new LambdaQueryWrapperX<MaterialFileDO>()
                    .eqIfPresent(MaterialFileDO::getCreator, creator)
                    .eqIfPresent(MaterialFileDO::getParentId, parentId)
                    .neIfPresent(MaterialFileDO::getId, id)
                    .eqIfPresent(MaterialFileDO::getSource, source)
                    .eqIfPresent(MaterialFileDO::getName, name)
                    .eqIfPresent(MaterialFileDO::getIsFile, isFile));
            if (BooleanUtil.isTrue(isExists)) {
                throw exception(MATERIAL_FILE_NAME_EXISTS);
            }
        }
    }

}