DROP IF EXISTS TABLE usersAndGroups;
DROP IF EXISTS TABLE usersAndBills;
DROP IF EXISTS TABLE bills;
DROP IF EXISTS TABLE groups;
DROP IF EXISTS TABLE users;


CREATE TABLE groups (
	name varchar(50) NOT NULL,
	PRIMARY KEY (name)
);


CREATE TABLE users (
	email varchar(50) NOT NULL,
	password varchar(50) NOT NULL,
	PRIMARY KEY (email)
);

CREATE TABLE bills (
	id varchar(50) NOT NULL,
	value float NOT NULL,
	dateOcurred date DEFAULT NULL,
	location varchar(50) DEFAULT NULL,
	picture varbinary(1024) DEFAULT NULL,
	gid varchar(50) NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (gid) REFERENCES groups(name) ON UPDATE CASCADE
);

CREATE TABLE usersAndGroups (
	uid varchar(50) NOT NULL,
	gid varchar(50) NOT NULL,
	PRIMARY KEY (uid, gid),
	FOREIGN KEY (uid) REFERENCES users(email) ON UPDATE CASCADE,
	FOREIGN KEY (gid) REFERENCES groups(name) ON UPDATE CASCADE
);

CREATE TABLE usersAndBills (
	uid varchar(50) NOT NULL,
	bid varchar(50) NOT NULL,
	value FLOAT NOT NULL,
	PRIMARY KEY (uid, bid),
	FOREIGN KEY (uid) REFERENCES users(email) ON UPDATE CASCADE,
	FOREIGN KEY (bid) REFERENCES bills(id) ON UPDATE CASCADE ON DELETE CASCADE
);
