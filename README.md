# HMA – Aplicação de Gestão de Eventos de Saúde

API REST em Spring Boot para gerir consultas médicas e idas ao ginásio. Os dados ficam guardados num PostgreSQL que corre num contentor Docker.

**Tecnologias:** Java · Spring Boot · Spring Data JPA · PostgreSQL · Docker Compose · JUnit 5 · Mockito

## 1. Arquitetura

```text
Controller  — endpoints REST (EventController)
   │
   ▼
Service     — lógica de negócio e validações (EventService)
   │
   ▼
Repository  — Spring Data JPA (EventRepository)
   │
   ▼
PostgreSQL  — contentor Docker
```

## Ferramentas Usadas

#### Requisitos Técnicos

| Componente           | Tecnologia                  |
| -------------------- | --------------------------- |
| Linguagem            | Java 25                     |
| Framework            | Spring Boot 4.1.2           |
| API                  | Spring WebMVC               |
| Persistência         | Spring Data JPA             |
| Base de dados        | PostgreSQL                  |
| Containerização      | Docker                      |
| Testes da API        | Postman                     |
| Build                | Maven                       |

#### Dependencias

| Componente           | Tecnologia                  |
| -------------------- | --------------------------- |
| Testes               | Spring Boot Test / JUnit 5  |
| Mocking              | Mockito                     |
| Integração de testes | Mockito JUnit Jupiter       |
| Desenvolvimento      | Spring Boot DevTools        |
| Containerização      | Spring Boot Docker Compose  |
| Redução de código    | Project Lombok              |





## 2. Como executar

1. Garantir que o Docker está a correr e que a porta `5432` está livre
   (nenhum outro PostgreSQL ou contentor a usá-la).
2. Na raiz do projeto, arrancar a aplicação com o comando:

   ```bash
   ./mvnw spring-boot:run
   ```

   A dependência `spring-boot-docker-compose`, que está listada no ficheiro `pom.xml`, inicia o PostgreSQL automaticamente
   a partir do `compose.yaml`. Na primeira execução, o ficheiro `init.sql` cria
   os tipos ENUM e a tabela `event`.
3. A API fica disponível em `http://localhost:8080`.

4. Os dados são guardados no volume Docker `pgdata`, por isso persistem quando a aplicação é reiniciada.

### Executar a partir de um IDE

Também podes executar a aplicação diretamente no IDE, sem usar a linha de comandos.
Em qualquer um dos casos, o Docker tem de estar a correr; O PostgreSQL é iniciado
automaticamente a partir do `compose.yaml` quando a aplicação arranca.

### Recomeçar com a base de dados vazia

O `init.sql` só corre quando o volume está vazio. Para apagar os dados e recriar o esquema:

```bash
docker compose down -v
```

## 3. Endpoints

| Método | Endpoint | Descrição | Respostas |
|---|---|---|---|
| `POST` | `/api/events` | Criar evento | 201, 400 |
| `GET` | `/api/events` | Listar eventos (com filtros opcionais) | 200, 400 |
| `GET` | `/api/events/{id}` | Consultar evento | 200, 404 |
| `PUT` | `/api/events/{id}` | Atualizar evento | 200, 400, 404 |
| `DELETE` | `/api/events/{id}` | Eliminar evento | 204, 404 |

### Filtros do `GET /api/events`

Todos os parâmetros são opcionais e podem ser combinados:

| Parâmetro | Valores | Exemplo |
|---|---|---|
| `type` | `DOCTOR`, `GYM` | `/api/events?type=GYM` |
| `status` | `SCHEDULED`, `COMPLETED`, `CANCELLED` | `/api/events?status=COMPLETED` |
| `type` e `status` | (`DOCTOR`, `GYM`) e (`SCHEDULED`, `COMPLETED`, `CANCELLED`) | `/api/events?type=GYM&status=COMPLETED` |

