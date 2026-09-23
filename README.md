# Sistema de Pagamentos e Reconciliação

Backend distribuído desenvolvido em **Java + Spring Boot** que simula uma plataforma real de processamento de pagamentos, comunicação com um provedor financeiro externo, registro contábil, mensageria assíncrona e reconciliação financeira.

O objetivo deste projeto é estudar e aplicar tecnologias e padrões amplamente utilizados em sistemas Java Backend modernos, construindo uma aplicação com características próximas de um ambiente real de produção.

> Este projeto também deve funcionar como um projeto educacional. Qualquer IA utilizada durante o desenvolvimento deverá atuar como **professor/tutor**, explicando conceitos, decisões arquiteturais, código, erros e tecnologias passo a passo, em vez de simplesmente gerar implementações completas sem contexto.

---

## Autor

**Igor Torres**  
GitHub: **IgoorToorres**

Identidade Maven/package sugerida:

```text
Group: io.github.igoortoorres
Artifact: payment-service
Package: io.github.igoortoorres.paymentservice
```

> Observação: o nome do usuário no GitHub mantém a grafia `IgoorToorres`, mas packages Java devem ser escritos em letras minúsculas.

---

# 1. Objetivos do projeto

Este projeto possui dois objetivos principais.

## Objetivo técnico

Construir um backend que demonstre conhecimento prático em:

- Java 21
- Spring Boot
- APIs REST
- PostgreSQL
- JPA / Hibernate
- Flyway
- Apache Kafka
- RabbitMQ
- Redis
- Docker
- Docker Compose
- Spring Security
- JWT / OAuth2
- JUnit
- Mockito
- Testcontainers
- WireMock
- Resilience4j
- OpenAPI / Swagger
- GitHub Actions
- AWS
- Prometheus
- Grafana
- OpenTelemetry

Além das tecnologias, deverão ser estudados conceitos importantes de arquitetura:

- sistemas distribuídos;
- arquitetura orientada a eventos;
- comunicação síncrona e assíncrona;
- idempotência;
- consistência eventual;
- retry;
- Dead Letter Queue;
- transactional outbox;
- Saga;
- circuit breaker;
- observabilidade;
- tracing distribuído;
- concorrência;
- segurança;
- testes de integração;
- CI/CD.

---

# 2. Objetivo educacional

Este projeto NÃO deve ser desenvolvido apenas com objetivo de "fazer funcionar".

Cada etapa deve ser compreendida.

Durante o desenvolvimento, a IA deverá constantemente explicar:

- o que estamos construindo;
- por que estamos construindo daquela maneira;
- qual problema aquela tecnologia resolve;
- como aquela tecnologia funciona internamente;
- quais alternativas existiriam;
- quais trade-offs existem;
- como isso seria usado em uma empresa real;
- como testar se a implementação está funcionando;
- quais erros podem acontecer;
- como diagnosticar esses erros;
- como explicar aquela implementação durante uma entrevista técnica.

O desenvolvedor deste projeto está aprendendo algumas tecnologias pela primeira vez.

Principalmente:

**Apache Kafka**

e

**RabbitMQ**

Portanto, essas tecnologias devem ser ensinadas desde os fundamentos.

Nenhuma familiaridade prévia com Kafka ou RabbitMQ deve ser presumida.

---

# 3. Instruções obrigatórias para a IA

Qualquer IA utilizada para auxiliar neste projeto deve seguir estas regras.

## 3.1 Não entregar tudo pronto

Evitar gerar grandes quantidades de código de uma única vez.

O desenvolvimento deve acontecer incrementalmente.

Preferência:

```text
explicação
↓
pequena implementação
↓
executar
↓
testar
↓
entender resultado
↓
próxima implementação
```

Nunca:

```text
"Aqui estão 30 arquivos completos. Copie tudo."
```

## 3.2 Antes de escrever código

Sempre explicar:

1. O que vamos desenvolver.
2. Por que precisamos disso.
3. Qual conceito está sendo estudado.
4. Como essa parte se encaixa na arquitetura.
5. Qual será o resultado esperado.

Somente depois começar a implementação.

## 3.3 Ao apresentar código

Explicar as partes importantes.

Principalmente quando aparecer algo novo como:

```java
@Transactional
@Entity
@KafkaListener
@RabbitListener
@Configuration
@Bean
@Component
@Service
@Repository
```

Não apenas dizer o que escrever.

Explicar:

- O que essa anotação faz?
- Quem executa isso?
- Quando ela é executada?
- Por que precisamos dela?
- O que aconteceria se ela não existisse?

---

# 4. Regra especial para Kafka

Kafka é uma tecnologia nova para o desenvolvedor.

Antes da primeira implementação com Kafka, a IA deverá ensinar os seguintes conceitos:

