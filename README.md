# Beauty Booking API

Sistema de agendamento e gerenciamento para serviços de beleza e bem-estar (salões, spas, clínicas de estética).

## Sumário

- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Funcionalidades](#funcionalidades)
- [Como executar localmente](#como-executar-localmente)
- [Variáveis de ambiente](#variáveis-de-ambiente)
- [Endpoints e documentação](#endpoints-e-documentação)
- [Testes](#testes)
- [Deploy](#deploy)

---

## Tecnologias

| Camada | Tecnologia |
|--------|-----------|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.3 |
| Banco de dados | PostgreSQL (produção) · H2 (testes) |
| Migrações | Flyway |
| Autenticação | Spring Security + JWT (JJWT 0.12) |
| Armazenamento | Cloudinary |
| E-mail | Spring Mail (SMTP) |
| Documentação | SpringDoc OpenAPI (Swagger UI) |
| Testes unitários | JUnit 5 + Mockito |
| Testes de integração | Spring Boot Test + MockMvc |
| BDD | Cucumber 7 |
| Testes de carga | Gatling 3.11 |
| Cobertura | JaCoCo (mínimo 70%) |
| Análise estática | Checkstyle |
| CI/CD | GitHub Actions |
| Deploy | Railway |

---

## Arquitetura

O projeto segue **Clean Architecture**, com separação clara em quatro camadas:

```
src/main/java/com/beautyscheduler/
├── domain/              # Entidades puras, Value Objects e exceções de domínio
│   ├── entity/          # User, Establishment, Professional, BeautyService, Appointment, Review
│   ├── valueobject/     # Address, BusinessHours
│   └── exception/       # DomainException, ResourceNotFoundException, etc.
│
├── application/         # Regras de negócio — independente de frameworks
│   ├── port/in/         # Use case interfaces (comandos de entrada)
│   ├── port/out/        # Interfaces de repositório e serviços externos
│   └── usecase/         # Implementações dos use cases
│
├── adapter/             # Adaptadores (entrada e saída)
│   ├── in/web/          # Controllers REST, DTOs de request/response
│   └── out/             # Persistence (JPA), Email, Cloudinary, Calendar mock
│
└── infrastructure/      # Configurações, Security, JWT, Scheduler
```

### Fluxo de dependência

```
adapter/in → application/usecase → domain
adapter/out ← application/port/out (inversão de dependência)
```

---

## Funcionalidades

1. **Cadastro de estabelecimentos** — nome, endereço, serviços, profissionais, fotos
2. **Perfil de profissionais** — especialidades, horários, tarifas
3. **Agendamento online** — confirmação, cancelamento, reagendamento, no-show
4. **Notificações por e-mail** — confirmação e cancelamento de agendamentos
5. **Avaliações pós-serviço** — clientes avaliam serviços com nota e comentário
6. **Busca e filtragem** — por nome, cidade, serviço, avaliação e preço
7. **Integração com calendários** — simulada via `CalendarMockAdapter`
8. **Upload de fotos** — via Cloudinary

---

## Como executar localmente

### Pré-requisitos

- Java 21+
- Maven 3.9+
- Docker (opcional, para o banco de dados)

### 1. Clone o repositório

```bash
git clone https://github.com/JPauloSantos/beauty-booking-api.git
cd beauty-booking-api
```

### 2. Suba o banco de dados com Docker Compose

```bash
docker-compose up -d
```

### 3. Configure as variáveis de ambiente

Copie o arquivo de exemplo e ajuste os valores:

```bash
cp .env.example .env
```

Ou exporte diretamente:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/beauty_scheduler
export SPRING_DATASOURCE_USERNAME=beauty_user
export SPRING_DATASOURCE_PASSWORD=beauty_pass
export JWT_SECRET=sua-chave-secreta-com-pelo-menos-256-bits-de-comprimento
export CLOUDINARY_URL=cloudinary://<api_key>:<api_secret>@<cloud_name>
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=seu@email.com
export MAIL_PASSWORD=senha-de-app-do-gmail
```

### 4. Execute a aplicação

```bash
mvn spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

---

## Variáveis de ambiente

| Variável | Descrição | Obrigatória |
|----------|-----------|-------------|
| `SPRING_DATASOURCE_URL` | JDBC URL do PostgreSQL | Sim |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | Sim |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | Sim |
| `JWT_SECRET` | Chave HMAC-SHA256 (mín. 64 chars) | Sim |
| `JWT_EXPIRATION_MS` | Expiração do token em ms (padrão: 86400000) | Não |
| `CLOUDINARY_URL` | URL de conexão do Cloudinary | Sim |
| `MAIL_HOST` | Servidor SMTP | Sim |
| `MAIL_PORT` | Porta SMTP (587 para TLS) | Sim |
| `MAIL_USERNAME` | Usuário SMTP | Sim |
| `MAIL_PASSWORD` | Senha SMTP | Sim |

---

## Endpoints e documentação

### Swagger UI

- **Produção (Railway):** https://beauty-booking-api-production.up.railway.app/swagger-ui.html
- **Local:** http://localhost:8080/swagger-ui.html

### Fluxo básico via Swagger

1. `POST /api/v1/auth/register` — cadastre um usuário
2. `POST /api/v1/auth/login` — obtenha o JWT token
3. Clique em **Authorize** no Swagger, insira `Bearer <token>`
4. Use os demais endpoints autenticados

### Principais endpoints

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/auth/register` | Cadastrar usuário |
| POST | `/api/v1/auth/login` | Login e obtenção de JWT |
| POST | `/api/v1/establishments` | Criar estabelecimento |
| GET | `/api/v1/establishments/search` | Buscar estabelecimentos |
| POST | `/api/v1/professionals` | Cadastrar profissional |
| POST | `/api/v1/services` | Cadastrar serviço |
| POST | `/api/v1/appointments` | Criar agendamento |
| PATCH | `/api/v1/appointments/{id}/cancel` | Cancelar agendamento |
| PATCH | `/api/v1/appointments/{id}/reschedule` | Reagendar |
| PATCH | `/api/v1/appointments/{id}/no-show` | Marcar no-show |
| POST | `/api/v1/reviews` | Avaliar serviço |
| GET | `/api/v1/reviews/establishment/{id}` | Listar avaliações |

---

## Testes

### Executar todos os testes

```bash
mvn test
```

### Relatório de cobertura (JaCoCo)

```bash
mvn test jacoco:report
# Relatório gerado em: target/site/jacoco/index.html
```

### Testes de carga (Gatling)

```bash
# Com a aplicação rodando localmente na porta 8080:
mvn gatling:test
# Relatório gerado em: target/gatling/
```

### Estrutura dos testes

```
src/test/
├── java/com/beautyscheduler/
│   ├── unit/
│   │   ├── domain/      # AppointmentTest, ReviewTest, UserTest
│   │   └── application/ # CreateAppointmentServiceTest, CancelAppointmentServiceTest,
│   │                    # RescheduleAppointmentServiceTest, MarkNoShowServiceTest,
│   │                    # RegisterUserServiceTest, AuthenticateUserServiceTest
│   ├── integration/     # EstablishmentControllerIT, AuthControllerIT,
│   │                    # AppointmentControllerIT
│   └── bdd/             # CucumberRunnerTest + steps
├── resources/
│   ├── features/        # establishment.feature, appointment.feature
│   └── application-test.yml
└── scala/               # AppointmentSimulation.scala (Gatling)
```

---

## Deploy

A API está deployada no **Railway** com PostgreSQL gerenciado:

- **API:** https://beauty-booking-api-production.up.railway.app
- **Swagger:** https://beauty-booking-api-production.up.railway.app/swagger-ui.html

O CI/CD é feito via **GitHub Actions** (`.github/workflows/ci.yml`), executando em cada push para `main` e `develop`:

1. Build e testes (`mvn clean verify`)
2. Verificação de cobertura JaCoCo ≥ 70%
3. Build da imagem Docker

---

## Repositório

[https://github.com/JPauloSantos/beauty-booking-api](https://github.com/JPauloSantos/beauty-booking-api)
