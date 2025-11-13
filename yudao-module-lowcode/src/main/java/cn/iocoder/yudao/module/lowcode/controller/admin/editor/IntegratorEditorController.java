package cn.iocoder.yudao.module.lowcode.controller.admin.editor;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.lowcode.controller.admin.BaseLowcodeController;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.*;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.integratorconfig.IntegratorConfigDO;
import cn.iocoder.yudao.module.lowcode.enums.IntegratorConfigType;
import cn.iocoder.yudao.module.lowcode.enums.MaterialFileSource;
import cn.iocoder.yudao.module.lowcode.integrator.IntegratorDataManager;
import cn.iocoder.yudao.module.lowcode.integrator.IntegratorExecutorLock;
import cn.iocoder.yudao.module.lowcode.integrator.IntegratorSyncClient;
import cn.iocoder.yudao.module.lowcode.integrator.utils.IntegratorEncryptUtils;
import cn.iocoder.yudao.module.lowcode.service.integratorconfig.IntegratorConfigService;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * @author leo
 */
@Slf4j
@Tag(name = "管理后台 - 低代码-集成器")
@RestController
@RequestMapping("/lowcode/integrator-editor")
@Validated
public class IntegratorEditorController extends BaseLowcodeController {

    @Resource
    private IntegratorConfigService integratorConfigService;
    @Resource
    private IntegratorDataManager integratorDataManager;
    @Resource
    private IntegratorExecutorLock integratorExecutorLock;

    @PostMapping("/config/create")
    @Operation(summary = "创建低代码-集成器配置")
    public CommonResult<Long> createIntegratorConfig(@Valid @RequestBody IntegratorConfigSaveReqVO createReqVO) {
        checkSource(EDITOR, MaterialFileSource.INTEGRATOR);
        return success(integratorConfigService.createIntegratorConfig(createReqVO));
    }