- o que é Apache Kafka;
- o que é um broker;
- o que é um cluster Kafka;
- producer;
- consumer;
- tópico;
- evento;
- partition;
- offset;
- consumer group;
- retention;
- ordering;
- replication;
- acknowledgement;
- delivery semantics;
- at-most-once;
- at-least-once;
- exactly-once;
- serialização;
- deserialização;
- Schema Evolution;
- retry;
- Dead Letter Topic;
- consumer lag.

A IA deve utilizar exemplos do próprio Sistema de Pagamentos.

Exemplo:

```text
Pagamento 9182 foi aprovado
```

poderá gerar:

```text
payment.authorized.v1
```

Fluxo:

```text
Payment Service
        │
        │ publica evento
        ▼
      Kafka
        │
        ├────────► Ledger Service
        ├────────► Reconciliation Service
        └────────► Analytics
```

Não iniciar Kafka apenas ensinando configuração.

Primeiro explicar o problema que Kafka resolve.

---

# 5. Regra especial para RabbitMQ

RabbitMQ também é uma tecnologia nova para o desenvolvedor.

Antes da primeira implementação, explicar:

- o que é um message broker;
- queue;
- producer;
- consumer;
- exchange;
- binding;
- routing key;
- acknowledgement;
- ACK;
- NACK;
- prefetch;
- retry;
- Dead Letter Exchange;
- Dead Letter Queue;
- durable queue;
- persistent message;
- competing consumers;
- diferença entre fila e evento.

A IA deverá mostrar claramente a diferença entre Kafka e RabbitMQ.

Neste projeto:

## Kafka

Será utilizado principalmente para **eventos de domínio**.

Exemplo:

```text
payment.created
payment.authorized
payment.failed
payment.settled
```

São fatos que já aconteceram.

## RabbitMQ

Será utilizado principalmente para **tarefas/comandos que precisam ser executados**.

Exemplo:

```text
process-payment
send-webhook
send-notification
retry-provider
```

Exemplo conceitual:

```text
Kafka

"Pagamento 123 foi aprovado."
```

versus:

```text
RabbitMQ

"Envie o webhook do pagamento 123."
```

Essa distinção deverá ser mantida durante todo o projeto.

---

# 6. Domínio do sistema

O sistema simulará uma empresa intermediadora de pagamentos.

Um cliente enviará uma requisição:

```http
POST /api/payments
```

Exemplo:

```json
{
  "amount": 199.90,
  "currency": "BRL",
  "paymentMethod": "PIX",
  "externalReference": "ORDER-92831"
}
```

O sistema deverá:

```text
Cliente
   │
   ▼
Payment API
   │
   ▼
Registrar pagamento
   │
   ▼
Solicitar processamento
   │
   ▼
Provedor financeiro simulado
   │
   ├── aprovado
   ├── recusado
   ├── timeout
   └── erro
   │
   ▼
Atualizar pagamento
   │
   ▼
Publicar eventos
```

---

# 7. Estados de um pagamento

```text
CREATED
   ↓
PROCESSING
   ↓
AUTHORIZED
   ↓
SETTLED
```

Ou:

```text
PROCESSING
   ↓
DECLINED
```

Falha técnica:

```text
PROCESSING
   ↓
FAILED
```

Possível evolução futura:

```text
AUTHORIZED
   ↓
REFUNDED
```

---

# 8. Arquitetura final desejada

A arquitetura será construída gradualmente.

```text
                           ┌─────────────────┐
                           │     Cliente     │
                           └────────┬────────┘
                                    │
                                    │ REST
                                    ▼
                         ┌────────────────────┐
                         │  Payment Service   │
                         └──────┬───────┬─────┘
                                │       │
                       PostgreSQL       │
                                        │
                    ┌───────────────────┴───────────────────┐
                    │                                       │
                    ▼                                       ▼
               RabbitMQ                                   Kafka
                    │                                       │
             process-payment                    payment.created
             webhook.dispatch                   payment.authorized
             notification                       payment.failed
                    │                            payment.settled
                    │                                       │
                    ▼                              ┌────────┴────────┐
          ┌──────────────────┐                     │                 │
          │ Payment Worker   │                     ▼                 ▼
          └────────┬─────────┘              Ledger Service   Reconciliation
                   │
                   ▼
       Payment Provider Simulator
```

---

# 9. Serviços planejados

O projeto NÃO deve começar com todos os serviços simultaneamente.

Eles serão adicionados conforme os conceitos forem estudados.

## payment-service

Responsável por:

- criar pagamentos;
- consultar pagamentos;
- controlar estados;
- idempotência;
- integração com provedor;
- publicar eventos;
- iniciar tarefas assíncronas.

## ledger-service

Responsável pelo livro razão financeiro.

Objetivos educacionais:

- contabilidade básica;
- consistência;
- eventos;
- consumidores Kafka;
- imutabilidade;
- auditoria.

## reconciliation-service

