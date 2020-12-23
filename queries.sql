INSERT INTO `users` (`activated`,`username`,`password`,`email`,`role`,`updated`,`created`)
VALUES (1, 'admin', '-', 'admin@myalerts.org', 'ROLE_ADMIN', now(), now());

INSERT INTO `users` (`activated`,`username`,`password`,`email`,`role`,`updated`,`created`)
VALUES (2, 'user', '-', 'user@myalerts.org', 'ROLE_USER', now(), now());