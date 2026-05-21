INSERT INTO category(name)
VALUES ('Eurogames');
INSERT INTO category(name)
VALUES ('Ameritrash');
INSERT INTO category(name)
VALUES ('Familiar');

INSERT INTO client(name)
VALUES ('Ana Lopez');
INSERT INTO client(name)
VALUES ('Carlos Martin');
INSERT INTO client(name)
VALUES ('Lucia Gomez');

INSERT INTO author(name, nationality)
VALUES ('Alan R. Moon', 'US');
INSERT INTO author(name, nationality)
VALUES ('Vital Lacerda', 'PT');
INSERT INTO author(name, nationality)
VALUES ('Simone Luciani', 'IT');
INSERT INTO author(name, nationality)
VALUES ('Perepau Llistosella', 'ES');
INSERT INTO author(name, nationality)
VALUES ('Michael Kiesling', 'DE');
INSERT INTO author(name, nationality)
VALUES ('Phil Walker-Harding', 'US');

INSERT INTO game(title, age, category_id, author_id)
VALUES ('Aventureros', '12', 3, 1);
INSERT INTO game(title, age, category_id, author_id)
VALUES ('Lisboa', '14', 1, 2);
INSERT INTO game(title, age, category_id, author_id)
VALUES ('Barrage', '14', 1, 3);
INSERT INTO game(title, age, category_id, author_id)
VALUES ('Carcassonne', '8', 3, 4);
INSERT INTO game(title, age, category_id, author_id)
VALUES ('Azul', '8', 2, 5);
INSERT INTO game(title, age, category_id, author_id)
VALUES ('Sushi Go', '8', 2, 6);