Responsável por conferir se os registros internos correspondem aos registros do provedor financeiro.

Resultados possíveis:

```text
MATCHED
MISSING_INTERNAL
MISSING_PROVIDER
AMOUNT_MISMATCH
STATUS_MISMATCH
DUPLICATE_PROVIDER
```

---

# 10. Payment Provider Simulator

Não será utilizado um banco ou gateway real.

Criaremos um serviço que simula um adquirente/gateway de pagamento.

```http
POST /provider/payments
```

Ele poderá responder com:

- aprovação;
- recusa;
- erro 500;
- timeout;
- lentidão.

Isso será utilizado posteriormente para estudar:

- retry;
- timeout;
- circuit breaker;
- fallback;
- resiliência.

---

# 11. Stack principal

## Linguagem

```text
Java 21
```

## Framework

```text
Spring Boot 4.1.1
```

## Spring

```text
Spring Web
Spring Data JPA
Spring Validation
Spring Security
Spring Kafka
Spring AMQP
Spring Actuator
```

## Banco

```text
PostgreSQL
Hibernate
Spring Data JPA
Flyway
```

## Mensageria

```text
Apache Kafka
RabbitMQ
```

## Cache

```text
Redis
```

Redis será introduzido somente quando houver um problema concreto que justifique sua utilização.

## Testes

```text
JUnit 5
Mockito
AssertJ
Testcontainers
RestAssured
WireMock
```

## Containers

```text
Docker
Docker Compose
```

## Observabilidade

```text
Spring Boot Actuator
Micrometer
Prometheus
Grafana
OpenTelemetry
```

## CI/CD

```text
GitHub Actions
```

## Cloud

```text
AWS
```

---

# 12. Configuração inicial no Spring Initializr

Para o primeiro serviço:

```text
Project: Maven
Language: Java
Spring Boot: 4.1.1

Group: io.github.igoortoorres
Artifact: payment-service
Name: payment-service
Description: Payment processing and reconciliation platform
Package name: io.github.igoortoorres.paymentservice

Packaging: Jar
Configuration: YAML
Java: 21
```

Dependências iniciais:

```text
Spring Web
Validation
```

Não adicionar inicialmente:

```text
Spring Data JPA
PostgreSQL Driver
Flyway Migration
Spring for Apache Kafka
Spring for RabbitMQ
Spring Security
Redis
Prometheus
```

Essas tecnologias serão introduzidas apenas quando houver um problema concreto para elas resolverem.

## Executar a aplicação localmente

Com Java 21, Docker e `make` instalados, execute:

```bash
make run
```

Esse comando inicia o PostgreSQL com Docker Compose e depois inicia a aplicação, que ficará disponível em `http://localhost:8080`. Para encerrar a aplicação, use `Ctrl+C`.

Outros comandos disponíveis:

```bash
make test     # executa os testes com um PostgreSQL temporário
make db-up    # inicia somente o PostgreSQL
make db-down  # encerra o PostgreSQL
```

Para executar `make test`, o Docker deve estar ativo. Os testes de integração
usam Testcontainers para criar um PostgreSQL isolado, executar as migrations do
Flyway e remover o container ao final da suíte. Não é necessário iniciar o banco
do Docker Compose antes dos testes.

Os testes estão separados por responsabilidade:

- testes unitários validam as regras de domínio e os casos de uso com JUnit,
  Mockito e AssertJ;
- testes de persistência validam o adapter JPA contra PostgreSQL real;
- testes da API usam RestAssured e uma aplicação iniciada em porta aleatória para
  validar requisições e respostas HTTP reais.

Para visualizar e testar os endpoints pelo Swagger UI, acesse:

```text
http://localhost:8080/swagger-ui/index.html
```

O contrato OpenAPI em JSON fica disponível em:

```text
http://localhost:8080/v3/api-docs
```

---

# 13. Estrutura prevista do repositório

Nome do repositório:

```text
sistema-pagamentos-reconciliacao
```

Estrutura:

