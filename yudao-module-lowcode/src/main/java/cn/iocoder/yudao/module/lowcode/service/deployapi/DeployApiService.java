package cn.iocoder.yudao.module.lowcode.service.deployapi;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.DeployApiDeleteReqVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.DeployApiDeployReqVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.DeployApiPageReqVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.DeployApiUpdateStatusReqVO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.deployapi.DeployApiDO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfiledata.MaterialFileDataDO;

import java.util.List;

/**
 * 低代码-部署接口 Service 接口
 *
 * @author 芋道源码
 */
public interface DeployApiService {

    PageResult<DeployApiDO> deployApiPage(DeployApiPageReqVO pageReqVO);

    MaterialFileDataDO deployApiDeploy(DeployApiDeployReqVO deployReqVO);

    Boolean deployApiUpdateStatus(DeployApiUpdateStatusReqVO updateStatusReqVO);

    Boolean deployApiDelete(DeployApiDeleteReqVO deleteReqVO);

    List<DeployApiDO> deployApiList(Long sourceFileId);
}