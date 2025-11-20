package cn.iocoder.yudao.module.lowcode.service.deployapi;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.*;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.deployapi.DeployApiDO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfiledata.MaterialFileDataDO;
import cn.iocoder.yudao.module.lowcode.dal.mysql.deployapi.DeployApiMapper;
import cn.iocoder.yudao.module.lowcode.enums.DeployApiStatus;
import cn.iocoder.yudao.module.lowcode.querier.QueryDomainDeployApi;
import cn.iocoder.yudao.module.lowcode.service.materialfiledata.MaterialFileDataService;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lowcode.enums.ErrorCodeConstants.*;

/**
 * 低代码-部署接口 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class DeployApiServiceImpl implements DeployApiService {

    @Resource
    private DeployApiMapper deployApiMapper;

    @Resource
    private MaterialFileDataService materialFileDataService;

    @Resource
    private QueryDomainDeployApi queryDomainDeployApi;

    @Override
    public PageResult<DeployApiDO> deployApiPage(DeployApiPageReqVO pageReqVO) {
        return deployApiMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MaterialFileDataDO deployApiDeploy(DeployApiDeployReqVO deployReqVO) {
        var saveData = this.materialFileDataService.saveMaterialFileData(deployReqVO);
        if (saveData.getVersion().equals(deployReqVO.getVersion())) {
            var deployApiData = JSON.parseObject(saveData.getData(), QuerierEditorDataVO.class);
            String apiName = deployApiData.getApiName();
            if (StrUtil.isEmpty(apiName)) {
                throw exception(DEPLOY_API_NAME_EMPTY_ERROR);
            }
            var isApiNameExist = this.deployApiMapper.exists(new LambdaQueryWrapperX<DeployApiDO>()
                    .neIfPresent(DeployApiDO::getSourceFileId, saveData.getFileId())
                    .eqIfPresent(DeployApiDO::getApiName, apiName));

            if (isApiNameExist) {
                throw exception(DEPLOY_API_NAME_EXISTS);
            }
            var apiIndex = this.deployApiMapper.selectCountBySourceFileId(saveData.getFileId()) + 1;
            var apiIndexStr = String.format("%0" + Math.max(String.valueOf(apiIndex).length(), 2) + "d", apiIndex);
            var apiFileId = saveData.getFileId();
            var apiFileIdStr = String.format("%0" + Math.max(String.valueOf(apiFileId).length(), 6) + "d", apiFileId);
            var apiCode = String.format("API%s%s", apiFileIdStr, apiIndexStr);
            var deployApi = new DeployApiDO();
            deployApi.setApiCode(apiCode);
            deployApi.setApiName(apiName);
            deployApi.setSourceFileId(saveData.getFileId());
            deployApi.setSourceFileVersion(saveData.getVersion());
            deployApi.setApiStatus(DeployApiStatus.ONLINE.getValue());
            this.deployApiMapper.insert(deployApi);

            if (Boolean.TRUE.equals(deployReqVO.getIsOfflineOther())) {
                this.deployApiMapper.update(DeployApiDO.builder().apiStatus(DeployApiStatus.OFFLINE.getValue()).build(),
                        new LambdaQueryWrapperX<DeployApiDO>()
                                .ne(DeployApiDO::getId, deployApi.getId())
                                .eq(DeployApiDO::getApiStatus, DeployApiStatus.ONLINE.getValue())
                                .eq(DeployApiDO::getApiName, apiName)
                );
            }

            queryDomainDeployApi.evictApiData(apiName, apiCode);
            queryDomainDeployApi.evictApiData(deployApi.getApiName(), null);
        }
        return saveData;
    }

    @Override
    public Boolean deployApiUpdateStatus(DeployApiUpdateStatusReqVO updateStatusReqVO) {
        var deployApi = deployApiMapper.selectOne(new LambdaQueryWrapperX<DeployApiDO>()
                .eqIfPresent(DeployApiDO::getSourceFileId, updateStatusReqVO.getSourceFileId())
                .eqIfPresent(DeployApiDO::getSourceFileVersion, updateStatusReqVO.getSourceFileVersion()));
        if (deployApi == null) {
            throw exception(DEPLOY_API_NOT_EXISTS);
        }
        if (deployApi.getApiStatus().equals(updateStatusReqVO.getApiStatus())) {
            throw exception(DEPLOY_API_STATUS_ERROR);
        }
        var update = new DeployApiDO();
        update.setId(deployApi.getId());
        update.setApiStatus(updateStatusReqVO.getApiStatus());
        this.deployApiMapper.updateById(update);
        queryDomainDeployApi.evictApiData(deployApi.getApiName(), deployApi.getApiCode());
        queryDomainDeployApi.evictApiData(deployApi.getApiName(), null);

        return true;
    }

    @Override
    public Boolean deployApiDelete(DeployApiDeleteReqVO updateStatusReqVO) {
        var deployApi = deployApiMapper.selectOne(new LambdaQueryWrapperX<DeployApiDO>()
                .eqIfPresent(DeployApiDO::getSourceFileId, updateStatusReqVO.getSourceFileId())
                .eqIfPresent(DeployApiDO::getSourceFileVersion, updateStatusReqVO.getSourceFileVersion()));
        if (deployApi == null) {
            throw exception(DEPLOY_API_NOT_EXISTS);
        }
        if (DeployApiStatus.ONLINE.getValue().equals(deployApi.getApiStatus())) {
            throw exception(DEPLOY_API_STATUS_ERROR);
        }
        this.deployApiMapper.deleteById(deployApi.getId());
        queryDomainDeployApi.evictApiData(deployApi.getApiName(), deployApi.getApiCode());
        queryDomainDeployApi.evictApiData(deployApi.getApiName(), null);
        return true;
    }

    @Override
    public List<DeployApiDO> deployApiList(Long sourceFileId) {
        return this.deployApiMapper.selectList(DeployApiDO::getSourceFileId, sourceFileId);
    }
}