    @PutMapping("/config/update")
    @Operation(summary = "更新低代码-集成器配置")
    public CommonResult<Boolean> updateIntegratorConfig(@Valid @RequestBody IntegratorConfigSaveReqVO updateReqVO) {
        checkSource(EDITOR, MaterialFileSource.INTEGRATOR);
        integratorConfigService.updateIntegratorConfig(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/config/delete")
    @Operation(summary = "删除低代码-集成器配置")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteIntegratorConfig(@RequestParam("id") Long id) {
        checkSource(EDITOR, MaterialFileSource.INTEGRATOR);
        integratorConfigService.deleteIntegratorConfig(id);
        return success(true);
    }

    @GetMapping("/config/get")
    @Operation(summary = "获得低代码-集成器配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<IntegratorConfigRespVO> getIntegratorConfig(@RequestParam("id") Long id) {
        checkSource(EDITOR, MaterialFileSource.INTEGRATOR);
        IntegratorConfigDO integratorConfig = integratorConfigService.getIntegratorConfig(id);
        return success(BeanUtils.toBean(integratorConfig, IntegratorConfigRespVO.class));
    }

    @GetMapping("/config/list")
    @Operation(summary = "获得低代码-集成器配置List")
    public CommonResult<List<IntegratorConfigRespVO>> getIntegratorConfigList(@Valid IntegratorConfigListReqVO reqVO) {
        checkSource(EDITOR, MaterialFileSource.INTEGRATOR);
        reqVO.setConfigType(IntegratorConfigType.REMOTE.getValue());
        List<IntegratorConfigDO> list = integratorConfigService.getIntegratorConfigList(reqVO);
        return success(BeanUtils.toBean(list, IntegratorConfigRespVO.class));
    }

    @GetMapping("/config/get-local")
    @Operation(summary = "获得低代码-集成器配置(本机)")
    public CommonResult<IntegratorConfigRespVO> getLocalIntegratorConfig() {
        checkSource(EDITOR, MaterialFileSource.INTEGRATOR);
        IntegratorConfigDO integratorConfig = integratorConfigService.getLocalIntegratorConfig();
        return success(BeanUtils.toBean(integratorConfig, IntegratorConfigRespVO.class));
    }

    @PostMapping("/config/gen-local")
    @Operation(summary = "生成低代码-集成器配置(本机)")
    public CommonResult<IntegratorConfigRespVO> genLocalIntegratorConfig() {
        checkSource(EDITOR, MaterialFileSource.INTEGRATOR);
        IntegratorConfigDO integratorConfig = integratorConfigService.genLocalIntegratorConfig();
        return success(BeanUtils.toBean(integratorConfig, IntegratorConfigRespVO.class));
    }

    @PostMapping("/sync")
    @Operation(summary = "集成器-同步数据")
    public CommonResult<IntegratorEntrySyncRespVO> integratorEntrySync(@Valid @RequestBody IntegratorEntrySyncReqVO syncReqVO) {
        checkSourceAndOperator(EDITOR, syncReqVO.getFileId());
        var fileId = syncReqVO.getFileId();
        var integratorConfig = integratorConfigService.getIntegratorConfig(syncReqVO.getConfigId());
        var integrateEntry = integratorConfig.getIntegrateEntry();
        var integrateKey = integratorConfig.getIntegrateKey();
        var isPull = Boolean.TRUE.equals(syncReqVO.getIsPull());
        var tenantId = TenantContextHolder.getTenantId();
        var ttl = 1800L;//30分钟
        var syncRespVO = new AtomicReference<IntegratorEntrySyncRespVO>();
        log.info("开始同步文件 fileId: {}, isPull: {} ", fileId, isPull);
        integratorExecutorLock.lock(fileId, ttl, () -> {
            var client = new IntegratorSyncClient(tenantId, integrateEntry, integrateKey).startSession(ttl);
            if(isPull) {
                var syncDataVO = client.pullSyncData(syncReqVO);
                this.integratorDataManager.integratorEntrySync(syncDataVO);
                syncRespVO.set(new IntegratorEntrySyncRespVO(true));
            }else {
                var syncDataVO = this.integratorDataManager.integratorEntryPull(syncReqVO);
                syncRespVO.set(client.pushSyncData(syncDataVO));
            }
        });
        log.info("同步文件完成 fileId: {}, syncResp: {}", fileId, JSON.toJSONString(syncRespVO.get()));
        return success(syncRespVO.get());
    }

    @PostMapping("/entry/session")
    @Operation(summary = "集成器入口-会话")
    public CommonResult<IntegratorEntryEncryptDataVO> integratorEntrySession(@Valid @RequestBody IntegratorEntryEncryptDataVO dataVO) {
        var integrateKey = this.integratorConfigService.getLocalIntegratorKey();
        var sessionVO = IntegratorEncryptUtils.toDecryptSession(integrateKey, dataVO);
        return success(IntegratorEncryptUtils.toSessionResp(integrateKey, sessionVO.getSessionTtl()));
    }

    @PostMapping("/entry/pull")
    @Operation(summary = "集成器入口-拉取入口")
    public CommonResult<IntegratorEntryEncryptDataVO> integratorEntryPull(@Valid @RequestBody IntegratorEntryEncryptDataVO dataVO) {
        var integrateKey = this.integratorConfigService.getLocalIntegratorKey();
        var pullReqVO = IntegratorEncryptUtils.toDecryptObject(integrateKey, dataVO, IntegratorEntryPullReqVO.class);
        var syncDataVO = this.integratorDataManager.integratorEntryPull(pullReqVO);
        return success(IntegratorEncryptUtils.toEncryptObject(integrateKey, dataVO.getSessionId(), syncDataVO));
    }

    @PostMapping("/entry/push")
    @Operation(summary = "集成器入口-推送入口")
    public CommonResult<IntegratorEntryEncryptDataVO> integratorEntryPush(@Valid @RequestBody IntegratorEntryEncryptDataVO dataVO) {
        var integrateKey = this.integratorConfigService.getLocalIntegratorKey();
        var syncDataVO = IntegratorEncryptUtils.toDecryptObject(integrateKey, dataVO, IntegratorEntrySyncDataVO.class);
        this.integratorDataManager.integratorEntrySync(syncDataVO);
        return success(IntegratorEncryptUtils.toEncryptObject(integrateKey, dataVO.getSessionId(), new IntegratorEntrySyncRespVO(Boolean.TRUE)));
    }

}
