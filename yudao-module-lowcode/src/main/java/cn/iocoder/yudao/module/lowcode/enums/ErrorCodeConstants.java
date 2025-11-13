package cn.iocoder.yudao.module.lowcode.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * lowcode 错误码枚举类
 * lowcode 系统，使用 1-005-000-000 段
 *
 * @author leo
 */
public interface ErrorCodeConstants {

    // ========== 参数配置 1-005-000-000 ==========

    ErrorCode MATERIAL_FILE_NOT_EXISTS = new ErrorCode(1_005_000_001, "物料文件不存在");
    ErrorCode MATERIAL_FILE_NAME_EXISTS = new ErrorCode(1_005_000_002, "文件名称已存在");
    ErrorCode MATERIAL_FILE_IS_LOCKED = new ErrorCode(1_005_000_003, "文件已锁定");
    ErrorCode MATERIAL_FILE_SOURCE_ERROR = new ErrorCode(1_005_000_004, "来源类型不正确");
    ErrorCode QUERY_DOMAIN_GEN_XML_ERROR = new ErrorCode(1_005_000_005, "生成查询器xml配置错误");
    ErrorCode QUERY_DOMAIN_BUILD_ERROR = new ErrorCode(1_005_000_006, "构建查询器错误");
    ErrorCode QUERY_DOMAIN_EXECUTE_ERROR = new ErrorCode(1_005_000_007, "查询器执行错误");
    ErrorCode QUERY_DOMAIN_QUERY_REQUIRED_ERROR = new ErrorCode(1_005_000_008, "查询必填项校验错误");
    ErrorCode DEPLOY_API_NOT_EXISTS = new ErrorCode(1_005_000_009, "接口不存在");
    ErrorCode MATERIAL_FILE_DATA_NOT_EXISTS = new ErrorCode(1_005_000_010, "文件数据不存在");
    ErrorCode DEPLOY_API_NAME_EMPTY_ERROR = new ErrorCode(1_005_000_011, "接口名称不能为空");
    ErrorCode DEPLOY_API_NAME_EXISTS = new ErrorCode(1_005_000_012, "接口名称已存在");
    ErrorCode DEPLOY_API_STATUS_ERROR = new ErrorCode(1_005_000_013, "接口状态不正确");
    ErrorCode QUERY_DOMAIN_FILTER_FAILURE = new ErrorCode(1_005_000_014, "查询过滤器执行失败");
    ErrorCode DEPLOY_MENU_EXISTS = new ErrorCode(1_005_000_015, "菜单已存在");
    ErrorCode DEPLOY_MENU_NOT_EXISTS = new ErrorCode(1_005_000_016, "菜单不存在");
    ErrorCode INTEGRATOR_CONFIG_NOT_EXISTS = new ErrorCode(1_005_000_017, "配置不存在");
    ErrorCode INTEGRATOR_CONFIG_KEY_EXISTS = new ErrorCode(1_005_000_018, "秘钥已存在");
    ErrorCode INTEGRATOR_CONFIG_ENTRY_EXISTS = new ErrorCode(1_005_000_019, "入口地址已存在");
    ErrorCode INTEGRATOR_CONFIG_NAME_EXISTS = new ErrorCode(1_005_000_020, "配置名称已存在");
    ErrorCode INTEGRATOR_CONFIG_TYPE_ERROR = new ErrorCode(1_005_000_021, "配置类型不存在");
    ErrorCode INTEGRATOR_DECRYPT_ERROR = new ErrorCode(1_005_000_022, "解密失败");
    ErrorCode INTEGRATOR_SIGN_VALID_ERROR = new ErrorCode(1_005_000_023, "签名不正确");
    ErrorCode INTEGRATOR_REQ_EXPIRED_ERROR = new ErrorCode(1_005_000_024, "请求已过期");
    ErrorCode INTEGRATOR_REQ_DUPLICATE_ERROR = new ErrorCode(1_005_000_025, "重复请求");
    ErrorCode INTEGRATOR_SESSION_TTL_ERROR = new ErrorCode(1_005_000_026, "会话有效期错误");
    ErrorCode INTEGRATOR_ENTRY_REQ_ERROR = new ErrorCode(1_005_000_027, "请求失败");
    ErrorCode INTEGRATOR_FILE_SYNC_EXECUTING_ERROR = new ErrorCode(1_005_000_028, "文件正在同步中");
    ErrorCode INTEGRATOR_FILE_SYNC_VERSION_ERROR = new ErrorCode(1_005_000_029, "缺少指定版本数据");
    ErrorCode MEDIA_DIR_NOT_EXISTS = new ErrorCode(1_005_000_030, "目录不存在");
    ErrorCode MEDIA_FILE_NOT_EXISTS = new ErrorCode(1_005_000_031, "文件不存在");


}
