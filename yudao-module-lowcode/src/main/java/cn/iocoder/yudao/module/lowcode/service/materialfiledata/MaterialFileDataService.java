package cn.iocoder.yudao.module.lowcode.service.materialfiledata;

import cn.iocoder.yudao.module.lowcode.controller.admin.materialfiledata.vo.GetMaterialFileDataReqVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.materialfiledata.vo.MaterialFileDataSaveReqVO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfiledata.MaterialFileDataDO;

import java.util.Collection;
import java.util.List;

/**
 * 低代码-物料文件数据 Service 接口
 *
 * @author 芋道源码
 */
public interface MaterialFileDataService {

    /**
     * 获得低代码-物料文件数据
     */
    MaterialFileDataDO getMaterialFileData(GetMaterialFileDataReqVO getReqVO);

    /**
     * 保存低代码-物料文件数据
     */
    MaterialFileDataDO saveMaterialFileData(MaterialFileDataSaveReqVO saveReqVO);

    /**
     * 获得低代码-物料文件对应版本的数据
     */
    List<MaterialFileDataDO> getMaterialFileDataList(Long fileId, Collection<Integer> versions);

}