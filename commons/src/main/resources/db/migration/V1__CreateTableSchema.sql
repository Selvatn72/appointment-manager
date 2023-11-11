CREATE TABLE IF NOT EXISTS `service_category` (
  `service_category_id` bigint NOT NULL AUTO_INCREMENT,
  `service_category` varchar(50) NOT NULL,
  PRIMARY KEY (`service_category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `service` (
  `service_id` bigint NOT NULL AUTO_INCREMENT,
  `service_name` varchar(50) NOT NULL,
  `created_by` varchar(50) NOT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  `service_category_id` bigint NOT NULL,
  PRIMARY KEY (`service_id`),
  FOREIGN KEY (`service_category_id`) REFERENCES `service_category` (`service_category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `sub_service` (
  `sub_service_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `created_by` varchar(50) NOT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  `service_id` bigint NOT NULL,
  PRIMARY KEY (`sub_service_id`),
  FOREIGN KEY (`service_id`) REFERENCES `service` (`service_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `admin` (
  `admin_id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `phone` varchar(50) NOT NULL,
  `shop_name` varchar(50) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  `service_category_id` bigint NOT NULL,
  PRIMARY KEY (`admin_id`),
  FOREIGN KEY (`service_category_id`) REFERENCES `service_category` (`service_category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `customer` (
  `customer_id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  PRIMARY KEY (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `branch` (
  `branch_id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) NOT NULL,
  `mail` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `website` varchar(255) DEFAULT NULL,
  `is_active` boolean DEFAULT 1,
  `city` varchar(255) NOT NULL,
  `state` varchar(255) NOT NULL,
  `country` varchar(255) NOT NULL,
  `zipcode` varchar(255) NOT NULL,
  `latitude` float NOT NULL,
  `longitude` float NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  `admin_id` bigint NOT NULL,
  `service_category_id` bigint NOT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `file_type` varchar(50) DEFAULT NULL,
  `image` longblob DEFAULT NULL,
  `is_photo_verified` boolean DEFAULT 0,
  PRIMARY KEY (`branch_id`),
  FOREIGN KEY (`admin_id`) REFERENCES `admin` (`admin_id`),
  FOREIGN KEY (`service_category_id`) REFERENCES `service_category` (`service_category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `branch_service` (
  `branch_id` bigint NOT NULL,
  `service_id` bigint NOT NULL,
  PRIMARY KEY (`branch_id`,`service_id`),
  FOREIGN KEY (`service_id`) REFERENCES `service` (`service_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`branch_id`) REFERENCES `branch` (`branch_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `employee` (
  `employee_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `phone` varchar(50) NOT NULL,
  `is_active` boolean DEFAULT 1,
  `city` varchar(50) NOT NULL,
  `degree` varchar(255) DEFAULT NULL,
  `experience` int DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `file_type` varchar(50) DEFAULT NULL,
  `image` longblob DEFAULT NULL,
  `created_by` varchar(50) NOT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  `service_id` bigint DEFAULT NULL,
  `branch_id` bigint NOT NULL,
  PRIMARY KEY (`employee_id`),
  FOREIGN KEY (`service_id`) REFERENCES `service` (`service_id`),
  FOREIGN KEY (`branch_id`) REFERENCES `branch` (`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `employee_sub_service` (
  `employee_id` bigint NOT NULL,
  `sub_service_id` bigint NOT NULL,
  PRIMARY KEY (`employee_id`,`sub_service_id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`sub_service_id`) REFERENCES `sub_service` (`sub_service_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `is_active` boolean DEFAULT 1,
  `email` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(50) NOT NULL,
  `user_name` varchar(100) NOT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  `admin_id` bigint DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `employee_id` bigint DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`),
  FOREIGN KEY (`admin_id`) REFERENCES `admin` (`admin_id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `user_verification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `verification_code` varchar(15) NOT NULL,
  `code_sent_on` datetime NOT NULL,
  `email` varchar(50) DEFAULT NULL,
  `status` varchar(50) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `public_holiday` (
  `public_holiday_id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  `public_holiday` date NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  `branch_id` bigint NOT NULL,
  `employee_id` bigint DEFAULT NULL,
  PRIMARY KEY (`public_holiday_id`),
  FOREIGN KEY (`branch_id`) REFERENCES `branch` (`branch_id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `shop_holiday` (
  `shop_holiday_id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  `shop_holiday` varchar(255) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  `branch_id` bigint NOT NULL,
  `employee_id` bigint DEFAULT NULL,
  PRIMARY KEY (`shop_holiday_id`),
  FOREIGN KEY (`branch_id`) REFERENCES `branch` (`branch_id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `appointment_status` (
  `appointment_status_id` int NOT NULL AUTO_INCREMENT,
  `status` varchar(30) NOT NULL,
  PRIMARY KEY (`appointment_status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `appointment` (
  `appointment_id` bigint NOT NULL AUTO_INCREMENT,
  `appointment_date` date NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `end_time` time NOT NULL,
  `reason_for_cancel` varchar(255) DEFAULT NULL,
  `start_time` time NOT NULL,
  `person_count` int DEFAULT 1,
  `appointee_name` varchar(50) NOT NULL,
  `appointee_email` varchar(50) NOT NULL,
  `appointee_phone` varchar(50) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) NOT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `appointment_status_id` int NOT NULL,
  `branch_id` bigint NOT NULL,
  `customer_id` bigint NOT NULL,
  `service_id` bigint NOT NULL,
  `employee_id` bigint DEFAULT NULL,
  PRIMARY KEY (`appointment_id`),
  FOREIGN KEY (`appointment_status_id`) REFERENCES `appointment_status` (`appointment_status_id`),
  FOREIGN KEY (`branch_id`) REFERENCES `branch` (`branch_id`),
  FOREIGN KEY (`service_id`) REFERENCES `service` (`service_id`),
  FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `available_slot` (
  `available_slot_id` bigint NOT NULL AUTO_INCREMENT,
  `booked_capacity` int DEFAULT NULL,
  `end_time` time DEFAULT NULL,
  `slot_date` date DEFAULT NULL,
  `start_time` time DEFAULT NULL,
  `status` varchar(25) DEFAULT NULL,
  `total_capacity` int DEFAULT NULL,
  `branch_id` bigint DEFAULT NULL,
  `service_id` bigint DEFAULT NULL,
  `employee_id` bigint DEFAULT NULL,
  PRIMARY KEY (`available_slot_id`),
  FOREIGN KEY (`service_id`) REFERENCES `service` (`service_id`),
  FOREIGN KEY (`branch_id`) REFERENCES `branch` (`branch_id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `role` (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `role_user` (
  `user_id` bigint NOT NULL,
  `role_id` int NOT NULL,
  FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `service_availability` (
  `service_availability_id` bigint NOT NULL AUTO_INCREMENT,
  `slot_capacity` int NOT NULL,
  `slot_day` varchar(50) NOT NULL,
  `slot_interval` int NOT NULL,
  `slot_time` varchar(100) NOT NULL,
  `created_by` varchar(50) NOT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  `branch_id` bigint NOT NULL,
  `service_id` bigint NOT NULL,
  `employee_id` bigint DEFAULT NULL,
  PRIMARY KEY (`service_availability_id`),
  FOREIGN KEY (`branch_id`) REFERENCES `branch` (`branch_id`),
  FOREIGN KEY (`service_id`) REFERENCES `service` (`service_id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `trading_hours` (
  `trading_hours_id` bigint NOT NULL AUTO_INCREMENT,
  `end_time` varchar(20) NOT NULL,
  `start_time` varchar(20) NOT NULL,
  `is_week_day` boolean DEFAULT 0,
  `is_week_end` boolean DEFAULT 0,
  `created_by` varchar(50) NOT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  `branch_id` bigint NOT NULL,
  `employee_id` bigint DEFAULT NULL,
  PRIMARY KEY (`trading_hours_id`),
  FOREIGN KEY (`branch_id`) REFERENCES `branch` (`branch_id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `service_request` (
  `service_request_id` bigint NOT NULL AUTO_INCREMENT,
  `service_name` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime DEFAULT NULL,
  `admin_id` bigint NOT NULL,
  `service_category_id` bigint NOT NULL,
  PRIMARY KEY (`service_request_id`),
  FOREIGN KEY (`admin_id`) REFERENCES `admin` (`admin_id`),
  FOREIGN KEY (`service_category_id`) REFERENCES `service_category` (`service_category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;