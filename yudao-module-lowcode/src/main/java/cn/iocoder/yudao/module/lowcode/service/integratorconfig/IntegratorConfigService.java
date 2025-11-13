package cn.iocoder.yudao.module.lowcode.service.integratorconfig;

import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.IntegratorConfigListReqVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.editor.vo.IntegratorConfigSaveReqVO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.integratorconfig.IntegratorConfigDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 低代码-集成器配置 Service 接口
 *
 * @author 芋道源码
 */
public interface IntegratorConfigService {

    /**
     * 创建低代码-集成器配置
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createIntegratorConfig(@Valid IntegratorConfigSaveReqVO createReqVO);

    /**
     * 更新低代码-集成器配置
     *
     * @param updateReqVO 更新信息
     */
    void updateIntegratorConfig(@Valid IntegratorConfigSaveReqVO updateReqVO);

    /**
     * 删除低代码-集成器配置
     *
     * @param id 编号
     */
    void deleteIntegratorConfig(Long id);

    /**
     * 获得低代码-集成器配置
     *
     * @param id 编号
     * @return 低代码-集成器配置
     */
    IntegratorConfigDO getIntegratorConfig(Long id);

    /**
     * 获得低代码-集成器配置List
     *
     * @param reqVO 查询参数
     * @return 低代码-集成器配置List
     */
    List<IntegratorConfigDO> getIntegratorConfigList(IntegratorConfigListReqVO reqVO);

    /**
     * 获得低代码-集成器配置(本机)
     *
     * @return 低代码-集成器配置
     */
    IntegratorConfigDO getLocalIntegratorConfig();

    /**
     * 生成低代码-集成器配置(本机)
     *
     * @return 低代码-集成器配置
     */
    IntegratorConfigDO genLocalIntegratorConfig();

    /**
     * 获得低代码-集成器配置(本机)Key
     *
     * @return 低代码-集成器配置验证Key
     */
    String getLocalIntegratorKey();

}