-- seed_data.sql
-- Populates the railway booking schema with baseline reference data for local development.

USE `12306`;

START TRANSACTION;

INSERT INTO user_account (user_id, name, password_hash, id_number, phone, role) VALUES
  (1, 'admin', 'password', 'ADMIN001', '13800000000', 'ADMIN'),
  (2, 'alice', 'password', 'USER001', '13900000000', 'USER'),
  (3, 'carol', 'password', 'EMP001', '13700000000', 'EMPLOYEE')
ON DUPLICATE KEY UPDATE name = VALUES(name), password_hash = VALUES(password_hash), role = VALUES(role);

INSERT INTO station (station_id, code, name, city) VALUES
  (1, 'SHG', 'Shanghai South', 'Shanghai'),
  (2, 'BJP', 'Beijing Central', 'Beijing'),
  (3, 'NJH', 'Nanjing Hub', 'Nanjing')
ON DUPLICATE KEY UPDATE name = VALUES(name), city = VALUES(city);

INSERT INTO train (train_id, train_no, train_type) VALUES
  (1, 'G1001', 'HIGH_SPEED'),
  (2, 'K88', 'EXPRESS')
ON DUPLICATE KEY UPDATE train_type = VALUES(train_type);

INSERT INTO train_schedule (schedule_id, train_id, depart_date, status) VALUES
  (1, 1, '2025-12-20', 'OPEN'),
  (2, 1, '2025-12-21', 'PLANNED'),
  (3, 2, '2025-12-20', 'OPEN')
ON DUPLICATE KEY UPDATE status = VALUES(status);

INSERT INTO schedule_stop (stop_id, schedule_id, station_id, seq_no, arrival_time, departure_time) VALUES
  (1, 1, 1, 1, NULL, '08:00:00'),
  (2, 1, 3, 2, '09:10:00', '09:15:00'),
  (3, 1, 2, 3, '11:30:00', NULL),
  (4, 3, 3, 1, NULL, '06:30:00'),
  (5, 3, 1, 2, '08:45:00', '08:55:00'),
  (6, 3, 2, 3, '12:30:00', NULL)
ON DUPLICATE KEY UPDATE arrival_time = VALUES(arrival_time), departure_time = VALUES(departure_time);

INSERT INTO coach (coach_id, schedule_id, coach_no, coach_type, seat_count) VALUES
  (1, 1, 'C1', 'FIRST_CLASS', 20),
  (2, 1, 'C2', 'SECOND_CLASS', 40),
  (3, 3, 'B1', 'SLEEPER', 30)
ON DUPLICATE KEY UPDATE coach_type = VALUES(coach_type), seat_count = VALUES(seat_count);

INSERT INTO seat (seat_id, coach_id, seat_no, seat_class) VALUES
  (1, 1, '1A', 'PREMIUM'),
  (2, 1, '1B', 'PREMIUM'),
  (3, 2, '5A', 'STANDARD'),
  (4, 2, '5B', 'STANDARD'),
  (5, 2, '6A', 'STANDARD'),
  (6, 3, 'S1', 'BERTH'),
  (7, 3, 'S2', 'BERTH')
ON DUPLICATE KEY UPDATE seat_class = VALUES(seat_class);

INSERT INTO user_account (user_id, name, id_number, phone, email, password_hash, role) VALUES
  (1, 'Alice Chen', 'ID1234567890', '13800000001', 'alice@example.com', '$2a$10$w7AdummyHashForDemoPassword1234567890abcdefghi', 'USER'),
  (2, 'Bob Li', 'ID0987654321', '13800000002', 'bob@example.com', '$2a$10$w7AdummyHashForDemoPassword1234567890abcdefghi', 'ADMIN'),
  (3, 'Carol Wang', 'ID5555555555', '13800000003', 'carol@example.com', '$2a$10$w7AdummyHashForDemoPassword1234567890abcdefghi', 'EMPLOYEE')
ON DUPLICATE KEY UPDATE phone = VALUES(phone), email = VALUES(email), role = VALUES(role);

INSERT INTO booking_order (order_id, user_id, created_at, status, total_amount) VALUES
  (1, 1, '2025-12-10 09:30:00', 'CONFIRMED', 560.00),
  (2, 1, '2025-12-11 14:20:00', 'PENDING', 280.00)
ON DUPLICATE KEY UPDATE status = VALUES(status), total_amount = VALUES(total_amount);

INSERT INTO seat_allocation (allocation_id, schedule_id, seat_id, status, reserved_by_order, reserved_until) VALUES
  (1, 1, 1, 'SOLD', 1, NULL),
  (2, 1, 2, 'RESERVED', 2, '2025-12-11 15:20:00'),
  (3, 1, 3, 'SOLD', 1, NULL),
  (4, 1, 4, 'AVAILABLE', NULL, NULL),
  (5, 3, 6, 'AVAILABLE', NULL, NULL)
ON DUPLICATE KEY UPDATE status = VALUES(status), reserved_by_order = VALUES(reserved_by_order), reserved_until = VALUES(reserved_until);

INSERT INTO ticket (ticket_id, order_id, schedule_id, seat_id, passenger_name, seat_status, price, issued_at) VALUES
  (1, 1, 1, 1, 'Alice Chen', 'CHECKED_IN', 260.00, '2025-12-10 09:35:00'),
  (2, 1, 1, 3, 'Alice Chen', 'ACTIVE', 300.00, '2025-12-10 09:36:00')
ON DUPLICATE KEY UPDATE seat_status = VALUES(seat_status), price = VALUES(price);

INSERT INTO payment (payment_id, order_id, amount, status, gateway_txn_id, paid_at) VALUES
  (1, 1, 560.00, 'SUCCESS', 'PAY202512100001', '2025-12-10 09:40:00'),
  (2, 2, 280.00, 'PENDING', NULL, NULL)
ON DUPLICATE KEY UPDATE amount = VALUES(amount), status = VALUES(status), gateway_txn_id = VALUES(gateway_txn_id), paid_at = VALUES(paid_at);

INSERT INTO employee (employee_id, name, role, station_id, user_id) VALUES
  (1, 'Carol Wang', 'TICKET_INSPECTOR', 1, 3)
ON DUPLICATE KEY UPDATE role = VALUES(role), station_id = VALUES(station_id), user_id = VALUES(user_id);

INSERT INTO checkin (checkin_id, ticket_id, employee_id, station_id, checkin_time, checkin_type) VALUES
  (1, 1, 1, 1, '2025-12-20 07:40:00', 'ENTRY')
ON DUPLICATE KEY UPDATE checkin_time = VALUES(checkin_time), checkin_type = VALUES(checkin_type);

INSERT INTO ticket_listing (listing_id, order_id, seller_id, price, status, created_at) VALUES
  (1, 1, 2, 280.00, 'ACTIVE', '2025-12-12 10:00:00')
ON DUPLICATE KEY UPDATE price = VALUES(price), status = VALUES(status);

COMMIT;
