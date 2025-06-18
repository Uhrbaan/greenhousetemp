ALTER TABLE measurements RENAME COLUMN value TO temperature;
ALTER TABLE measurements ADD humidity FLOAT NOT NULL DEFAULT 0.5;
