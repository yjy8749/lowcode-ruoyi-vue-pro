package cn.iocoder.yudao.module.lowcode.service.materialfilelog;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.lowcode.controller.admin.materialfilelog.vo.MaterialFileLogPageReqVO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfile.MaterialFileDO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfiledata.MaterialFileDataDO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfilelog.MaterialFileLogDO;
import cn.iocoder.yudao.module.lowcode.enums.MaterialFileLogOpType;

/**
 * 低代码-物料文件操作日志 Service 接口
 *
 * @author 芋道源码
 */
public interface MaterialFileLogService {

    /**
     * 获得低代码-物料文件操作日志分页
     */
    PageResult<MaterialFileLogDO> getMaterialFileLogPage(MaterialFileLogPageReqVO pageReqVO);

    /**
     * 保存 低代码-物料文件操作日志
     */
    void saveFileLog(MaterialFileDO file, MaterialFileLogOpType opType);

    /**
     * 保存 低代码-物料文件操作日志
     */
    void saveFileLog(MaterialFileDO file, MaterialFileLogOpType opType, String opDetail);

    /**
     * 保存 低代码-物料文件数据操作日志
     */
    void saveFileLog(MaterialFileDO file, MaterialFileDataDO fileData, MaterialFileLogOpType opType, String opDetail);

}