ALTER TABLE "total_execution_time" DROP COLUMN max_duration;

ALTER TABLE "total_execution_time"
    ADD COLUMN max_duration INTEGER;