```text
sistema-pagamentos-reconciliacao/
│
├── payment-service/
│
├── ledger-service/
│
├── reconciliation-service/
│
├── provider-simulator/
│
├── infrastructure/
│   ├── docker/
│   ├── kafka/
│   ├── rabbitmq/
│   ├── prometheus/
│   └── grafana/
│
├── docs/
│   ├── architecture/
│   ├── diagrams/
│   └── adr/
│
├── .github/
│   └── workflows/
│
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

---

# 14. Modelo inicial de Payment

Campos conceituais:

```text
id
externalReference
providerTransactionId
amount
currency
paymentMethod
status
idempotencyKey
createdAt
updatedAt
version
```

Possível enum:

```java
CREATED
PROCESSING
AUTHORIZED
DECLINED
SETTLED
FAILED
REFUNDED
```

O modelo final deverá ser definido durante o desenvolvimento.

Não copiar automaticamente este modelo sem discutir cada campo.

---

# 15. API inicial

## Criar pagamento

```http
POST /api/payments
```

Header:

```http
Idempotency-Key: 2e8e7a46...
```

Body:

```json
{
  "amount": 299.90,
  "currency": "BRL",
  "paymentMethod": "PIX",
  "externalReference": "ORDER-10001"
}
```

## Buscar pagamento

```http
GET /api/payments/{paymentId}
```

## Listar pagamentos

```http
GET /api/payments
```

Posteriormente:

- paginação;
- filtros;
- ordenação.

## Reembolso

```http
POST /api/payments/{paymentId}/refund
```

---

# 16. Idempotência

Um sistema de pagamento não deve processar a mesma solicitação duas vezes por acidente.

```http
Idempotency-Key: ABC123
```

A primeira requisição cria o pagamento.

Uma segunda requisição com a mesma chave deve retornar o mesmo resultado, em vez de criar outra transação.

Esse tópico deverá incluir:

- unique constraint;
- concorrência;
- race conditions;
- idempotência no banco;
- possível evolução para Redis.

---

# 17. Eventos Kafka

Exemplos:

```text
payment.created.v1
payment.processing.v1
payment.authorized.v1
payment.declined.v1
payment.failed.v1
payment.settled.v1
payment.refunded.v1
```

Envelope conceitual:

```json
{
  "eventId": "uuid",
  "eventType": "payment.authorized",
  "eventVersion": 1,
  "occurredAt": "2026-09-16T12:00:00Z",
  "aggregateId": "payment-uuid",
  "correlationId": "uuid",
  "payload": {
    "paymentId": "uuid",
    "amount": 299.90,
    "currency": "BRL"
  }
}
```

Estudar:

- event envelope;
- versionamento;
- correlation ID;
- event ID;
- timestamps;
- serialização;
- compatibilidade.

---

# 18. RabbitMQ

Filas previstas:

```text
payment.process
webhook.dispatch
notification.send
payment.retry
provider.retry
```

---

# 19. Dead Letter Queue

Depois de determinado número de falhas:

```text
payment.process
       │
       ▼
tentativa
       │
       X
      erro
       │
       ▼
retry
       │
       X
      erro
       │
       ▼
DLQ
```

Exemplo:

```text
payment.process.dlq
```

A IA deverá explicar:

- por que DLQ existe;
- quando usar;
- como investigar mensagens;
- diferença entre retry imediato e retry com atraso.

---

# 20. Transactional Outbox

Problema:

```text
Banco salva pagamento ✅
Kafka falha ❌
```

O banco passa a indicar que o pagamento mudou de estado, mas os outros serviços nunca receberam o evento.

Solução a estudar:

```text
Transactional Outbox
```

Fluxo:

```text
TRANSACTION DATABASE
│
├── UPDATE payment
└── INSERT outbox_event
```

Depois:

```text
Outbox Publisher
       │
       ▼
     Kafka
```

A implementação ingênua deve ser construída e compreendida antes da solução com Outbox.

---

# 21. Ledger

O Ledger deverá possuir registros imutáveis.

Exemplo:

```text
Ledger Entry 1
Ledger Entry 2
Ledger Entry 3
Ledger Entry 4
```

Campos:

```text
entryId
paymentId
account
type
amount
currency
createdAt
```

Estudar:

- ledger;
- débito;
- crédito;
- double-entry bookkeeping;
- imutabilidade;
- auditoria financeira.

---

# 22. Reconciliação

O provider poderá gerar registros como:

```csv
transaction_id,payment_id,amount,status,settled_at
TX001,PAY001,100.00,SETTLED,2026-09-16
TX002,PAY002,200.00,SETTLED,2026-09-16
```

O `reconciliation-service` compara isso com os registros internos.

Resultados:

```text
MATCHED
AMOUNT_MISMATCH
STATUS_MISMATCH
MISSING_PROVIDER
MISSING_INTERNAL
DUPLICATE_PROVIDER
```

---

# 23. Resiliência

Utilizar posteriormente:

```text
Resilience4j
```

Estudar:

- timeout;
- retry;
- circuit breaker;
- fallback;
- bulkhead.

Estados do Circuit Breaker:

```text
CLOSED
OPEN
HALF_OPEN
```

---

# 24. Segurança

Adicionar depois que o fluxo principal estiver funcionando.

```text
Spring Security
JWT
OAuth2
```

Possível evolução:

```text
Keycloak
```

Não adicionar Keycloak antes de compreender Spring Security.

---

# 25. Observabilidade

## Logs

Incluir quando possível:

```text
paymentId
correlationId
eventId
```

## Métricas

Exemplos:

```text
payments_created_total
payments_authorized_total
payments_declined_total
payments_failed_total
payment_processing_duration
provider_request_duration
```

## Dashboard Grafana

Exibir:

```text
Pagamentos por minuto
Taxa de aprovação
Taxa de recusa
Erros 5xx
Latência média
Latência P95
Kafka Consumer Lag
RabbitMQ Queue Depth
```

## Distributed Tracing

Utilizar:

```text
OpenTelemetry
```

Fluxo observável:

```text
POST /api/payments
       │
       ▼
