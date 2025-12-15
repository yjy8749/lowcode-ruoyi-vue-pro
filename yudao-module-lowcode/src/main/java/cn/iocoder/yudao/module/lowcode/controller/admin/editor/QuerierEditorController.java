package cn.iocoder.yudao.module.lowcode.controller.admin.editor;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.infra.controller.admin.db.vo.DataSourceConfigRespVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.db.DataSourceConfigDO;
import cn.iocoder.yudao.module.infra.service.db.DataSourceConfigService;
import cn.iocoder.yudao.module.lowcode.config.LowcodeProperties;
import cn.iocoder.yudao.module.lowcode.controller.admin.BaseLowcodeController;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.*;
import cn.iocoder.yudao.module.lowcode.controller.admin.materialfiledata.vo.MaterialFileDataRespVO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.deployapi.DeployApiDO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.materialfiledata.MaterialFileDataDO;
import cn.iocoder.yudao.module.lowcode.enums.MaterialFileSource;
import cn.iocoder.yudao.module.lowcode.querier.QueryDomainDeployApi;
import cn.iocoder.yudao.module.lowcode.querier.QueryDomainExecutor;
import cn.iocoder.yudao.module.lowcode.querier.QueryDomainExecutorFactory;
import cn.iocoder.yudao.module.lowcode.querier.QueryDomainGenerator;
import cn.iocoder.yudao.module.lowcode.querier.xml.QueryDomain;
import cn.iocoder.yudao.module.lowcode.service.deployapi.DeployApiService;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.module.lowcode.enums.ErrorCodeConstants.QUERIER_DATA_SOURCE_NOT_ENABLED;

/**
 * @author leo
 */
@Tag(name = "管理后台 - 低代码-查询器")
@RestController
@RequestMapping("/lowcode/querier-editor")
@Validated
public class QuerierEditorController extends BaseLowcodeController {

    @Resource
    private QueryDomainGenerator queryDomainGenerator;
    @Resource
    private QueryDomainExecutorFactory queryDomainExecutorFactory;
    @Resource
    private DataSourceConfigService dataSourceConfigService;
    @Resource
    private DeployApiService deployApiService;
    @Resource
    private QueryDomainDeployApi queryDomainDeployApi;
    @Resource
    private LowcodeProperties lowcodeProperties;

    @GetMapping("/data-source-list")
    @Operation(summary = "获得数据源配置列表")
    public CommonResult<List<DataSourceConfigRespVO>> getDataSourceList() {
        checkSource(EDITOR, MaterialFileSource.QUERIER);
        List<DataSourceConfigDO> list = dataSourceConfigService.getDataSourceConfigList();
        Long tenantId = TenantContextHolder.getTenantId();
        if(CollectionUtil.isNotEmpty(lowcodeProperties.getEnabledDataSources())
                && CollectionUtil.isNotEmpty(lowcodeProperties.getEnabledDataSources().get(tenantId))){
            List<String> dataSources = lowcodeProperties.getEnabledDataSources().get(tenantId);
            list = list.stream().filter(i -> dataSources.contains(i.getName())).toList();
        }else if(CollectionUtil.isNotEmpty(lowcodeProperties.getDisabledDataSources())
                && CollectionUtil.isNotEmpty(lowcodeProperties.getDisabledDataSources().get(tenantId))){
            List<String> dataSources = lowcodeProperties.getDisabledDataSources().get(tenantId);
            list = list.stream().filter(i -> !dataSources.contains(i.getName())).toList();
        }
        return success(BeanUtils.toBean(list, DataSourceConfigRespVO.class));
    }

    @PostMapping("/gen-table-xml")
    @Operation(summary = "查询器-生成数据表配置XML")
    public CommonResult<String> genTableXml(@Valid @RequestBody GenTableXmlReqVO reqVO) {
        checkSource(EDITOR, MaterialFileSource.QUERIER);
        return success(queryDomainGenerator.genXml(reqVO.getDataSourceId(), reqVO.getTableName()));
    }

    @PostMapping("/parse-query-domain-xml")
    @Operation(summary = "查询器-解析数据表配置XML")
    public CommonResult<QueryDomain> parseQueryDomainXml(@Valid @RequestBody ParseQueryDomainXmlReqVO reqVO) {
        checkSource(EDITOR, MaterialFileSource.QUERIER);
        QueryDomain queryDomain = this.queryDomainExecutorFactory.parseQueryDomainXml(reqVO.getQueryXml());
        return success(queryDomain);
    }

    @PostMapping("/to-query-domain-xml")
    @Operation(summary = "查询器-数据表配置对象生成XML")
    public CommonResult<String> toQueryDomainXml(@Valid @RequestBody ToQueryDomainXmlReqVO reqVO) {
        checkSource(EDITOR, MaterialFileSource.QUERIER);
        String queryXml = this.queryDomainExecutorFactory.toQueryDomainXml(reqVO.getQueryDomain());
        return success(queryXml);
    }

    @PostMapping("/get-query-domain")
    @Operation(summary = "查询器-获取接口查询配置Bean")
    public CommonResult<QueryDomain> getQueryDomain(@Valid @RequestBody GetQueryDomainReqVO reqVO) {
        checkSource(EDITOR, MaterialFileSource.DESIGNER);
        var apiData = this.queryDomainDeployApi.getApiData(reqVO.getApiName(), reqVO.getApiCode());
        QueryDomain queryDomain = this.queryDomainExecutorFactory.parseQueryDomainXml(apiData.getQueryXml());
        return success(queryDomain);
    }

