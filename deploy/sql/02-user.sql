USE `user`;

CREATE TABLE IF NOT EXISTS `t_user`
(
    `id`       BIGINT         NOT NULL COMMENT 'user id',
    `username` VARCHAR(64)    NOT NULL COMMENT 'username',
    `balance`  DECIMAL(18, 2) NOT NULL DEFAULT 0.00 COMMENT 'account balance',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='user account';

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

INSERT INTO `t_user` (`id`, `username`, `balance`)
VALUES (1, 'alice', 10000.00),
       (2, 'bob', 500.00)
ON DUPLICATE KEY UPDATE `username` = VALUES(`username`);
