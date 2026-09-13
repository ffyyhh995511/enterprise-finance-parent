USE `product`;

CREATE TABLE IF NOT EXISTS `t_product`
(
    `id`          BIGINT         NOT NULL COMMENT 'product id',
    `name`        VARCHAR(128)   NOT NULL COMMENT 'product name',
    `price`       DECIMAL(18, 2) NOT NULL COMMENT 'unit price',
    `stock`       INT            NOT NULL DEFAULT 0 COMMENT 'available stock',
    `status`      VARCHAR(32)    NOT NULL DEFAULT 'ON_SALE' COMMENT 'ON_SALE / OFF_SALE',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='product catalog';

CREATE TABLE IF NOT EXISTS `undo_log`
(
    `branch_id`     BIGINT       NOT NULL COMMENT 'branch transaction id',
    `xid`           VARCHAR(128) NOT NULL COMMENT 'global transaction id',
    `context`       VARCHAR(128) NOT NULL COMMENT 'undo_log context,such as serialization',
    `rollback_info` LONGBLOB     NOT NULL COMMENT 'rollback info',
    `log_status`    INT          NOT NULL COMMENT '0:normal status,1:defense status',
    `log_created`   DATETIME(6)  NOT NULL COMMENT 'create datetime',
    `log_modified`  DATETIME(6)  NOT NULL COMMENT 'modify datetime',
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='AT transaction mode undo table';

INSERT INTO `t_product` (`id`, `name`, `price`, `stock`, `status`)
VALUES (1, 'Gold Fund Share', 100.00, 1000, 'ON_SALE'),
       (2, 'Bond Package A', 50.00, 500, 'ON_SALE'),
       (3, 'Insurance Plan B', 200.00, 200, 'ON_SALE')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

CREATE TABLE IF NOT EXISTS `t_tx_demo`
(
    `id`      BIGINT      NOT NULL COMMENT 'snowflake id',
    `tag`     VARCHAR(64) NOT NULL COMMENT 'same tag for one demo run',
    `service` VARCHAR(32) NOT NULL COMMENT 'user / order / product',
    PRIMARY KEY (`id`),
    KEY `idx_tag` (`tag`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='seata insert-chain demo';
