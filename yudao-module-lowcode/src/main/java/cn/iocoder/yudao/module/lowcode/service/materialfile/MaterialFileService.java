package cn.iocoder.yudao.module.lowcode.service.materialfile;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.lowcode.controller.admin.materialfile.vo.*;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfile.MaterialFileDO;

/**
 * 低代码-物料文件 Service 接口
 *
 * @author 芋道源码
 */
public interface MaterialFileService {

    /**
     * 获得低代码-物料文件分页
     */
    PageResult<MaterialFileDO> getMaterialFilePage(MaterialFilePageReqVO pageReqVO);

    /**
     * 根据ID获得低代码-物料文件
     */
    MaterialFileDO getMaterialFileById(Long id);

    /**
     * 创建低代码-物料文件
     */
    Long createMaterialFile(MaterialFileSaveReqVO createReqVO);

    /**
     * 更新低代码-物料文件
     */
    boolean updateMaterialFile(MaterialFileUpdateReqVO updateReqVO);

    /**
     * 更新状态低代码-物料文件
     */
    boolean updateMaterialFileStatus(MaterialFileUpdateStatusReqVO updateReqVO);

    /**
     * 删除低代码-物料文件
     */
    boolean deleteMaterialFile(MaterialFileDeleteReqVO deleteReqVO);

    /**
     * 转移所有权 -物料文件
     */
    boolean transferMaterialFile(MaterialFileTransferReqVO reqVO);

    /**
     * 移动位置 -物料文件
     */
    boolean moveMaterialFile(MaterialFileMoveReqVO reqVO);

}