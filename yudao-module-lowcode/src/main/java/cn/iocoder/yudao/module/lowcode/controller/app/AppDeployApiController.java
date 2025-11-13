package cn.iocoder.yudao.module.lowcode.controller.app;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.QuerierEditorDataVO;
import cn.iocoder.yudao.module.lowcode.enums.DeployApiChannel;
import cn.iocoder.yudao.module.lowcode.querier.QueryDomainDeployApi;
import cn.iocoder.yudao.module.lowcode.querier.QueryDomainExecutor;
import cn.iocoder.yudao.module.lowcode.querier.QueryDomainExecutorFactory;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * @author leo
 */
@Tag(name = "用户 APP - 低代码-部署接口")
@RestController
@RequestMapping("/lowcode/deploy-api")
@Validated
public class AppDeployApiController {
    @Resource
    private QueryDomainDeployApi queryDomainDeployApi;

    @Resource
    private QueryDomainExecutorFactory queryDomainExecutorFactory;

    private void assertAccess(QuerierEditorDataVO data, String method) {
        if (!CollectionUtil.contains(data.getChannels(), DeployApiChannel.CLIENT.getValue()) ||
                !CollectionUtil.contains(data.getQueryTypes(), method)) {
            throw exception(NOT_FOUND);
        }
    }

    @PostMapping(value = "/{apiName}/select-list")
    @Operation(summary = "调用部署接口-查询selectList")
    public CommonResult<List<Map<Object, Object>>> deployApiSelectList(
            @PathVariable String apiName, @RequestParam(required = false) String apiCode, @RequestBody QueryDomainParams params) {
        var apiData = this.queryDomainDeployApi.getApiData(apiName, apiCode);
        assertAccess(apiData, "selectList");
        QueryDomainExecutor executor = this.queryDomainExecutorFactory.createExecutor(apiData.getDataSourceId(), apiData.getQueryXml());
        return success(executor.selectList(params));
    }

    @PostMapping(value = "/{apiName}/select-one")
    @Operation(summary = "调用部署接口-查询selectOne")
    public CommonResult<Map<Object, Object>> deployApiSelectOne(
            @PathVariable String apiName, @RequestParam(required = false) String apiCode, @RequestBody QueryDomainParams params) {
        var apiData = this.queryDomainDeployApi.getApiData(apiName, apiCode);
        assertAccess(apiData, "selectOne");
        QueryDomainExecutor executor = this.queryDomainExecutorFactory.createExecutor(apiData.getDataSourceId(), apiData.getQueryXml());
        return success(executor.selectOne(params));
    }

    @PostMapping(value = "/{apiName}/select-page")
    @Operation(summary = "调用部署接口-查询selectPage")
    public CommonResult<PageResult<Map<Object, Object>>> deployApiSelectPage(
            @PathVariable String apiName, @RequestParam(required = false) String apiCode, @RequestBody QueryDomainParams params) {
        var apiData = this.queryDomainDeployApi.getApiData(apiName, apiCode);
        assertAccess(apiData, "selectPage");
        QueryDomainExecutor executor = this.queryDomainExecutorFactory.createExecutor(apiData.getDataSourceId(), apiData.getQueryXml());
        return success(executor.selectPage(params));
    }

    @PostMapping(value = "/{apiName}/select-count")
    @Operation(summary = "调用部署接口-查询selectCount")
    public CommonResult<Long> deployApiSelectCount(
            @PathVariable String apiName, @RequestParam(required = false) String apiCode, @RequestBody QueryDomainParams params) {
        var apiData = this.queryDomainDeployApi.getApiData(apiName, apiCode);
        assertAccess(apiData, "selectCount");
        QueryDomainExecutor executor = this.queryDomainExecutorFactory.createExecutor(apiData.getDataSourceId(), apiData.getQueryXml());
        return success(executor.selectCount(params));
    }
}
