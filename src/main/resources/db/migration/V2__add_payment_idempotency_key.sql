ALTER TABLE payments
    ADD COLUMN idempotency_key VARCHAR(100);

UPDATE payments
SET idempotency_key = 'legacy-' || id
WHERE idempotency_key IS NULL;

ALTER TABLE payments
    ALTER COLUMN idempotency_key SET NOT NULL;

ALTER TABLE payments
    ADD CONSTRAINT uk_payments_idempotency_key
        UNIQUE (idempotency_key);
