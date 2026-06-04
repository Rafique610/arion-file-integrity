CREATE DATABASE file_integrity_system;
USE file_integrity_system;

CREATE TABLE users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(64) NOT NULL,
  role ENUM('Admin','Analyst','User') DEFAULT 'User',
  full_name VARCHAR(100),
  phone VARCHAR(20),
  is_active BOOLEAN DEFAULT TRUE,
  created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_login TIMESTAMP NULL
);

CREATE TABLE files (
  file_id INT AUTO_INCREMENT PRIMARY KEY,
  file_name VARCHAR(255) NOT NULL,
  file_path VARCHAR(500),
  file_size BIGINT,
  hash_value VARCHAR(128),
  hash_algorithm VARCHAR(20),
  uploaded_by INT,
  status VARCHAR(20) DEFAULT 'Protected',
  upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_verified TIMESTAMP NULL,
  FOREIGN KEY (uploaded_by) REFERENCES users(user_id)
);

CREATE TABLE audit_logs (
  log_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT,
  action VARCHAR(100),
  target_type VARCHAR(50),
  target_id INT,
  ip_address VARCHAR(45),
  details TEXT,
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE file_upload_history (
  history_id INT AUTO_INCREMENT PRIMARY KEY,
  file_id INT,
  user_id INT,
  action_type VARCHAR(50),
  action_status VARCHAR(20),
  hash_value VARCHAR(128),
  algorithm_used VARCHAR(20),
  details TEXT,
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (file_id) REFERENCES files(file_id),
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE file_integrity_checks (
  check_id INT AUTO_INCREMENT PRIMARY KEY,
  file_id INT,
  checked_by INT,
  previous_hash VARCHAR(128),
  current_hash VARCHAR(128),
  check_result VARCHAR(20),
  check_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (file_id) REFERENCES files(file_id),
  FOREIGN KEY (checked_by) REFERENCES users(user_id)
);

CREATE TABLE security_reports (
  report_id INT AUTO_INCREMENT PRIMARY KEY,
  report_name VARCHAR(200),
  report_type VARCHAR(50),
  report_date DATE,
  status VARCHAR(20) DEFAULT 'New',
  content TEXT,
  generated_by INT,
  reviewed_by INT NULL,
  reviewed_date TIMESTAMP NULL,
  created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  notes TEXT,
  FOREIGN KEY (generated_by) REFERENCES users(user_id),
  FOREIGN KEY (reviewed_by) REFERENCES users(user_id)
);

CREATE TABLE attack_simulations (
  simulation_id INT AUTO_INCREMENT PRIMARY KEY,
  attack_type VARCHAR(100),
  target_file VARCHAR(255),
  intensity_level INT,
  detection_time DOUBLE DEFAULT 0,
  system_response VARCHAR(50),
  affected_files INT DEFAULT 0,
  alerts_triggered INT DEFAULT 0,
  success_rate DOUBLE DEFAULT 0,
  total_duration INT DEFAULT 0,
  simulation_log TEXT,
  started_by INT,
  start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  end_time TIMESTAMP NULL,
  FOREIGN KEY (started_by) REFERENCES users(user_id)
);

-- Insert a test user (password = "password123")
INSERT INTO users (username, email, password_hash, role, full_name, is_active)
VALUES ('testuser', 'test@arion.com',
'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f',
'User', 'Test User', TRUE);