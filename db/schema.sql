-- schema.sql
-- Creates the railway booking database schema for local MySQL deployment.

CREATE DATABASE IF NOT EXISTS `12306`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `12306`;

-- Drop tables if they already exist to allow idempotent schema reloads.
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS checkin;
DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS ticket;
DROP TABLE IF EXISTS seat_allocation;
DROP TABLE IF EXISTS booking_order;
DROP TABLE IF EXISTS user_account;
DROP TABLE IF EXISTS seat;
DROP TABLE IF EXISTS coach;
DROP TABLE IF EXISTS schedule_stop;
DROP TABLE IF EXISTS train_schedule;
DROP TABLE IF EXISTS train;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS station;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE station (
  station_id INT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(16) NOT NULL UNIQUE,
  name VARCHAR(128) NOT NULL,
  city VARCHAR(128) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB;

CREATE TABLE train (
  train_id INT PRIMARY KEY AUTO_INCREMENT,
  train_no VARCHAR(32) NOT NULL UNIQUE,
  train_type VARCHAR(32) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB;

CREATE TABLE train_schedule (
  schedule_id INT PRIMARY KEY AUTO_INCREMENT,
  train_id INT NOT NULL,
  depart_date DATE NOT NULL,
  status ENUM('PLANNED', 'OPEN', 'DEPARTED', 'ARRIVED', 'CANCELLED') NOT NULL DEFAULT 'PLANNED',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_schedule_train FOREIGN KEY (train_id)
    REFERENCES train (train_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT uq_schedule_train_date UNIQUE (train_id, depart_date)
) ENGINE = InnoDB;

CREATE TABLE schedule_stop (
  stop_id INT PRIMARY KEY AUTO_INCREMENT,
  schedule_id INT NOT NULL,
  station_id INT NOT NULL,
  seq_no INT NOT NULL,
  arrival_time TIME NULL,
  departure_time TIME NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_stop_schedule FOREIGN KEY (schedule_id)
    REFERENCES train_schedule (schedule_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_stop_station FOREIGN KEY (station_id)
    REFERENCES station (station_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT uq_stop_sequence UNIQUE (schedule_id, seq_no),
  CONSTRAINT uq_stop_station UNIQUE (schedule_id, station_id)
) ENGINE = InnoDB;

CREATE TABLE coach (
  coach_id INT PRIMARY KEY AUTO_INCREMENT,
  schedule_id INT NOT NULL,
  coach_no VARCHAR(16) NOT NULL,
  coach_type VARCHAR(32) NOT NULL,
  seat_count INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_coach_schedule FOREIGN KEY (schedule_id)
    REFERENCES train_schedule (schedule_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT uq_coach_schedule UNIQUE (schedule_id, coach_no)
) ENGINE = InnoDB;

CREATE TABLE seat (
  seat_id INT PRIMARY KEY AUTO_INCREMENT,
  coach_id INT NOT NULL,
  seat_no VARCHAR(16) NOT NULL,
  seat_class VARCHAR(32) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_seat_coach FOREIGN KEY (coach_id)
    REFERENCES coach (coach_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT uq_seat_coach UNIQUE (coach_id, seat_no)
) ENGINE = InnoDB;

CREATE TABLE user_account (
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  id_number VARCHAR(32) NOT NULL UNIQUE,
  phone VARCHAR(32) NOT NULL,
  email VARCHAR(128) NULL,
  password_hash VARCHAR(255) NOT NULL,
  role ENUM('USER', 'ADMIN', 'EMPLOYEE') NOT NULL DEFAULT 'USER',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB;

CREATE TABLE booking_order (
  order_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status ENUM('PENDING', 'PAID', 'CONFIRMED', 'CANCELLED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
  total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
  CONSTRAINT fk_order_user FOREIGN KEY (user_id)
    REFERENCES user_account (user_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  INDEX idx_order_user (user_id)
) ENGINE = InnoDB;

CREATE TABLE seat_allocation (
  allocation_id INT PRIMARY KEY AUTO_INCREMENT,
  schedule_id INT NOT NULL,
  seat_id INT NOT NULL,
  status ENUM('AVAILABLE', 'LOCKED', 'RESERVED', 'SOLD') NOT NULL DEFAULT 'AVAILABLE',
  reserved_by_order INT NULL,
  reserved_until TIMESTAMP NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_allocation_schedule FOREIGN KEY (schedule_id)
    REFERENCES train_schedule (schedule_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_allocation_seat FOREIGN KEY (seat_id)
    REFERENCES seat (seat_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_allocation_order FOREIGN KEY (reserved_by_order)
    REFERENCES booking_order (order_id)
    ON UPDATE CASCADE
    ON DELETE SET NULL,
  CONSTRAINT uq_allocation UNIQUE (schedule_id, seat_id),
  INDEX idx_allocation_status (status)
) ENGINE = InnoDB;

CREATE TABLE ticket (
  ticket_id INT PRIMARY KEY AUTO_INCREMENT,
  order_id INT NOT NULL,
  schedule_id INT NOT NULL,
  seat_id INT NOT NULL,
  passenger_name VARCHAR(128) NOT NULL,
  seat_status ENUM('ACTIVE', 'CANCELLED', 'CHECKED_IN') NOT NULL DEFAULT 'ACTIVE',
  price DECIMAL(10, 2) NOT NULL,
  issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ticket_order FOREIGN KEY (order_id)
    REFERENCES booking_order (order_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_ticket_schedule FOREIGN KEY (schedule_id)
    REFERENCES train_schedule (schedule_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_ticket_seat FOREIGN KEY (seat_id)
    REFERENCES seat (seat_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  INDEX idx_ticket_schedule (schedule_id),
  INDEX idx_ticket_seat (seat_id)
) ENGINE = InnoDB;

CREATE TABLE payment (
  payment_id INT PRIMARY KEY AUTO_INCREMENT,
  order_id INT NOT NULL,
  amount DECIMAL(10, 2) NOT NULL,
  status ENUM('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
  gateway_txn_id VARCHAR(64) NULL,
  paid_at TIMESTAMP NULL,
  CONSTRAINT fk_payment_order FOREIGN KEY (order_id)
    REFERENCES booking_order (order_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  INDEX idx_payment_order (order_id)
) ENGINE = InnoDB;

CREATE TABLE employee (
  employee_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  role VARCHAR(64) NOT NULL,
  station_id INT NOT NULL,
  user_id INT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_employee_station FOREIGN KEY (station_id)
    REFERENCES station (station_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_employee_user FOREIGN KEY (user_id)
    REFERENCES user_account (user_id)
    ON UPDATE CASCADE
    ON DELETE SET NULL
) ENGINE = InnoDB;

CREATE TABLE checkin (
  checkin_id INT PRIMARY KEY AUTO_INCREMENT,
  ticket_id INT NOT NULL,
  employee_id INT NOT NULL,
  station_id INT NOT NULL,
  checkin_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  checkin_type ENUM('ENTRY', 'EXIT') NOT NULL,
  CONSTRAINT fk_checkin_ticket FOREIGN KEY (ticket_id)
    REFERENCES ticket (ticket_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_checkin_employee FOREIGN KEY (employee_id)
    REFERENCES employee (employee_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_checkin_station FOREIGN KEY (station_id)
    REFERENCES station (station_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  INDEX idx_checkin_ticket (ticket_id)
) ENGINE = InnoDB;

CREATE TABLE ticket_listing (
  listing_id INT PRIMARY KEY AUTO_INCREMENT,
  seller_id INT NOT NULL,
  order_id INT NOT NULL,
  price DECIMAL(10, 2) NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_listing_seller FOREIGN KEY (seller_id)
    REFERENCES user_account (user_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_listing_order FOREIGN KEY (order_id)
    REFERENCES booking_order (order_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE = InnoDB;
