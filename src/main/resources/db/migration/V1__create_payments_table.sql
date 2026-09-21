CREATE TABLE payments
(
    id                 UUID PRIMARY KEY,
    amount             NUMERIC(19, 2)           NOT NULL,
    currency           VARCHAR(3)               NOT NULL,
    payment_method     VARCHAR(30)              NOT NULL,
    external_reference VARCHAR(100)             NOT NULL,
    status             VARCHAR(30)              NOT NULL,
    created_at         TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT ck_payments_amount_positive
        CHECK (amount > 0)
);
