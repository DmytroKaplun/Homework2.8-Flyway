INSERT INTO worker(name, birthday, level, salary)
VALUES
    ('John Doe', '1990-05-15', 'Trainee', 800),
    ('Jane Smith', '1985-08-20', 'Junior', 1200),
    ('Michael Johnson', '1982-11-10', 'Middle', 2000),
    ('Emily Brown', '1978-03-25', 'Senior', 5500),
    ('David Lee', '1995-09-08', 'Trainee', 850),
    ('Sarah Wilson', '1992-07-12', 'Junior', 1300),
    ('Daniel Martinez', '1988-01-30', 'Middle', 2200),
    ('Jessica Taylor', '1980-06-18', 'Senior', 5800),
    ('Christopher Anderson', '1993-04-05', 'Junior', 1250),
    ('Ashley Garcia', '1987-10-22', 'Middle', 2100);

INSERT INTO client(name)
VALUES
    ('client A'),
	('client B'),
	('client C'),
    ('client D'),
    ('client E');

INSERT INTO project (client_id, start_date, finish_date)
VALUES
    (1, '2022-01-01', '2022-03-31'),
    (1, '2022-02-01', '2022-05-31'),
    (1, '2022-03-01', '2022-06-30'),
    (2, '2022-04-01', '2022-08-31'),
    (5, '2022-05-01', '2022-07-31'),
    (4, '2022-06-01', '2022-09-30'),
    (2, '2022-07-01', '2022-10-31'),
    (3, '2022-08-01', '2022-11-30'),
    (4, '2022-09-01', '2023-01-31'),
    (5, '2022-10-01', '2023-02-28');

INSERT INTO project_worker(project_id, worker_id)
VALUES
    (1, 2),
    (2, 3),
    (2, 4),
    (3, 5),
    (3, 6),
    (4, 7),
    (4, 8),
    (5, 9),
    (5, 10),
    (6, 1),
    (6, 3),
    (7, 2),
    (7, 4),
    (8, 5),
	(8, 6),
    (9, 7),
    (9, 8),
    (10, 9),
    (10, 10);