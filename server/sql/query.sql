-- name: SelectLatestMeasurement :one 
SELECT MAX(timestamp), value FROM measurements LIMIT 1;

-- name: SelectAllMeasurements :many
SELECT * FROM measurements;
