-- ============================================
-- 数据查询与统计系统 - 数据库初始化SQL脚本
-- CITEL-T-003
-- ============================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS travel_stats
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE travel_stats;

-- ============================================
-- 表1: 人员出行数据表
-- ============================================
CREATE TABLE IF NOT EXISTS person_travel_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    person_id BIGINT NOT NULL COMMENT '人员ID',
    gender TINYINT NOT NULL COMMENT '性别: 0-女, 1-男',
    birth_year INT NOT NULL COMMENT '出生年份',
    total_mileage BIGINT NOT NULL COMMENT '总旅行里程',
    total_travel_time BIGINT NOT NULL COMMENT '总旅行时间(分钟)',
    INDEX idx_birth_year (birth_year),
    INDEX idx_mileage (total_mileage),
    INDEX idx_travel_time (total_travel_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员出行数据表';

-- ============================================
-- 表2: 查询区间配置表
-- ============================================
CREATE TABLE IF NOT EXISTS query_range_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    config_name VARCHAR(100) NOT NULL COMMENT '配置名称',
    query_type VARCHAR(20) NOT NULL COMMENT '查询类型: AGE-年龄, MILEAGE-里程, TIME-时间',
    range_data JSON NOT NULL COMMENT '区间数据(JSON): [{"label":"区间1","min":10,"max":20},...]',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_query_type (query_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='查询区间配置表';
