.PHONY: run test db-up db-down docker-up docker-down docker-logs

run: db-up
	./mvnw spring-boot:run

test:
	./mvnw test

db-up:
	docker compose up -d postgres

db-down:
	docker compose stop postgres

docker-up:
	docker compose up --build -d

docker-down:
	docker compose down

docker-logs:
	docker compose logs -f payment-service
