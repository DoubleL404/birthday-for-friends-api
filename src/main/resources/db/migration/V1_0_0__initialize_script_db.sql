START TRANSACTION;

CREATE TABLE IF NOT EXISTS `role` (
    `id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(50) NOT NULL,
    `display_name` varchar(50) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_role__name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `permission` (
    `id` int NOT NULL AUTO_INCREMENT,
    `action` varchar(15) NOT NULL,
    `resource` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_permission__action_resource` (`action`, `resource`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `role_permission` (
    `role_id` int NOT NULL,
    `permission_id` int NOT NULL,
    PRIMARY KEY (`role_id`, `permission_id`),
    CONSTRAINT `fk_role_permission__role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPdate CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `refresh_token` (
    `id` int NOT NULL AUTO_INCREMENT,
    `username` varchar(255) NOT NULL,
    `token` char(36) NOT NULL,
    `expired_time` timestamp NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `gender` (
    `id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(15) NOT NULL,
    `display_name` varchar(15) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_gender__name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `language` (
    `id` int NOT NULL AUTO_INCREMENT,
    `code` varchar(10) NOT NULL,
    `name` varchar(50) NOT NULL,
    `display_order` int NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `user` (
    `id` char(36) NOT NULL,
    `email` varchar(255) NOT NULL,
    `password` char(60) NOT NULL,
    `first_name` varchar(50) NOT NULL,
    `last_name` varchar(50) NOT NULL,
    `birthdate` date NOT NULL,
    `gender_id` int NOT NULL,
    `role_id` int NOT NULL,
    `language_id` int NOT NULL,
    `is_account_non_expired` boolean NOT NULL DEFAULT true,
    `is_account_non_locked` boolean NOT NULL DEFAULT true,
    `is_credentials_non_expired` boolean NOT NULL DEFAULT true,
    `is_enabled` boolean NOT NULL DEFAULT true,
    `created_date` timestamp NOT NULL,
    `last_modified_date` timestamp NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_user__email` (`email`),
    CONSTRAINT `fk_user__gender` FOREIGN KEY (`gender_id`) REFERENCES `gender` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `fk_user__role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `fk_user__language` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `friend` (
    `id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(100) NOT NULL,
    `other_name` varchar(100),
    `birthdate` date NOT NULL,
    `gender_id` int NOT NULL,
    `email` varchar(255),
    `phone` varchar(10),
    `note` text,
    `user_id` CHAR(36) NOT NULL,
    `created_date` timestamp NOT NULL,
    `last_modified_date` timestamp NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_friend__user_id_email` (`user_id`, `email`),
    UNIQUE KEY `uq_friend__user_id_phone` (`user_id`, `phone`),
    CONSTRAINT `fk_friend__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

COMMIT;