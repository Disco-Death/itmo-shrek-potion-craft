INSERT INTO ingredients VALUES
(1, 'Чешуя дракона', 'Огнестойкая'),
(2, 'Пыльца феи', 'Усыпляет');

INSERT INTO task (description, user_id) VALUES
     ('Задача 1', 1),
     ('Задача 2', 1),
     ('Задача 3', 1);

UPDATE user_role
SET roles = 'SALES_DEPT'
WHERE user_id = 2;