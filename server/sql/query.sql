-- name: SelectLatestMeasurement :one 
SELECT * FROM measurements
ORDER BY timestamp DESC 
LIMIT 1;

-- name: SelectAllMeasurements :many
SELECT * FROM measurements;

-- name: AddMeasurement :exec
INSERT INTO measurements (temperature, humidity)
VALUES (?, ?);

-- name: SelectRangeMeasurements :many
SELECT * FROM measurements
WHERE timestamp > sqlc.arg(from)
AND timestamp < sqlc.arg(to);


