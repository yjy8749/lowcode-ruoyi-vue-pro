package cn.iocoder.yudao.module.lowcode.service.mediadir;

import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lowcode.controller.admin.mediadir.vo.MediaDirSaveReqVO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.mediadir.MediaDirDO;
import cn.iocoder.yudao.module.lowcode.dal.dataobject.mediafile.MediaFileDO;
import cn.iocoder.yudao.module.lowcode.dal.mysql.mediadir.MediaDirMapper;
import cn.iocoder.yudao.module.lowcode.dal.mysql.mediafile.MediaFileMapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lowcode.enums.ErrorCodeConstants.MEDIA_DIR_NOT_EXISTS;

/**
 * 低代码-媒体库目录 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MediaDirServiceImpl implements MediaDirService {

    @Resource
    private MediaDirMapper mediaDirMapper;

    @Resource
    private MediaFileMapper mediaFileMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMediaDir(MediaDirSaveReqVO createReqVO) {
        // 插入
        MediaDirDO mediaDir = BeanUtils.toBean(createReqVO, MediaDirDO.class);
        mediaDirMapper.insert(mediaDir);

        // 更新路径
        var parentDir = mediaDirMapper.selectById(mediaDir.getParentId());
        updateIdPath(parentDir, mediaDir);

        // 返回
        return mediaDir.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMediaDir(MediaDirSaveReqVO updateReqVO) {
        // 校验存在
        validateMediaDirExists(updateReqVO.getId());

        // 更新
        MediaDirDO updateObj = BeanUtils.toBean(updateReqVO, MediaDirDO.class);
        mediaDirMapper.updateById(updateObj);

        // 更新路径
        var mediaDir = mediaDirMapper.selectById(updateObj.getId());
        var parentDir = this.mediaDirMapper.selectById(mediaDir.getParentId());
        updateIdPath(parentDir, mediaDir);
    }

    @Override
    public void deleteMediaDir(Long id) {
        // 校验存在
        validateMediaDirExists(id);
        // 删除
        mediaDirMapper.deleteById(id);
    }

    private void updateIdPath(MediaDirDO parentDir, MediaDirDO mediaDir) {
        var oldPath = mediaDir.getIdPath();

        // 更新目录路径
        var newPath = (parentDir == null ? "" : (parentDir.getIdPath() + ",")) + mediaDir.getId();
        mediaDirMapper.updateById(MediaDirDO.builder().id(mediaDir.getId()).idPath(newPath).build());
        mediaDir.setIdPath(newPath);

        // 更新文件路径
        if(ObjectUtil.notEqual(oldPath, newPath)) {
            // 更新文件路径
            mediaFileMapper.update(new LambdaUpdateWrapper<MediaFileDO>()
                    .eq(MediaFileDO::getDirId, mediaDir.getId()).set(MediaFileDO::getDirIdPath, newPath));
            // 更新子目录路径
            List<MediaDirDO> subDirs = mediaDirMapper.selectList(new LambdaQueryWrapperX<MediaDirDO>()
                    .eq(MediaDirDO::getParentId, mediaDir.getId()));
            for (MediaDirDO subDir : subDirs) {
                updateIdPath(mediaDir, subDir);
            }
        }
    }

    private void validateMediaDirExists(Long id) {
        if (mediaDirMapper.selectById(id) == null) {
            throw exception(MEDIA_DIR_NOT_EXISTS);
        }
    }

}