    @PostMapping("/deploy-api/deploy")
    @Operation(summary = "查询器-接口部署")
    public CommonResult<MaterialFileDataRespVO> deployApiDeploy(@Valid @RequestBody DeployApiDeployReqVO deployReqVO) {
        checkSourceAndOperator(EDITOR, deployReqVO.getFileId());
        QuerierEditorDataVO querierEditorDataVO = JSON.parseObject(deployReqVO.getData(), QuerierEditorDataVO.class);
        var dataSourceConfig = this.dataSourceConfigService.getDataSourceConfig(querierEditorDataVO.getDataSourceId());
        String dataSourceName = dataSourceConfig.getName();
        Long tenantId = TenantContextHolder.getTenantId();
        if(CollectionUtil.isNotEmpty(lowcodeProperties.getEnabledDataSources())
                && CollectionUtil.isNotEmpty(lowcodeProperties.getEnabledDataSources().get(tenantId))){
            List<String> dataSources = lowcodeProperties.getEnabledDataSources().get(tenantId);
            if(!dataSources.contains(dataSourceName)) {
                throw exception((QUERIER_DATA_SOURCE_NOT_ENABLED));
            }
        }else if(CollectionUtil.isNotEmpty(lowcodeProperties.getDisabledDataSources())
                && CollectionUtil.isNotEmpty(lowcodeProperties.getDisabledDataSources().get(tenantId))){
            List<String> dataSources = lowcodeProperties.getDisabledDataSources().get(tenantId);
            if(dataSources.contains(dataSourceName)) {
                throw exception((QUERIER_DATA_SOURCE_NOT_ENABLED));
            }
        }
        MaterialFileDataDO materialFileData = this.deployApiService.deployApiDeploy(deployReqVO);
        return success(BeanUtils.toBean(materialFileData, MaterialFileDataRespVO.class));
    }

    @GetMapping("/deploy-api/page")
    @Operation(summary = "查询器-接口部署分页")
    public CommonResult<PageResult<DeployApiRespVO>> deployApiPage(@Valid DeployApiPageReqVO pageReqVO) {
        checkSourceAndOperator(QUERY, pageReqVO.getSourceFileId());
        PageResult<DeployApiDO> pageResult = this.deployApiService.deployApiPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DeployApiRespVO.class));
    }

    @PostMapping("/deploy-api/update-status")
    @Operation(summary = "查询器-接口状态更新")
    public CommonResult<Boolean> deployApiUpdateStatus(@Valid @RequestBody DeployApiUpdateStatusReqVO updateStatusReqVO) {
        checkSourceAndOperator(EDITOR, updateStatusReqVO.getSourceFileId());
        return success(this.deployApiService.deployApiUpdateStatus(updateStatusReqVO));
    }

    @PostMapping("/deploy-api/delete")
    @Operation(summary = "查询器-接口删除")
    public CommonResult<Boolean> deployApiDelete(@Valid @RequestBody DeployApiDeleteReqVO deleteReqVO) {
        checkSourceAndOperator(EDITOR, deleteReqVO.getSourceFileId());
        return success(this.deployApiService.deployApiDelete(deleteReqVO));
    }

    @PostMapping(value = "/test/select-list")
    @Operation(summary = "查询器-测试-selectList")
    public CommonResult<List<Map<Object, Object>>> testSelectList(@Valid @RequestBody TestQueryDomainReqVO reqVO) {
        checkSource(EDITOR, MaterialFileSource.QUERIER);
        QueryDomainExecutor executor = this.queryDomainExecutorFactory.createExecutor(reqVO.getDataSourceId(), reqVO.getQueryXml());
        return success(executor.selectList(reqVO.getParams()));
    }

    @PostMapping(value = "/test/select-one")
    @Operation(summary = "查询器-测试-selectOne")
    public CommonResult<Map<Object, Object>> testSelectOne(@Valid @RequestBody TestQueryDomainReqVO reqVO) {
        checkSource(EDITOR, MaterialFileSource.QUERIER);
        QueryDomainExecutor executor = this.queryDomainExecutorFactory.createExecutor(reqVO.getDataSourceId(), reqVO.getQueryXml());
        return success(executor.selectOne(reqVO.getParams()));
    }

    @PostMapping(value = "/test/select-page")
    @Operation(summary = "查询器-测试-selectPage")
    public CommonResult<PageResult<Map<Object, Object>>> testSelectPage(@Valid @RequestBody TestQueryDomainReqVO reqVO) {
        checkSource(EDITOR, MaterialFileSource.QUERIER);
        QueryDomainExecutor executor = this.queryDomainExecutorFactory.createExecutor(reqVO.getDataSourceId(), reqVO.getQueryXml());
        return success(executor.selectPage(reqVO.getParams()));
    }

    @PostMapping(value = "/test/export")
    @Operation(summary = "查询器-测试-export")
    public void exportUserList(@Valid @RequestBody TestQueryDomainReqVO reqVO, HttpServletResponse response) {
        checkSource(EDITOR, MaterialFileSource.QUERIER);
        QueryDomainExecutor executor = this.queryDomainExecutorFactory.createExecutor(reqVO.getDataSourceId(), reqVO.getQueryXml());
        executor.export(reqVO.getParams(), response, 10);
    }
}