Payment Service
       │
       ▼
RabbitMQ
       │
       ▼
Payment Worker
       │
       ▼
Provider Simulator
       │
       ▼
Kafka
       │
       ▼
Ledger Service
```

---

# 26. Estratégia de testes

## Testes unitários

```text
JUnit 5
Mockito
AssertJ
```

Testar:

- regras de domínio;
- transições de estado;
- validações;
- serviços.

## Testes de integração

```text
Testcontainers
```

Containers:

```text
PostgreSQLContainer
KafkaContainer
RabbitMQContainer
```

## Integração externa

```text
WireMock
```

Simular:

```text
200
422
500
timeout
```

---

# 27. API documentation

Utilizar OpenAPI / Swagger.

Documentar:

- endpoints;
- requests;
- responses;
- erros;
- autenticação.

---

# 28. Tratamento de erros

Formato sugerido:

```json
{
  "timestamp": "2026-09-16T14:30:00Z",
  "status": 404,
  "error": "PAYMENT_NOT_FOUND",
  "message": "Payment not found",
  "path": "/api/payments/123",
  "correlationId": "abc-123"
}
```

Utilizar posteriormente:

```java
@RestControllerAdvice
```

A IA deverá explicar a anotação antes de usá-la.

---

# 29. Docker

Objetivo final:

```bash
docker compose up
```

Subindo:

```text
PostgreSQL
Kafka
RabbitMQ
Redis
Prometheus
Grafana
payment-service
ledger-service
reconciliation-service
provider-simulator
```

Não adicionar tudo de uma vez.

---

# 30. CI/CD

Pipeline:

```text
Push
  │
  ▼
Compile
  │
  ▼
Unit Tests
  │
  ▼
Integration Tests
  │
  ▼
Build
  │
  ▼
Docker Image
  │
  ▼
Container Registry
  │
  ▼
Deploy
```

---

# 31. Deploy

Primeira versão:

```text
AWS EC2
+
Docker Compose
```

Com:

- HTTPS;
- reverse proxy;
- domínio;
- GitHub Actions.

Possíveis evoluções:

```text
RDS PostgreSQL
ECS / Fargate
Amazon MSK
Amazon MQ
ElastiCache
CloudWatch
```

Kubernetes/EKS poderá ser estudado posteriormente.

---

# 32. Variáveis de ambiente

Criar:

```text
.env.example
```

Nunca versionar:

```text
.env
```

Exemplo:

```env
DATABASE_URL=
DATABASE_USERNAME=
DATABASE_PASSWORD=

KAFKA_BOOTSTRAP_SERVERS=

RABBITMQ_HOST=
RABBITMQ_USERNAME=
RABBITMQ_PASSWORD=

REDIS_HOST=

JWT_SECRET=
```

---

# 33. Git

Repositório:

```text
https://github.com/IgoorToorres/sistema-pagamentos-reconciliacao
```

Branch principal:

```text
main
```

Branches sugeridas:

```text
feature/payment-api
feature/kafka-events
feature/rabbitmq-processing
feature/reconciliation
fix/payment-idempotency
```

Commits:

```text
feat: add payment creation endpoint
feat: publish payment created event
test: add payment integration tests
fix: prevent duplicate payments using idempotency key
```

---

# 34. ADR — Architecture Decision Records

Diretório:

```text
docs/adr
```

Exemplos:

```text
0001-use-postgresql.md
0002-use-kafka-for-domain-events.md
0003-use-rabbitmq-for-work-queues.md
0004-use-transactional-outbox.md
```

Cada ADR deve responder:

```text
Problema
Contexto
Decisão
Alternativas
Consequências
```

---

# 35. Ordem correta de desenvolvimento

## ETAPA 0 — Preparação

Entender:

- fluxo de pagamento;
- arquitetura;
- responsabilidades;
- comunicação síncrona;
- comunicação assíncrona.

Criar:

```text
README
Git repository
Java 21
Maven
Spring Boot
```

Confirmar:

```bash
java --version
mvn --version
```

Resultado esperado:

Aplicação Spring Boot iniciando corretamente.

---

## ETAPA 1 — Primeira API

Criar somente:

```text
payment-service
```

Sem Kafka.

Sem RabbitMQ.

Sem Redis.

Endpoints:

```http
POST /api/payments
GET /api/payments/{id}
GET /api/payments
```

Estudar:

- Controller;
- Service;
- Repository;
- DTO;
- Entity;
- Dependency Injection;
- Bean;
- Spring Container;
- Validation.

Fluxo:

```text
HTTP
 ↓
