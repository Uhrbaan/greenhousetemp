CREATE TABLE measurements (
    timestamp DATETIME PRIMARY KEY DEFAULT (DATETIME('now', 'localtime')),
    value FLOAT NOT NULL
);
