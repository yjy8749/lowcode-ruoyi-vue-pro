package cn.iocoder.yudao.module.lowcode.service.materialfilelog;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lowcode.controller.admin.materialfilelog.vo.MaterialFileLogPageReqVO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfile.MaterialFileDO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfiledata.MaterialFileDataDO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfilelog.MaterialFileLogDO;
import cn.iocoder.yudao.module.lowcode.dal.mysql.materialfilelog.MaterialFileLogMapper;
import cn.iocoder.yudao.module.lowcode.enums.MaterialFileLogOpType;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 低代码-物料文件操作日志 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MaterialFileLogServiceImpl implements MaterialFileLogService {

    @Resource
    private MaterialFileLogMapper materialFileLogMapper;

    @Override
    public PageResult<MaterialFileLogDO> getMaterialFileLogPage(MaterialFileLogPageReqVO reqVO) {
        var query = new LambdaQueryWrapperX<MaterialFileLogDO>();
        query.select(
                MaterialFileLogDO::getId,
                MaterialFileLogDO::getFileId,
                MaterialFileLogDO::getFileSource,
                MaterialFileLogDO::getDataType,
                MaterialFileLogDO::getOpType,
                MaterialFileLogDO::getOpDesc,
                MaterialFileLogDO::getOpDetail,
                MaterialFileLogDO::getOpVersion,
//                MaterialFileLogDO::getOpData,
                MaterialFileLogDO::getCreator,
                MaterialFileLogDO::getCreateTime,
                MaterialFileLogDO::getUpdater,
                MaterialFileLogDO::getUpdateTime,
                MaterialFileLogDO::getDeleted
        );
        return materialFileLogMapper.selectPage(reqVO, query
                .betweenIfPresent(MaterialFileLogDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(MaterialFileLogDO::getFileId, reqVO.getFileId())
                .eqIfPresent(MaterialFileLogDO::getFileSource, reqVO.getFileSource())
                .eqIfPresent(MaterialFileLogDO::getDataType, reqVO.getDataType())
                .eqIfPresent(MaterialFileLogDO::getOpType, reqVO.getOpType())
                .orderByDesc(MaterialFileLogDO::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFileLog(MaterialFileDO file, MaterialFileLogOpType opType) {
        saveFileLog(file, null, opType, "");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFileLog(MaterialFileDO file, MaterialFileLogOpType opType, String opDetail) {
        saveFileLog(file, null, opType, opDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFileLog(MaterialFileDO file, MaterialFileDataDO fileData, MaterialFileLogOpType opType, String opDetail) {
        var logDO = new MaterialFileLogDO();
        logDO.setFileId(file.getId());
        logDO.setFileSource(file.getSource());
        logDO.setOpType(opType.getValue());
        logDO.setOpDesc(opType.getName());
        logDO.setOpDetail(opDetail);
        if (fileData != null) {
            logDO.setDataType(fileData.getDataType());
            logDO.setOpVersion(fileData.getVersion());
            logDO.setOpData(fileData.getData());
        }
        materialFileLogMapper.insert(logDO);
    }

}