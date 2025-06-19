-- name: SelectLatestMeasurement :one 
SELECT * FROM measurements
ORDER BY timestamp DESC 
LIMIT 1;

-- name: SelectAllMeasurements :many
SELECT * FROM measurements;

-- name: AddMeasurement :exec
INSERT INTO measurements (temperature, humidity)
VALUES (?, ?);




