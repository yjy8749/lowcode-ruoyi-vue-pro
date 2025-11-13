package cn.iocoder.yudao.module.lowcode.service.integratorconfig;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.IntegratorConfigListReqVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.IntegratorConfigSaveReqVO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.integratorconfig.IntegratorConfigDO;
import cn.iocoder.yudao.module.lowcode.dal.mysql.integratorconfig.IntegratorConfigMapper;
import cn.iocoder.yudao.module.lowcode.enums.IntegratorConfigType;
import cn.iocoder.yudao.module.lowcode.integrator.utils.IntegratorEncryptUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lowcode.enums.ErrorCodeConstants.*;

/**
 * 低代码-集成器配置 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class IntegratorConfigServiceImpl implements IntegratorConfigService {

    @Resource
    private IntegratorConfigMapper integratorConfigMapper;

    private void validateConfigTypeNotLocal(IntegratorConfigDO integratorConfig) {
        if (IntegratorConfigType.LOCAL.getValue().equals(integratorConfig.getConfigType())) {
            throw exception(INTEGRATOR_CONFIG_TYPE_ERROR);
        }
    }

    private IntegratorConfigDO validateIntegratorConfigExists(Long id) {
        var dbConfig = integratorConfigMapper.selectById(id);
        if (dbConfig == null) {
            throw exception(INTEGRATOR_CONFIG_NOT_EXISTS);
        }
        return dbConfig;
    }

    private void validateIntegrateKeyNotExists(IntegratorConfigDO integratorConfig) {
        var dbConfig = integratorConfigMapper.selectFirstOne(IntegratorConfigDO::getIntegrateKey, integratorConfig.getIntegrateKey());
        if (dbConfig != null && !dbConfig.getId().equals(integratorConfig.getId())) {
            throw exception(INTEGRATOR_CONFIG_KEY_EXISTS);
        }
    }

    private void validateIntegrateEntryNotExists(IntegratorConfigDO integratorConfig) {
        var dbConfig = integratorConfigMapper.selectFirstOne(IntegratorConfigDO::getIntegrateEntry, integratorConfig.getIntegrateEntry());
        if (dbConfig != null && !dbConfig.getId().equals(integratorConfig.getId())) {
            throw exception(INTEGRATOR_CONFIG_ENTRY_EXISTS);
        }
    }

    private void validateConfigNameNotExists(IntegratorConfigDO integratorConfig) {
        var dbConfig = integratorConfigMapper.selectFirstOne(IntegratorConfigDO::getConfigName, integratorConfig.getConfigName());
        if (dbConfig != null && !dbConfig.getId().equals(integratorConfig.getId())) {
            throw exception(INTEGRATOR_CONFIG_NAME_EXISTS);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createIntegratorConfig(IntegratorConfigSaveReqVO createReqVO) {
        // 插入
        IntegratorConfigDO integratorConfig = BeanUtils.toBean(createReqVO, IntegratorConfigDO.class);

        // 校验
        validateConfigTypeNotLocal(integratorConfig);
        validateIntegrateKeyNotExists(integratorConfig);
        validateIntegrateEntryNotExists(integratorConfig);
        validateConfigNameNotExists(integratorConfig);

        integratorConfig.setConfigType(IntegratorConfigType.REMOTE.getValue());
        integratorConfigMapper.insert(integratorConfig);
        // 返回
        return integratorConfig.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIntegratorConfig(IntegratorConfigSaveReqVO updateReqVO) {
        // 校验存在
        validateIntegratorConfigExists(updateReqVO.getId());

        // 更新
        IntegratorConfigDO updateObj = BeanUtils.toBean(updateReqVO, IntegratorConfigDO.class);

        // 校验
        validateConfigTypeNotLocal(updateObj);
        validateIntegrateKeyNotExists(updateObj);
        validateIntegrateEntryNotExists(updateObj);
        validateConfigNameNotExists(updateObj);

        integratorConfigMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteIntegratorConfig(Long id) {
        // 校验存在
        var integratorConfig = validateIntegratorConfigExists(id);

        // 校验
        validateConfigTypeNotLocal(integratorConfig);

        // 删除
        integratorConfigMapper.deleteById(id);
    }

    @Override
    public IntegratorConfigDO getIntegratorConfig(Long id) {
        return integratorConfigMapper.selectById(id);
    }

    @Override
    public List<IntegratorConfigDO> getIntegratorConfigList(IntegratorConfigListReqVO reqVO) {
        return integratorConfigMapper.selectList(new LambdaQueryWrapperX<IntegratorConfigDO>()
                .likeIfPresent(IntegratorConfigDO::getConfigName, reqVO.getConfigName())
                .eqIfPresent(IntegratorConfigDO::getConfigType, reqVO.getConfigType())
                .eqIfPresent(IntegratorConfigDO::getIntegrateEntry, reqVO.getIntegrateEntry())
                .eqIfPresent(IntegratorConfigDO::getIntegrateKey, reqVO.getIntegrateKey())
                .inIfPresent(IntegratorConfigDO::getId, reqVO.getIds())
                .orderByDesc(IntegratorConfigDO::getId));
    }

    @Override
    public IntegratorConfigDO getLocalIntegratorConfig() {
        return integratorConfigMapper.selectFirstOne(IntegratorConfigDO::getConfigType, IntegratorConfigType.LOCAL.getValue());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IntegratorConfigDO genLocalIntegratorConfig() {
        var configDO = this.getLocalIntegratorConfig();
        if (configDO == null) {
            configDO = new IntegratorConfigDO();
            configDO.setConfigType(IntegratorConfigType.LOCAL.getValue());
            configDO.setConfigName("本机配置");
            configDO.setIntegrateKey(IntegratorEncryptUtils.genKey());
            integratorConfigMapper.insert(configDO);
            return configDO;
        } else {
            configDO.setIntegrateKey(IntegratorEncryptUtils.genKey());
            integratorConfigMapper.updateById(configDO);
            return configDO;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getLocalIntegratorKey() {
        var localConfig = this.getLocalIntegratorConfig();
        if (localConfig == null) {
            localConfig = this.genLocalIntegratorConfig();
        }
        return localConfig.getIntegrateKey();
    }

}