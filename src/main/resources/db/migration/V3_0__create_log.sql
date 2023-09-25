CREATE TABLE log (
   id SERIAL PRIMARY KEY,
   start_time TIMESTAMP NOT NULL,
   end_time TIMESTAMP,
   lambda_id INTEGER NOT NULL,
   CONSTRAINT fk_log_lambda
   FOREIGN KEY (lambda_id)
   REFERENCES lambda(id)
   ON DELETE CASCADE
);