Controller
 ↓
Service
 ↓
Repository
 ↓
Database
```

---

## ETAPA 2 — PostgreSQL

Adicionar:

```text
PostgreSQL
Docker
Docker Compose
Spring Data JPA
Hibernate
Flyway
```

Estudar:

- primary key;
- unique constraint;
- index;
- transaction;
- migration;
- ORM;
- lazy loading;
- eager loading;
- N+1.

---

## ETAPA 3 — Regras de domínio

Estados:

```text
CREATED
PROCESSING
AUTHORIZED
DECLINED
FAILED
SETTLED
```

Não permitir transições inválidas.

Estudar:

- modelagem de domínio;
- invariantes;
- encapsulamento;
- DDD básico.

---

## ETAPA 4 — Idempotência

Implementar:

```http
Idempotency-Key
```

Estudar:

- pagamento duplicado;
- concorrência;
- unique constraint;
- race conditions.

---

## ETAPA 5 — Testes

Implementar:

```text
JUnit
Mockito
Testcontainers
RestAssured
```

---

## ETAPA 6 — Docker local

Subir:

```text
payment-service
PostgreSQL
```

Estudar:

- image;
- container;
- volume;
- network;
- port mapping;
- environment variables.

---

## ETAPA 7 — Introdução ao Kafka

Antes de código:

**REALIZAR A AULA DE KAFKA DESCRITA NESTE README.**

Depois:

```text
payment.created.v1
```

Criar producer e consumer simples apenas para estudo.

---

## ETAPA 8 — Kafka aplicado

Adicionar:

```text
payment.created.v1
payment.authorized.v1
payment.declined.v1
payment.settled.v1
```

Estudar:

- producers;
- consumers;
- partitions;
- offsets;
- consumer groups;
- retries;
- serialization.

---

## ETAPA 9 — Transactional Outbox

Primeiro:

```text
save()
publish()
```

Demonstrar como pode falhar.

Depois implementar Outbox.

---

## ETAPA 10 — Ledger Service

Criar:

```text
ledger-service
```

Consumir:

```text
payment.authorized.v1
```

Estudar:

- Kafka consumers reais;
- consumer groups;
- idempotent consumer;
- consistência eventual.

---

## ETAPA 11 — RabbitMQ

Antes de código:

**REALIZAR A AULA DE RABBITMQ DESCRITA NESTE README.**

Explorar RabbitMQ Management UI.

Criar fila experimental.

Enviar e consumir mensagem manualmente.

Entender ACK.

---

## ETAPA 12 — Processamento assíncrono

Criar:

```text
payment.process
```

Fluxo:

```text
POST /api/payments
       │
       ▼
Payment CREATED
       │
       ▼
RabbitMQ
payment.process
       │
       ▼
Payment Worker
       │
       ▼
