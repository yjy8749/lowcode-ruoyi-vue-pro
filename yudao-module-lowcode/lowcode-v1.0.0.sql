-- 低代码模块
DROP TABLE IF EXISTS `lowcode_material_file`;
CREATE TABLE `lowcode_material_file`
(
    `id`                  bigint        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `creator`             varchar(64)   NOT NULL DEFAULT '' COMMENT '创建者',
    `create_time`         datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`             varchar(64)   NOT NULL DEFAULT '' COMMENT '更新者',
    `update_time`         datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             bit(1)        NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`           bigint        NOT NULL DEFAULT 0 COMMENT '租户编号',
    `name`                varchar(255)  NOT NULL DEFAULT '' COMMENT '名称',
    `description`         varchar(1024) NOT NULL DEFAULT '' COMMENT '描述',
    `source`              int           NOT NULL DEFAULT 0 COMMENT '来源 0-无 1-查询器 2-设计器 3-集成器',
    `parent_id`           bigint        NOT NULL DEFAULT 0 COMMENT '父ID',
    `is_file`             bit(1)        NOT NULL DEFAULT b'0' COMMENT '是否是文件',
    `is_private`          bit(1)        NOT NULL DEFAULT b'0' COMMENT '是否私有',
    `status`              tinyint       NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-正常 2-锁定 3-弃用',
    `sort`                int           NOT NULL DEFAULT 0 COMMENT '排序',
    `source_file_id`      bigint        NOT NULL DEFAULT 0 COMMENT '来源文件ID',
    `source_file_version` int           NOT NULL DEFAULT 0 COMMENT '来源文件版本号',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '低代码-物料文件';

CREATE INDEX idx_creator ON lowcode_material_file (creator);
CREATE INDEX idx_tenant_id ON lowcode_material_file (tenant_id);
CREATE INDEX idx_name ON lowcode_material_file (name);
CREATE INDEX idx_parent_id ON lowcode_material_file (parent_id);

DROP TABLE IF EXISTS `lowcode_material_file_data`;
CREATE TABLE `lowcode_material_file_data`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `creator`     varchar(64) NOT NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64) NOT NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     bit(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`   bigint      NOT NULL DEFAULT 0 COMMENT '租户编号',
    `file_id`     bigint      NOT NULL DEFAULT 0 COMMENT '文件ID',
    `file_source` int         NOT NULL DEFAULT 0 COMMENT '来源 0-无 1-查询器 2-设计器 3-集成器',
    `data_type`   int         NOT NULL DEFAULT 0 COMMENT '数据类型 0-主数据',
    `version`     int         NOT NULL DEFAULT 0 COMMENT '版本号',
    `data`        json        NOT NULL DEFAULT ('{}') COMMENT '文件数据',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '低代码-物料文件数据';

CREATE INDEX idx_tenant_id ON lowcode_material_file_data (tenant_id);
CREATE UNIQUE INDEX ux_file ON lowcode_material_file_data (file_id, data_type, version);

DROP TABLE IF EXISTS `lowcode_material_file_log`;
CREATE TABLE `lowcode_material_file_log`
(
    `id`          bigint        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `creator`     varchar(64)   NOT NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64)   NOT NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     bit(1)        NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`   bigint        NOT NULL DEFAULT 0 COMMENT '租户编号',
    `file_id`     bigint        NOT NULL DEFAULT 0 COMMENT '文件ID',
    `file_source` int           NOT NULL DEFAULT 0 COMMENT '来源 0-无 1-查询器 2-设计器 3-集成器',
    `data_type`   int           NOT NULL DEFAULT 0 COMMENT '数据类型 0-主数据',
    `op_type`     int           NOT NULL DEFAULT 0 COMMENT '操作类型',
    `op_desc`     varchar(255)  NOT NULL DEFAULT '' COMMENT '操作描述',
    `op_detail`   varchar(1024) NOT NULL DEFAULT '' COMMENT '详细信息',
    `op_version`  int           NOT NULL DEFAULT 0 COMMENT '版本号',
    `op_data`     json          NOT NULL DEFAULT ('{}') COMMENT '文件数据',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '低代码-物料文件操作日志';

CREATE INDEX idx_tenant_id ON lowcode_material_file_log (tenant_id);
CREATE INDEX idx_file_id ON lowcode_material_file_log (file_id);

DROP TABLE IF EXISTS `lowcode_deploy_api`;
CREATE TABLE `lowcode_deploy_api`
(
    `id`                  bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `creator`             varchar(64)  NOT NULL DEFAULT '' COMMENT '创建者',
    `create_time`         datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`             varchar(64)  NOT NULL DEFAULT '' COMMENT '更新者',
    `update_time`         datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`           bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
    `api_name`            varchar(128) NOT NULL DEFAULT '' COMMENT '接口名称',
    `api_code`            varchar(128) NOT NULL DEFAULT '' COMMENT '接口编码',
    `source_file_id`      bigint       NOT NULL DEFAULT 0 COMMENT '接口源文件ID',
    `source_file_version` int          NOT NULL DEFAULT 0 COMMENT '接口源文件版本号',
    `api_status`          int          NOT NULL DEFAULT 0 COMMENT '接口状态 1-已上线 2-已下线',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '低代码-部署接口';

CREATE UNIQUE INDEX ux_api_code ON lowcode_deploy_api (tenant_id, api_code);
CREATE UNIQUE INDEX ux_api_version ON lowcode_deploy_api (tenant_id, api_name, source_file_version);
CREATE UNIQUE INDEX ux_file_version ON lowcode_deploy_api (tenant_id, source_file_id, source_file_version);

DROP TABLE IF EXISTS `lowcode_deploy_menu`;
CREATE TABLE `lowcode_deploy_menu`
(
    `id`                  bigint      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `creator`             varchar(64) NOT NULL DEFAULT '' COMMENT '创建者',
    `create_time`         datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`             varchar(64) NOT NULL DEFAULT '' COMMENT '更新者',
    `update_time`         datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             bit(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`           bigint      NOT NULL DEFAULT 0 COMMENT '租户编号',
    `menu_id`             bigint      NOT NULL DEFAULT 0 COMMENT '菜单ID',
    `source_file_id`      bigint      NOT NULL DEFAULT 0 COMMENT '菜单源文件ID',
    `source_file_version` int         NOT NULL DEFAULT 0 COMMENT '菜单源文件版本号',
    `menu_status`         int         NOT NULL DEFAULT 0 COMMENT '菜单状态 1-已上线 2-已下线',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '低代码-部署菜单';

CREATE INDEX idx_menu_id ON lowcode_deploy_menu (menu_id);
CREATE INDEX idx_file_version ON lowcode_deploy_menu (source_file_id, source_file_version);

DROP TABLE IF EXISTS `lowcode_integrator_config`;
CREATE TABLE `lowcode_integrator_config`
(
    `id`              bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `creator`         varchar(64)  NOT NULL DEFAULT '' COMMENT '创建者',
    `create_time`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`         varchar(64)  NOT NULL DEFAULT '' COMMENT '更新者',
    `update_time`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`       bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
    `config_name`     varchar(255) NOT NULL DEFAULT '' COMMENT '配置名称',
    `config_type`     int          NOT NULL DEFAULT 0 COMMENT '配置类型 0-本机 1-远程',
    `integrate_entry` varchar(255) NOT NULL DEFAULT '' COMMENT '集成器入口',
    `integrate_key`   varchar(255) NOT NULL DEFAULT '' COMMENT '集成校验KEY',
    `comment`         varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '低代码-集成器配置';

CREATE INDEX idx_integrate_entry ON lowcode_integrator_config (integrate_entry);
CREATE INDEX idx_integrate_key ON lowcode_integrator_config (integrate_key);

# 插入字典
INSERT INTO system_dict_type (name, type, status, remark, creator, create_time, updater, update_time, deleted,
                              deleted_time)
VALUES ('Lowcode 物料状态', 'lowcode_material_status', 0, '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false, '1970-01-01 00:00:00');
INSERT INTO system_dict_type (name, type, status, remark, creator, create_time, updater, update_time, deleted,
                              deleted_time)
VALUES ('Lowcode 查询器符号类型', 'lowcode_querier_field_symbols', 0, '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false, '1970-01-01 00:00:00');
INSERT INTO system_dict_type (name, type, status, remark, creator, create_time, updater, update_time, deleted,
                              deleted_time)
VALUES ('Lowcode 查询器查询类型', 'lowcode_querier_query_types', 0, '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false, '1970-01-01 00:00:00');
INSERT INTO system_dict_type (name, type, status, remark, creator, create_time, updater, update_time, deleted,
                              deleted_time)
VALUES ('Lowcode 查询器确权类型', 'lowcode_querier_auth_types', 0, '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false, '1970-01-01 00:00:00');
INSERT INTO system_dict_type (name, type, status, remark, creator, create_time, updater, update_time, deleted,
                              deleted_time)
VALUES ('Lowcode API部署渠道', 'lowcode_querier_deploy_api_channels', 0, '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false, '1970-01-01 00:00:00');
INSERT INTO system_dict_type (name, type, status, remark, creator, create_time, updater, update_time, deleted,
                              deleted_time)
VALUES ('Lowcode API部署状态', 'lowcode_querier_deploy_api_status', 0, '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false, '1970-01-01 00:00:00');
INSERT INTO system_dict_type (name, type, status, remark, creator, create_time, updater, update_time, deleted,
                              deleted_time)
VALUES ('Lowcode MENU部署状态', 'lowcode_designer_deploy_menu_status', 0, '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false, '1970-01-01 00:00:00');

INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, '禁用', '0', 'lowcode_material_status', 0, 'info', '', '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, '正常', '1', 'lowcode_material_status', 0, 'primary', '', '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, '锁定', '2', 'lowcode_material_status', 0, 'warning', '', '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, '弃用', '3', 'lowcode_material_status', 0, 'danger', '', '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'selectList', 'selectList', 'lowcode_querier_query_types', 0, '', '', '列表查询', '1', '2025-10-01 00:00:00',
        '1', '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'selectOne', 'selectOne', 'lowcode_querier_query_types', 0, '', '', '单条查询', '1', '2025-10-01 00:00:00',
        '1', '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'selectPage', 'selectPage', 'lowcode_querier_query_types', 0, '', '', '分页查询', '1', '2025-10-01 00:00:00',
        '1', '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'selectCount', 'selectCount', 'lowcode_querier_query_types', 0, '', '', '统计查询', '1',
        '2025-10-01 00:00:00', '1', '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'NONE', 'NONE', 'lowcode_querier_field_symbols', 0, '', '', '无需判断', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'EQ', 'EQ', 'lowcode_querier_field_symbols', 0, '', '', '=', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'LIKE', 'LIKE', 'lowcode_querier_field_symbols', 0, '', '', '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'IN', 'IN', 'lowcode_querier_field_symbols', 0, '', '', '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'NOTIN', 'NOTIN', 'lowcode_querier_field_symbols', 0, '', '', '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'BETWEEN', 'BETWEEN', 'lowcode_querier_field_symbols', 0, '', '', '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'ISNULL', 'ISNULL', 'lowcode_querier_field_symbols', 0, '', '', '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'NOTNULL', 'NOTNULL', 'lowcode_querier_field_symbols', 0, '', '', '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'NE', 'NE', 'lowcode_querier_field_symbols', 0, '', '', '!=', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'GE', 'GE', 'lowcode_querier_field_symbols', 0, '', '', '>=', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'GT', 'GT', 'lowcode_querier_field_symbols', 0, '', '', '>', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'LE', 'LE', 'lowcode_querier_field_symbols', 0, '', '', '<=', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'LT', 'LT', 'lowcode_querier_field_symbols', 0, '', '', '<', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'LLIKE', 'LLIKE', 'lowcode_querier_field_symbols', 0, '', '', 'left like', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'RLIKE', 'RLIKE', 'lowcode_querier_field_symbols', 0, '', '', 'right like', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'NOTLIKE', 'NOTLIKE', 'lowcode_querier_field_symbols', 0, '', '', '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'JSON_CONTAINS', 'JSON_CONTAINS', 'lowcode_querier_field_symbols', 0, '', '', '', '1', '2025-10-01 00:00:00',
        '1', '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'FIND_IN_SET', 'FIND_IN_SET', 'lowcode_querier_field_symbols', 0, '', '', '', '1', '2025-10-01 00:00:00',
        '1', '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, '登录人ID', 'authUserId', 'lowcode_querier_auth_types', 0, '', '', '登录人ID', '1', '2025-10-01 00:00:00',
        '1', '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, '管理后台', '0', 'lowcode_querier_deploy_api_channels', 0, '', '', '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'C端应用', '1', 'lowcode_querier_deploy_api_channels', 0, '', '', '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, '待上线', '0', 'lowcode_querier_deploy_api_status', 0, 'warning', '', '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', true);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, '已上线', '1', 'lowcode_querier_deploy_api_status', 0, 'success', '', '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, '已下线', '2', 'lowcode_querier_deploy_api_status', 0, 'danger', '', '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, 'export', 'export', 'lowcode_querier_query_types', 0, '', '', '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, '已上线', '1', 'lowcode_designer_deploy_menu_status', 0, 'success', '', '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator,
                              create_time, updater, update_time, deleted)
VALUES (0, '已下线', '2', 'lowcode_designer_deploy_menu_status', 0, 'danger', '', '', '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);

# 插入菜单
INSERT INTO system_menu (name, permission, type, sort, parent_id, path, icon, component, component_name, status,
                         visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES ('低代码', '', 1, 21, 0, '/lowcode', 'fa:cubes', '', '', 0, true, true, true, '1', '2025-10-01 00:00:00', '1',
        '2025-10-01 00:00:00', false);
SET @temp_id = (SELECT id
                FROM system_menu
                WHERE name = '低代码'
                  and path = '/lowcode'
                LIMIT 1);
INSERT INTO system_menu (name, permission, type, sort, parent_id, path, icon, component, component_name, status,
                         visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES ('查询器', 'lowcode:querier:query', 2, 0, @temp_id, 'querier', 'fa:gg', 'lowcode/querier/index',
        'LowcodeQuerierIndex', 0, true, true, true, '1', '2025-10-01 00:00:00', '1', '2025-10-01 00:00:00', false);
INSERT INTO system_menu (name, permission, type, sort, parent_id, path, icon, component, component_name, status,
                         visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES ('设计器', 'lowcode:designer:query', 2, 0, @temp_id, 'designer', 'fa:gg-circle', 'lowcode/designer/index',
        'LowcodeDesignerIndex', 0, true, true, true, '1', '2025-10-01 00:00:00', '1', '2025-10-01 00:00:00', false);
INSERT INTO system_menu (name, permission, type, sort, parent_id, path, icon, component, component_name, status,
                         visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES ('集成器', '', 1, 0, @temp_id, 'integrator', 'fa:cubes', '', '', 0, true, true, true, '1', '2025-10-01 00:00:00',
        '1', '2025-10-01 00:00:00', false);
SET @temp_id = (SELECT id
                FROM system_menu
                WHERE name = '查询器'
                  and component_name = 'LowcodeQuerierIndex'
                LIMIT 1);
INSERT INTO system_menu (name, permission, type, sort, parent_id, path, icon, component, component_name, status,
                         visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES ('查询定义', 'lowcode:querier:editor', 3, 0, @temp_id, '', '', '', '', 0, true, true, true, '1',
        '2025-10-01 00:00:00', '1', '2025-10-01 00:00:00', false);
SET @temp_id = (SELECT id
                FROM system_menu
                WHERE name = '设计器'
                  and component_name = 'LowcodeDesignerIndex'
                LIMIT 1);
INSERT INTO system_menu (name, permission, type, sort, parent_id, path, icon, component, component_name, status,
                         visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES ('页面设计', 'lowcode:designer:editor', 3, 0, @temp_id, '', '', '', '', 0, true, true, true, '1',
        '2025-10-01 00:00:00', '1', '2025-10-01 00:00:00', false);
SET @temp_id = (SELECT id
                FROM system_menu
                WHERE name = '集成器'
                  and path = 'integrator'
                LIMIT 1);
INSERT INTO system_menu (name, permission, type, sort, parent_id, path, icon, component, component_name, status,
                         visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES ('配置管理', 'lowcode:integrator:editor', 2, 0, @temp_id, 'config', '', 'lowcode/integrator/config',
        'LowcodeIntegratorConfig', 0, true, true, true, '1', '2025-10-01 00:00:00', '1', '2025-10-01 00:00:00', false);
INSERT INTO system_menu (name, permission, type, sort, parent_id, path, icon, component, component_name, status,
                         visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES ('集成管理', 'lowcode:integrator:editor', 2, 0, @temp_id, 'index', '', 'lowcode/integrator/index',
        'LowcodeIntegratorIndex', 0, true, true, true, '1', '2025-10-01 00:00:00', '1', '2025-10-01 00:00:00', false);