-- 修复 admin 密码：确保 admin 账号密码为 admin123 (BCrypt)
-- 此脚本在每次容器启动时都可安全重复执行
UPDATE sys_user
SET password_hash = '$2a$10$WFhI3yqqRL4ngA9ZSYnOP.USIo355xFs1rxRPszVAiif9x8WbeQ.6',
    login_attempts = 0,
    lock_until = NULL
WHERE username = 'admin';