Provider
```

---

## ETAPA 13 — Provider Simulator

Criar:

```text
provider-simulator
```

Simular:

- aprovação;
- recusa;
- timeout;
- erro HTTP 500;
- lentidão.

---

## ETAPA 14 — Resiliência

Adicionar:

```text
Resilience4j
```

Estudar:

```text
timeout
retry
circuit breaker
```

---

## ETAPA 15 — RabbitMQ Retry + DLQ

Implementar:

```text
retry
DLQ
```

Inspecionar mensagens manualmente.

---

## ETAPA 16 — Reconciliation Service

Criar:

```text
reconciliation-service
```

Primeira versão manual.

---

## ETAPA 17 — Reconciliação automatizada

Estudar:

```java
@Scheduled
```

Possível evolução:

```text
Spring Batch
```

---

## ETAPA 18 — Redis

Somente quando houver necessidade real.

Possíveis usos:

```text
idempotency
cache
distributed lock
rate limit
```

---

## ETAPA 19 — Segurança

Adicionar:

```text
Spring Security
JWT
```

Depois avaliar:

```text
OAuth2
Keycloak
```

---

## ETAPA 20 — Observabilidade

Adicionar:

```text
Actuator
Micrometer
Prometheus
Grafana
OpenTelemetry
```

---

## ETAPA 21 — Performance

Ferramenta sugerida:

```text
k6
```

Analisar:

- CPU;
- memória;
- conexões;
- latência;
- throughput;
- erros;
- Kafka lag;
- filas RabbitMQ.

---

## ETAPA 22 — CI

GitHub Actions executando:

```text
mvn verify
```

Mais:

- testes;
- build;
- imagem Docker.

---

## ETAPA 23 — Produção

Primeira versão:

```text
AWS EC2
Docker Compose
HTTPS
```

---

## ETAPA 24 — Monitoramento em produção

Observar:

```text
requisições
latência
erros
CPU
memória
Kafka
RabbitMQ
```

---

## ETAPA 25 — Documentação final

Atualizar README com:

- arquitetura;
- diagramas;
- decisões;
- screenshots;
- Grafana;
- Swagger;
- deploy;
- exemplos;
- testes;
- problemas enfrentados.

---

# 36. Regra de progressão

Nunca avançar de etapa se a etapa atual não estiver funcionando.

Antes de avançar:

```text
Código compila?
Testes passam?
Entendi o conceito?
Consigo explicar o que construí?
Consigo explicar por que fiz dessa maneira?
```

Se a resposta for não, continuar estudando a etapa.

---

# 37. Método que a IA deve utilizar

Ao iniciar cada etapa:

## 1. Objetivo

O que construiremos.

## 2. Conceito

Explicar a teoria necessária.

## 3. Arquitetura

Onde entra no sistema.

## 4. Implementação

Fazer uma pequena alteração.

## 5. Explicação do código

Explicar as partes relevantes.

## 6. Executar

Mostrar os comandos.

## 7. Resultado esperado

Mostrar o que deve acontecer.

## 8. Testar

Explicar como validar.

## 9. Possíveis erros

Explicar problemas comuns.

## 10. Checkpoint

Validar se Igor consegue explicar:

- o que foi feito;
- por que;
- como funciona.

Somente depois continuar.

---

# 38. Ao encontrar erros

A IA NÃO deve simplesmente devolver outro código inteiro.

Primeiro:

1. Ler o erro completo.
2. Identificar a camada onde ocorreu.
3. Explicar o erro.
4. Mostrar o que significa a stack trace.
5. Criar hipótese.
6. Validar hipótese.
7. Corrigir.
8. Executar novamente.
9. Explicar por que a correção funcionou.

O objetivo também é aprender debugging.

---

# 39. Arquitetura e Clean Code

Durante o projeto estudar:

```text
SOLID
Clean Code
DDD
Hexagonal Architecture
Clean Architecture
```

Regra:

> Introduzir um padrão somente quando existir um problema que ele resolva.

Evitar abstrações prematuras.

---

# 40. Qualidade de código

- classes pequenas;
- métodos com responsabilidades claras;
- nomes explícitos;
- evitar lógica no Controller;
- evitar Entities expostas diretamente pela API;
- DTOs específicos;
- validação de entrada;
- tratamento consistente de erros;
- evitar duplicação;
- evitar abstrações prematuras.

---

# 41. Requisitos não funcionais

## Confiabilidade

Nenhum pagamento deve ser silenciosamente perdido.

## Idempotência

Eventos e requisições repetidos não devem causar duplicidade.

## Auditoria

Alterações importantes devem ser rastreáveis.

## Observabilidade

Falhas devem ser diagnosticáveis.

## Segurança

Endpoints devem ser protegidos quando necessário.

## Resiliência

Falhas externas não devem derrubar todo o sistema.

## Escalabilidade

Consumers devem poder ser escalados horizontalmente.

---

# 42. Cenários obrigatórios

## Pagamento normal

```text
Pagamento criado
Pagamento processado
Pagamento aprovado
Pagamento liquidado
Ledger atualizado
Reconciliação correta
```

## Pagamento recusado

```text
Payment -> DECLINED
```

## Provedor fora do ar

```text
timeout
retry
circuit breaker
```

## Mensagem falhando

```text
RabbitMQ
↓
retry
↓
DLQ
```

## Evento duplicado

Consumer deve ser idempotente.

## Requisição duplicada

`Idempotency-Key` deve impedir processamento duplicado.

## Reconciliação divergente

Detectar:

```text
AMOUNT_MISMATCH
```

## Evento não publicado

Simular falha entre banco e Kafka para justificar:

```text
Transactional Outbox
```

---

# 43. Definition of Done

Uma tarefa só estará pronta quando:

- código implementado;
- código compreendido;
- testes criados;
- testes executados;
- tratamento de erro considerado;
- documentação necessária atualizada;
- aplicação executada localmente;
- nenhuma credencial adicionada ao Git;
- commit realizado.

---

# 44. Definition of Done do projeto

O projeto estará concluído quando for possível demonstrar:

```text
POST /api/payments
```

seguido de:

```text
Request
↓
Payment Service
↓
PostgreSQL
↓
RabbitMQ
↓
Worker
↓
Provider Simulator
↓
Payment Status
↓
Kafka
↓
Ledger
↓
Reconciliation
↓
Prometheus
↓
Grafana
```

com o sistema executando em ambiente publicado.

---

# 45. O que deverá ser possível explicar em entrevista

## Java / Spring

- Como funciona Dependency Injection?
- O que é um Bean?
- Como funciona `@Transactional`?
- Como o Spring Boot configura a aplicação?

## PostgreSQL

- Como você garantiu idempotência?
- Quais índices criou?
- Como lidou com concorrência?

## Kafka

- Por que utilizou Kafka?
- O que é uma partition?
- O que é consumer group?
- Como funciona offset?
- Como tratou eventos duplicados?
- Como implementou retry?
- O que é consumer lag?

## RabbitMQ

- Por que RabbitMQ além de Kafka?
- O que é Exchange?
- O que é Routing Key?
- O que é ACK?
- O que acontece quando um consumer falha?
- Como sua DLQ funciona?

## Sistemas distribuídos

- O que é eventual consistency?
- O que é idempotência?
- Como você tratou falha entre banco e Kafka?
- Por que implementou Transactional Outbox?

## Resiliência

- Como funciona Circuit Breaker?
- Quando retry pode ser perigoso?

## Observabilidade

- Qual a diferença entre logs, metrics e traces?

## Docker

- Qual diferença entre image e container?
- Como seus serviços se comunicam?

## Produção

- Como funciona o pipeline de CI/CD?
- Como a aplicação foi publicada?
- Como segredos são armazenados?

---

# 46. Como apresentar o projeto no LinkedIn

Exemplo futuro:

> Desenvolvi um sistema distribuído de processamento e reconciliação de pagamentos utilizando Java 21 e Spring Boot, simulando cenários reais de plataformas financeiras.
>
> A solução utiliza PostgreSQL para persistência transacional, Kafka para eventos de domínio e RabbitMQ para processamento assíncrono, incluindo estratégias de idempotência, retry, Dead Letter Queue e Transactional Outbox.
>
> Implementei integração com um provedor financeiro simulado, circuit breaker e mecanismos de reconciliação para identificar divergências entre transações internas e registros externos.
>
> O ambiente utiliza Docker, testes automatizados com JUnit e Testcontainers, observabilidade com Prometheus/Grafana/OpenTelemetry e pipeline CI/CD para publicação em ambiente AWS.

Este texto deve ser utilizado somente quando as funcionalidades descritas realmente tiverem sido implementadas.

---

# 47. O que NÃO fazer

Não transformar o projeto em uma corrida para adicionar tecnologias.

Não adicionar:

```text
Kafka
RabbitMQ
Redis
Kubernetes
AWS
```

sem compreender o problema resolvido por cada uma.

Não criar 15 microsserviços.

Não seguir arquitetura de grandes empresas sem necessidade.

Não copiar projetos completos de tutoriais.

Não permitir que a IA escreva todo o sistema sem explicação.

Não avançar com erro anterior não compreendido.

---

# 48. Filosofia do projeto

A prioridade é:

```text
ENTENDER
↓
IMPLEMENTAR
↓
TESTAR
↓
QUEBRAR
↓
CORRIGIR
↓
OBSERVAR
↓
EXPLICAR
```

E não:

```text
COPIAR
↓
COLAR
↓
FUNCIONOU
↓
PRÓXIMO
```

O objetivo final não é simplesmente possuir um repositório com várias tecnologias.

O objetivo é conseguir abrir qualquer parte deste projeto e explicar:

- O que isso faz?
- Por que está aqui?
- Qual problema resolve?
- O que aconteceria sem isso?
- Quais alternativas existiam?
- Como isso se comporta quando algo dá errado?

---

# 49. Status do projeto

```text
[x] Etapa 0 — Preparação
[x] Etapa 1 — API inicial
[x] Etapa 2 — PostgreSQL
[x] Etapa 3 — Regras de domínio
[x] Etapa 4 — Idempotência
[x] Etapa 5 — Testes
[ ] Etapa 6 — Docker
[ ] Etapa 7 — Introdução ao Kafka
[ ] Etapa 8 — Kafka aplicado
[ ] Etapa 9 — Transactional Outbox
[ ] Etapa 10 — Ledger Service
[ ] Etapa 11 — Introdução ao RabbitMQ
[ ] Etapa 12 — Processamento assíncrono
[ ] Etapa 13 — Provider Simulator
[ ] Etapa 14 — Resiliência
[ ] Etapa 15 — RabbitMQ Retry / DLQ
[ ] Etapa 16 — Reconciliation Service
[ ] Etapa 17 — Reconciliação automática
[ ] Etapa 18 — Redis
[ ] Etapa 19 — Segurança
[ ] Etapa 20 — Observabilidade
[ ] Etapa 21 — Performance
[ ] Etapa 22 — CI
[ ] Etapa 23 — Produção
[ ] Etapa 24 — Monitoramento
[ ] Etapa 25 — Documentação final
```

---

# 50. Próxima etapa

Começar exclusivamente pela:

```text
ETAPA 6 — Docker local
```

Antes de implementar, estudar imagem, container, volume, rede, mapeamento de
portas e variáveis de ambiente. O objetivo será criar a imagem da aplicação e
executar `payment-service` e PostgreSQL juntos com Docker Compose.

**Não pular etapas.**
