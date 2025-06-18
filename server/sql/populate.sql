BEGIN TRANSACTION;

-- Use a recursive Common Table Expression (CTE) to generate a sequence of numbers from 0 to 19.
-- This sequence will be used to calculate the timestamps for each of the 20 entries.
WITH RECURSIVE
  time_series(n) AS (
    -- Base case: Start with the first value (0)
    SELECT 0
    UNION ALL
    -- Recursive step: Increment n by 1 until it reaches 19 (for a total of 20 values)
    SELECT n + 1
    FROM time_series
    WHERE n < 99
  )

-- Insert the generated data into the measurements table.
INSERT INTO measurements (timestamp, temperature, humidity)
SELECT
  DATETIME('now', '+' || (n * 5) || ' minutes'),
  18.0 + (ABS(RANDOM()) * 1.0 / 9223372036854775807.0) * (36.0 - 18.0),
  20.0 + (ABS(RANDOM()) * 1.0 / 9223372036854775807.0) * (90.0 - 20.0)
FROM time_series;

-- Commit the transaction, making all inserts permanent in the database.
COMMIT;
