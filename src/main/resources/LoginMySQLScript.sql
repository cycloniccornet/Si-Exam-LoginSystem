# CREATE USER 'test1234'@'localhost' IDENTIFIED BY 'test1234';
# GRANT ALL PRIVILEGES ON Users.* TO 'test1234'@'localhost';
# drop table users;

CREATE schema IF NOT EXISTS users;

USE users;

CREATE TABLE IF NOT EXISTS users
(
	user_id		    INT		   		PRIMARY KEY		AUTO_INCREMENT,
	mail            VARCHAR(200)    NOT NULL,
	name            VARCHAR(200)    NOT NULL,
    username		VARCHAR(100)	NOT NULL,
    password		VARCHAR(100)	NOT NULL
);

INSERT INTO users
(mail, name, username, password)
VALUES
    ('patr180499@gmail.com', 'Patrick Jønnson', 'Patrick', '$2a$10$XpT/dJU7HkRNu6k3FfJW7eAPhlC6VHf77WcBTCTBeDT6kGJFednky'), # username : "Patrick" | Password : "password"
    ('martin-holmqvist@hotmail.com', 'Martin Holmqvist', 'Martin', '$2a$10$sCLX1Ph60LlD/eKux6gZvOCibkD7k1hGRqqs6srWdmPIEnXC5RBSy');  # username : "Patrick" | Password : "secret"

select * from users;
