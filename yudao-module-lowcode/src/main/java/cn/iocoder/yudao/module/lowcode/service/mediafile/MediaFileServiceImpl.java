package cn.iocoder.yudao.module.lowcode.service.mediafile;

import cn.hutool.core.io.FileUtil;
import cn.iocoder.yudao.module.lowcode.controller.admin.mediafile.vo.MediaFileBatchDeleteReqVO;
import cn.iocoder.yudao.module.lowcode.controller.admin.mediafile.vo.MediaFileBatchSaveReqVO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.mediafile.MediaFileDO;
import cn.iocoder.yudao.module.lowcode.dal.mysql.mediadir.MediaDirMapper;
import cn.iocoder.yudao.module.lowcode.dal.mysql.mediafile.MediaFileMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lowcode.enums.ErrorCodeConstants.MEDIA_FILE_NOT_EXISTS;

/**
 * 低代码-媒体库文件 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MediaFileServiceImpl implements MediaFileService {

    @Resource
    private MediaFileMapper mediaFileMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchSave(MediaFileBatchSaveReqVO batchSaveReqVO) {

        //批量保存
        var files = batchSaveReqVO.getUrls().stream().map(url -> {
            var file = new MediaFileDO();
            file.setDirId(batchSaveReqVO.getDirId());
            file.setDirIdPath(batchSaveReqVO.getDirIdPath());
            file.setType(FileUtil.getSuffix(url));
            file.setUrl(url);
            return file;
        }).toList();

        //返回
        return this.mediaFileMapper.insertBatch(files);
    }

    @Override
    public Boolean batchDelete(MediaFileBatchDeleteReqVO batchDeleteReqVO) {
        this.mediaFileMapper.deleteByIds(batchDeleteReqVO.getIds());
        return true;
    }

}