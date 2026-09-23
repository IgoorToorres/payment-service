.PHONY: run test db-up db-down

run: db-up
	./mvnw spring-boot:run

test:
	./mvnw test

db-up:
	docker compose up -d postgres

db-down:
	docker compose down
