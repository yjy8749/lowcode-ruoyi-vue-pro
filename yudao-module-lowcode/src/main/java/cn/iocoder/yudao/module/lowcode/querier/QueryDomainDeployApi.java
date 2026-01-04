package cn.iocoder.yudao.module.lowcode.querier;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.QuerierEditorDataVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.materialfiledata.vo.GetMaterialFileDataReqVO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.deployapi.DeployApiDO;
import cn.iocoder.yudao.module.lowcode.dal.mysql.deployapi.DeployApiMapper;
import cn.iocoder.yudao.module.lowcode.enums.DeployApiStatus;
import cn.iocoder.yudao.module.lowcode.enums.MaterialFileDataType;
import cn.iocoder.yudao.module.lowcode.enums.MaterialFileSource;
import cn.iocoder.yudao.module.lowcode.service.materialfiledata.MaterialFileDataService;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lowcode.enums.ErrorCodeConstants.DEPLOY_API_NOT_EXISTS;

/**
 * @author leo
 */
@Component
public class QueryDomainDeployApi {

    private static final String QUERY_DOMAIN_DEPLOY_API_KEY = "QUERY_DOMAIN_DEPLOY_API";

    @Resource
    private DeployApiMapper deployApiMapper;

    @Resource
    private MaterialFileDataService materialFileDataService;

    @TenantIgnore
    @Cacheable(cacheNames = QUERY_DOMAIN_DEPLOY_API_KEY, key = "#apiName + '#' + #apiCode")
    public QuerierEditorDataVO getApiData(String apiName, String apiCode) {
        var deployApi = this.deployApiMapper.selectOne(new LambdaQueryWrapperX<DeployApiDO>()
                .eqIfPresent(DeployApiDO::getApiName, apiName)
                .eqIfPresent(DeployApiDO::getApiCode, apiCode)
                .eqIfPresent(DeployApiDO::getApiStatus, DeployApiStatus.ONLINE.getValue())
                .orderByDesc(DeployApiDO::getSourceFileVersion).last("limit 1"));

        if (deployApi == null) {
            throw exception(DEPLOY_API_NOT_EXISTS);
        }

        var saveData = this.materialFileDataService.getMaterialFileData(GetMaterialFileDataReqVO.builder()
                .fileId(deployApi.getSourceFileId())
                .fileSource(MaterialFileSource.QUERIER.getValue())
                .dataType(MaterialFileDataType.MAIN.getValue())
                .version(deployApi.getSourceFileVersion())
                .build());
        
        return JSON.parseObject(saveData.getData(), QuerierEditorDataVO.class);
    }

    @CacheEvict(cacheNames = QUERY_DOMAIN_DEPLOY_API_KEY, key = "#apiName + '#' + #apiCode")
    public void evictApiData(String apiName, String apiCode) {

    }

}
