# MCP Booking Server

Servidor MCP construído com Quarkus para expor operações de reservas como ferramentas consumíveis por agentes de IA.

Este módulo faz parte do projeto [`Agência de Viagens com IA`](../README.md).

## Ferramentas publicadas

| Nome | Argumentos | Comportamento |
| --- | --- | --- |
| `getBookingDetails` | `bookingId` | Retorna os dados completos de uma reserva |
| `cancelBooking` | `bookingId`, `name` | Cancela quando `name` corresponde ao final do nome do titular |
| `listPackagesByCategory` | `category` | Lista destinos por `ADVENTURE` ou `TREASURES` |

## Dados de exemplo

| ID | Titular | Destino | Status inicial | Categoria |
| --- | --- | --- | --- | --- |
| `12345` | John Doe | Tesouros do Egito | `CONFIRMED` | `TREASURES` |
| `67890` | Jane Smith | Aventura Amazônica | `CONFIRMED` | `ADVENTURE` |
| `98765` | Peter Jones | Trilha Inca | `CONFIRMED` | `ADVENTURE` |

Os dados são armazenados em memória e voltam ao estado inicial quando o serviço é reiniciado.

## Requisitos

- JDK 21 ou superior

## Executando em desenvolvimento

```bash
./mvnw quarkus:dev
```

No Windows:

```powershell
mvnw.cmd quarkus:dev
```

O servidor usa a porta `8081`.

| Transporte | Endpoint |
| --- | --- |
| Streamable HTTP | `http://localhost:8081/mcp` |
| SSE legado | `http://localhost:8081/mcp/sse` |

A Dev UI do Quarkus fica disponível em `http://localhost:8081/q/dev-ui`.

## Regra de cancelamento

O exemplo valida o nome informado usando `booking.customerName().endsWith(name)`. Assim, a reserva `67890` pode ser cancelada com `Smith` ou `Jane Smith`, mas não com um usuário sem relação com o titular.

Essa validação é propositalmente simples para estudo. Em produção, use uma identidade autenticada e relacione a reserva a um identificador imutável do usuário.