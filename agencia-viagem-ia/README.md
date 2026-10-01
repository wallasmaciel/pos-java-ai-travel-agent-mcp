# Agência de Viagens com IA

API Quarkus que implementa um assistente virtual da empresa fictícia **Mundo Viagens**. O serviço usa um modelo local no Ollama, recupera informações de pacotes com RAG e acessa reservas por meio de ferramentas MCP.

Este módulo faz parte do projeto [`Agência de Viagens com IA`](../README.md).

## Funcionalidades

- Chat com o modelo `qwen3:4b` executado no Ollama
- Embeddings com `nomic-embed-text`
- Ingestão do documento `pacotes-viagem.md`
- Busca semântica com PgVector
- Memória de até 20 mensagens, separada por usuário
- Integração com o servidor de reservas via MCP Streamable HTTP
- Guardrail de entrada contra prompt injection
- Guardrail de saída para manter tom profissional

## Requisitos

- JDK 25
- Docker em execução para o PgVector iniciado pelo Quarkus Dev Services
- Ollama disponível em `http://localhost:11434`
- Servidor [`mcp-booking-server`](../mcp-booking-server) em execução na porta `8081`

Prepare os modelos:

```bash
ollama pull qwen3:4b
ollama pull nomic-embed-text
```

## Executando em desenvolvimento

```bash
./mvnw quarkus:dev
```

No Windows:

```powershell
mvnw.cmd quarkus:dev
```

A aplicação fica disponível em `http://localhost:8080` e a Dev UI do Quarkus em `http://localhost:8080/q/dev-ui`.

## API

Todas as requisições usam corpo `text/plain`.

| Endpoint | Recursos |
| --- | --- |
| `POST /travel/v1` | Chat básico |
| `POST /travel/v2` | Memória por usuário e ferramentas MCP |
| `POST /travel` | Template de autenticação, memória, ferramentas MCP e guardrails |

Os endpoints `/travel/v2` e `/travel` exigem o cabeçalho `X-User-Name`.

### Exemplo

```bash
curl -X POST \
  -H "Content-Type: text/plain" \
  -H "X-User-Name: Smith" \
  -d "Por favor, cancele minha reserva 67890." \
  http://localhost:8080/travel
```

Para a reserva de exemplo `67890`, utilize `Smith`, pois ela pertence a `Jane Smith`. O cabeçalho representa apenas uma autenticação simulada para fins didáticos.

## RAG

Na inicialização, `DocumentIngestor` carrega `src/main/resources/rag/pacotes-viagem.md`, divide o documento em segmentos e grava seus embeddings no PgVector. O recuperador retorna até cinco segmentos relevantes para cada consulta.

Para adicionar novos pacotes, atualize o arquivo Markdown de conhecimento e reinicie a aplicação para executar uma nova ingestão.

## Configuração principal

As propriedades ficam em `src/main/resources/application.properties`:

```properties
quarkus.langchain4j.ollama.base-url=http://localhost:11434
quarkus.langchain4j.ollama.chat-model.model-id=qwen3:4b
quarkus.langchain4j.ollama.embedding-model.model-id=nomic-embed-text
quarkus.langchain4j.mcp.booking-server.transport-type=streamable-http
quarkus.langchain4j.mcp.booking-server.url=http://localhost:8081/mcp
```

## Observações

- A memória de conversa é local e se perde ao reiniciar a aplicação.
- O primeiro uso pode ser mais lento enquanto os modelos são carregados pelo Ollama.
- Guardrails baseados em modelos são uma camada complementar, não uma fronteira de segurança completa.
