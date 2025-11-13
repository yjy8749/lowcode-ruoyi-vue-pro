package cn.iocoder.yudao.module.lowcode.service.mediafile;

import cn.iocoder.yudao.module.lowcode.controller.admin.mediafile.vo.MediaFileBatchDeleteReqVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.mediafile.vo.MediaFileBatchSaveReqVO;
import jakarta.validation.Valid;

/**
 * 低代码-媒体库文件 Service 接口
 *
 * @author 芋道源码
 */
public interface MediaFileService {

    Boolean batchSave(@Valid MediaFileBatchSaveReqVO batchSaveReqVO);
    Boolean batchDelete(MediaFileBatchDeleteReqVO batchDeleteReqVO);
}