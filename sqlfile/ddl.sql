-- auto-generated definition
create schema `ttdeye-stock` collate utf8_general_ci;


create user 'ttdeye'@'%' identified by 'ttdeye@N3KyO';


GRANT ALL PRIVILEGES ON *.* TO 'ttdeye'@'%' IDENTIFIED BY 'ttdeye@N3KyO' WITH GRANT OPTION;


set password for ttdeye@'%' = password('ttdeye@N3KyO');


grant alter, alter routine, create, create routine, create temporary tables, create view, delete, drop, event, execute, index, insert, lock tables, references, select, show view, trigger, update on `ttdeye-stock`.* to ttdeye;


create table `ttdeye-stock`.ttdeye_batch
(
    batch_id        bigint auto_increment comment '批次id'
        primary key,
    batch_no        varchar(64)                         not null comment '批次编号',
    production_date date                                null comment '生产日期',
    shelf_life      int                                 not null comment '保质期-天',
    create_time     timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    delete_flag     int       default 0                 not null comment '是否删除：1-已删除，0-未删除'
)
    comment '批次信息';

create table `ttdeye-stock`.ttdeye_file_log
(
    log_id               bigint auto_increment comment '主键id'
        primary key,
    file_url             varchar(256) default ''                not null comment '导入文件地址',
    file_type            int          default 0                 not null comment '文件类别：1-商品（spu）导入，2-产品（sku）导入，3-批量入库，4-批量出库',
    create_time          timestamp    default CURRENT_TIMESTAMP not null comment '创建时间',
    create_login_account varchar(64)  default ''                not null comment '操作人账号'
)
    comment '文件记录' ;

create table `ttdeye-stock`.ttdeye_notify_record
(
    record_id   bigint auto_increment comment '通知记录id'
        primary key,
    shop_name   varchar(64)                         not null comment '店铺名称',
    shop_id     bigint    default 0                 not null comment '店铺id',
    notify_type int       default 0                 not null comment '通知类型：1-订单通知接收，2-库存变动回调通知',
    create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    result      int                                 not null comment '处理结果：1-成功，0-失败',
    notify_desc varchar(256)                        not null comment '通知内容JSON',
    update_time timestamp                           null comment '更新时间'
)
    comment '通知记录';

create table `ttdeye-stock`.ttdeye_shop
(
    shop_id           bigint auto_increment comment '商店id'
        primary key,
    shop_name         varchar(32)  default ''                not null comment '店铺名称',
    shop_callback_url varchar(256) default ''                not null comment '回调地址',
    platform          varchar(32)  default ''                not null comment '平台名称',
    create_time       timestamp    default CURRENT_TIMESTAMP not null comment '创建时间',
    delete_flag       int                                    not null comment '是否删除：1-是，0-否'
)
    comment '商店管理';

create table `ttdeye-stock`.ttdeye_sku
(
    sku_id               bigint auto_increment comment 'skuId，主键id'
        primary key,
    sku_code             varchar(64)    default ''                not null comment 'SKU代码',
    sku_name             varchar(64)    default ''                not null comment 'sku名称',
    stock_current_num    bigint         default 0                 not null comment '库存-实时库存',
    stock_all_num        bigint         default 0                 not null comment '总计入库数量',
    stock_out_num        bigint         default 0                 not null comment '总计出库数量',
    purchase_price       decimal(12, 2) default 0.00              not null comment '采购单价-分',
    remark               varchar(64)    default ''                not null comment '备注',
    degree               decimal(3, 2)  default 0.00              not null comment '度数',
    create_time          timestamp      default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time          timestamp                                null comment '更新时间',
    update_login_account varchar(64)    default ''                not null comment '更新人账号',
    source_type          int            default 1                 not null comment '1-手动添加；2-文件导入',
    state                int            default 1                 not null comment '状态：1-在售，2-已下架，3-缺货下架',
    spu_id               bigint         default 0                 not null comment 'spuId',
    spu_no               varchar(64)    default ''                not null comment 'spu编码',
    delete_flag          int            default 0                 not null comment '是否删除：1-删除，0-未删除',
    sku_no               varchar(64)    default ''                not null comment 'SKU编号'
)
    comment '产品信息表SKU';

create table `ttdeye-stock`.ttdeye_sku_batch
(
    sku_batch_id      bigint auto_increment comment 'sku库存批次表主键'
        primary key,
    sku_id            bigint                                null comment 'skuid',
    sku_no            varchar(64) default ''                not null comment 'sku编号',
    batch_id          bigint                                null comment '批次id',
    batch_no          varchar(64) default ''                not null comment '批次编号',
    stock_current_num bigint      default 0                 not null comment '批次实时库存',
    stock_all_num     bigint      default 0                 not null comment '批次总入库数量',
    stock_out_num     bigint      default 0                 not null comment '批次总出库数量',
    create_time       timestamp   default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time       timestamp                             null comment '更新时间',
    sku_batch_no      varchar(64) default ''                not null comment 'sku批次库存编号'
)
    comment 'sku库存批次表';