**Histórico de eventos concluídos:** `GET /api/events?status=COMPLETED`

## 4. Exemplos

### Criar uma consulta médica

`POST /api/events`

```json
{
  "name": "Consulta de rotina",
  "type": "DOCTOR",
  "date": "2026-11-10",
  "time": "10:30",
  "duration": 30,
  "status": "SCHEDULED"
}
```

### Criar uma sessão de ginásio

`POST /api/events`

```json
{
  "name": "Treino de musculação",
  "type": "GYM",
  "date": "2026-11-11",
  "time": "18:00",
  "duration": 60,
  "status": "SCHEDULED"
}
```

## 5. Regras de negócio

- O nome do evento é obrigatório.
- A data e a hora são obrigatórias.
- A duração deve ser superior a zero (minutos).
- O tipo é `DOCTOR` ou `GYM`.
- Um evento só pode ser criado com o estado `SCHEDULED`.
- Dois eventos não podem sobrepor-se no tempo (eventos cancelados são ignorados;
  um evento que começa quando outro termina não conta como sobreposição).
- Atualizar ou eliminar um evento inexistente devolve `404`.
- Dados inválidos ou conflitos de horário devolvem `400`.

## 6. Testes

```bash
./mvnw test
```

Os testes unitários (JUnit 5 + Mockito) cobrem o `EventService`, com o repositório simulado,
por isso não precisam de base de dados.

## 7. Coleção Postman

O ficheiro `postman/Events.postman_collection.json` é uma coleção do Postman com um conjunto de
pedidos de teste prontos a usar. Cobre todos os endpoints da API e está dividido em duas pastas:
**Successful Requests** (pedidos que devem ter sucesso) e **Unsuccessful Requests**
(pedidos que devem devolver erro 400 ou 404).

### Como importar e usar

1. Abrir o Postman e clicar em **Import** (canto superior esquerdo), ou arrastar o ficheiro
   `Events.postman_collection.json` para a janela do Postman.
2. A coleção **Events** aparece na barra lateral, em **Collections**.
3. Arrancar a aplicação (a API deve estar em `http://localhost:8080`).
4. Executar os pedidos um a um, abrindo o pedido e clicando em **Send**. Comparar o código de resposta com o indicado no nome do pedido.

O nome de cada pedido indica o resultado esperado, por exemplo
`Create New Event (Success - Code 201)` ou `Get Non-Existent Event (Error - Code 404)`.

### Successful Requests

| Pedido | Método e URL | Resposta esperada |
|---|---|---|
| Create New Event | `POST /api/events` | 201 |
| Get All Events | `GET /api/events` | 200 |
| Get Event By Id | `GET /api/events/1` | 200 |
| Update Event | `PUT /api/events/1` | 200 |
| Delete Event | `DELETE /api/events/1` | 204 |
| Get Events By Type | `GET /api/events?type=DOCTOR` | 200 |
| Get Events By Status | `GET /api/events?status=SCHEDULED` | 200 |
| Get Events By Type & Status | `GET /api/events?type=DOCTOR&status=SCHEDULED` | 200 |

### Unsuccessful Requests

Organizados por método HTTP:

| Pasta | Pedido | Resposta esperada |
|---|---|---|
| POST | Evento sem nome | 400 |
| POST | Evento sem data ou hora | 400 |
| POST | Evento com duração inválida (0) | 400 |
| POST | Evento com tipo inválido | 400 |
| POST | Evento com estado inválido | 400 |
| POST | Evento com conflito de horário | 400 |
| GET | Evento inexistente (`/api/events/999`) | 404 |
| PUT | Os mesmos 6 casos de erro do POST (sobre `/api/events/4`) | 400 |
| PUT | Evento inexistente (`/api/events/999`) | 404 |
| DELETE | Evento inexistente (`/api/events/999`) | 404 |



``Nota``: Alguns pedidos dependem de dados que já existem na base de dados.

