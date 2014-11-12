
INSERT INTO users VALUES ('user1', '1234');
INSERT INTO users VALUES ('user2', '1234');

INSERT INTO groups VALUES ('group1');

--	id value dateOcurred location picture gid
INSERT INTO bills VALUES ('bgroup11', 50.00, '', '', '', 'group1');

INSERT INTO usersAndGroups VALUES ('user1', 'group1');
INSERT INTO usersAndGroups VALUES ('user2', 'group1');

--	uid, bid, value
INSERT INTO usersAndBills VALUES ('user1','bgroup11', 50.00);
INSERT INTO usersAndBills VALUES ('user2','bgroup11', -50.00);