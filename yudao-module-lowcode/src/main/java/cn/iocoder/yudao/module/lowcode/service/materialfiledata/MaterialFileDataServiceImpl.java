package cn.iocoder.yudao.module.lowcode.service.materialfiledata;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lowcode.controller.admin.materialfiledata.vo.GetMaterialFileDataReqVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.materialfiledata.vo.MaterialFileDataSaveReqVO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfiledata.MaterialFileDataDO;
import cn.iocoder.yudao.module.lowcode.dal.mysql.materialfiledata.MaterialFileDataMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

/**
 * 低代码-物料文件数据 Service 实现类
 *
 * @author 芋道源码
 */
@Slf4j
@Service
@Validated
public class MaterialFileDataServiceImpl implements MaterialFileDataService {

    @Resource
    private MaterialFileDataMapper materialFileDataMapper;

    @Override
    public MaterialFileDataDO getMaterialFileData(GetMaterialFileDataReqVO getReqVO) {
        return materialFileDataMapper.selectOne(new LambdaQueryWrapperX<MaterialFileDataDO>()
                .eqIfPresent(MaterialFileDataDO::getFileId, getReqVO.getFileId())
                .eqIfPresent(MaterialFileDataDO::getFileSource, getReqVO.getFileSource())
                .eqIfPresent(MaterialFileDataDO::getDataType, getReqVO.getDataType())
                .eqIfPresent(MaterialFileDataDO::getVersion, getReqVO.getVersion())
                .orderByDesc(MaterialFileDataDO::getVersion)
                .last("limit 1"));
    }

    @Override
    public MaterialFileDataDO saveMaterialFileData(MaterialFileDataSaveReqVO saveReqVO) {
        try {
            saveReqVO.setId(null);
            MaterialFileDataDO materialFileData = BeanUtils.toBean(saveReqVO, MaterialFileDataDO.class);
            materialFileDataMapper.insert(materialFileData);
        } catch (DuplicateKeyException ex) {
            log.error("saveMaterialFileData", ex);
        }
        return this.getMaterialFileData(GetMaterialFileDataReqVO.builder().fileId(saveReqVO.getFileId())
                .fileSource(saveReqVO.getFileSource()).dataType(saveReqVO.getDataType()).build());
    }

    @Override
    public List<MaterialFileDataDO> getMaterialFileDataList(Long fileId, Collection<Integer> versions) {
        return materialFileDataMapper.selectList(new LambdaQueryWrapperX<MaterialFileDataDO>()
                .eqIfPresent(MaterialFileDataDO::getFileId, fileId)
                .inIfPresent(MaterialFileDataDO::getVersion, versions));
    }

}