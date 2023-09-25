CREATE TABLE lambda_execution_time (
    id         SERIAL PRIMARY KEY,
    execution_date TIMESTAMP NOT NULL,
    duration   INTEGER,
    lambda_id  INTEGER   NOT NULL,
    CONSTRAINT fk_execution_lambda
        FOREIGN KEY (lambda_id)
            REFERENCES lambda (id)
            ON DELETE CASCADE
);