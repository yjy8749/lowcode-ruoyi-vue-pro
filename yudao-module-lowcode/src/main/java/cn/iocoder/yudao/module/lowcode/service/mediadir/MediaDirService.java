package cn.iocoder.yudao.module.lowcode.service.mediadir;

import cn.iocoder.yudao.module.lowcode.controller.admin.mediadir.vo.MediaDirSaveReqVO;
import jakarta.validation.Valid;

/**
 * 低代码-媒体库目录 Service 接口
 *
 * @author 芋道源码
 */
public interface MediaDirService {

    /**
     * 创建低代码-媒体库目录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMediaDir(@Valid MediaDirSaveReqVO createReqVO);

    /**
     * 更新低代码-媒体库目录
     *
     * @param updateReqVO 更新信息
     */
    void updateMediaDir(@Valid MediaDirSaveReqVO updateReqVO);

    /**
     * 删除低代码-媒体库目录
     *
     * @param id 编号
     */
    void deleteMediaDir(Long id);

}