create table `ttdeye-stock`.ttdeye_sku_shop_detail
(
    detail_id            bigint auto_increment comment '明细id'
        primary key,
    shop_id              bigint                                not null comment '商店id',
    sku_id               bigint      default 0                 not null comment 'skuid',
    sku_code             varchar(64) default ''                not null comment 'sku编码',
    spu_id               bigint                                not null comment 'spuId',
    spu_code             varchar(64)                           not null comment 'spu编码',
    create_time          timestamp   default CURRENT_TIMESTAMP not null comment '创建时间',
    create_login_account varchar(64) default ''                not null comment '创建人编号',
    delete_flag          int         default 0                 not null comment '是否删除：1-已删除，0-未删除'
)
    comment 'sku售卖商店明细';

create table `ttdeye-stock`.ttdeye_spu
(
    spu_id               bigint auto_increment comment 'spuId'
        primary key,
    spu_code             varchar(64)  default ''                not null comment '商品编码',
    title_ch             varchar(64)  default ''                not null comment '中文名称',
    title_en             varchar(64)  default ''                not null comment '英文名称',
    purchase_url         varchar(128) default ''                not null comment '采购链接',
    spu_attributes_type  int          default 1                 not null comment '1-普通货',
    e_commerce_platform  int          default 1                 not null comment '电商平台：0-其他，1-shopfily',
    remark               varchar(256) default ''                not null comment '备注',
    create_time          timestamp    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time          timestamp                              null comment '更新时间',
    update_login_account varchar(64)  default ''                not null comment '更新人账号',
    delete_flag          int          default 0                 not null comment '是否删除：1-删除，0-未删除',
    source_type          int          default 1                 not null comment '来源：1-手动录入，2-文件导入',
    batch_flag           int          default 0                 not null comment '是否支持批次：1-支持，0-不支持',
    spu_no               varchar(64)                            not null comment 'SPU编号'
)
    comment '商品信息表' ;

create table `ttdeye-stock`.ttdeye_stock_change_record
(
    record_id              bigint auto_increment comment '库存变更记录id'
        primary key,
    sku_id                 bigint         default 0                 not null comment 'skuId',
    sku_no                 varchar(64)    default ''                not null comment 'sku编码',
    batch_id               bigint                                   null comment '批次id：无批次则为null',
    batch_no               varchar(64)                              null comment '批次号',
    sku_before_stock       bigint         default 0                 not null comment 'sku原库存',
    sku_after_stock        bigint         default 0                 not null comment 'sku变更后库存',
    sku_batch_before_stock bigint         default 0                 not null comment 'sku批次原库存',
    sku_batch_after_stock  bigint         default 0                 not null comment 'sku批次现库存',
    occur_stock            bigint         default 0                 not null comment '出入库数量：发生量',
    direction              int            default 0                 not null comment '方向：1-入库，0-出库',
    batch_flag             int            default 0                 not null comment '是否批次出入库：1-是，0-否',
    source_type            int            default 0                 not null comment '来源：1-批量导入SKU，2-采购入库导入，3-批量出库，4，手动入库，5，手动出库',
    order_no               varchar(64)    default ''                not null comment '订单编号',
    order_desc             varchar(256)   default ''                not null comment '订单商品明细：json',
    file_url               varchar(512)   default ''                null comment '如果是因为批量导入，则将文件直接存储到记录中',
    create_time            timestamp      default CURRENT_TIMESTAMP not null comment '创建时间',
    create_login_account   varchar(64)    default ''                not null comment '创建人登陆账号',
    create_nike_name       varchar(64)    default ''                not null comment '创建人姓名',
    delete_flag            int            default 0                 not null comment '是否删除：1-已删除，0-未删除',
    spu_id                 bigint         default 0                 not null comment '商品id',
    spu_no                 varchar(64)                              not null comment '商品编码',
    shop_name              varchar(64)    default ''                not null comment '订单-售卖店铺名称',
    sku_batch_no           varchar(64)    default ''                not null comment 'sku批次库存编号',
    sku_batch_id           bigint         default 0                 not null comment 'sku库存主键',
    unit_price             decimal(12, 2) default 0.00              not null comment '入库单价-分'
)
    comment '库存变更记录';

create table `ttdeye-stock`.ttdeye_user
(
    user_id             bigint auto_increment comment '用户主键id'
        primary key,
    user_code           varchar(64) default ''                not null comment '用户编码',
    nick_name           varchar(64)                           null comment '昵称',
    login_account       varchar(64) default ''                not null comment '登陆账号',
    login_password      varchar(64) default ''                not null comment '密码',
    state               int         default 1                 not null comment '状态：1-启用，2-停用',
    delete_flag         int         default 0                 not null comment '是否删除：1-删除，0-未删除',
    create_time         timestamp   default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time         timestamp                             null comment '更新时间',
    update_user_account varchar(64) default ''                not null comment '更新人账号',
    admin_flag          int         default 0                 not null comment '是否Admin：1-是，0-否',
    phone               varchar(32) default ''                not null comment '手机号',
    constraint ttdeye_user_login_account_uindex
        unique (login_account),
    constraint ttdeye_user_user_code_uindex
        unique (user_code)
)
    comment '用户表';

