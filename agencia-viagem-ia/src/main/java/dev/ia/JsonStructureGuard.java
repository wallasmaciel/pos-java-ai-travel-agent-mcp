package dev.ia;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.StringReader;

@ApplicationScoped
public class JsonStructureGuard implements OutputGuardrail {
    @Override
    public OutputGuardrailResult validate(AiMessage aiMessage) {
        String response = aiMessage.text();
        try (JsonReader reader = Json.createReader(new StringReader(response))) {
            JsonObject jsonObject = reader.readObject();
            return OutputGuardrailResult.success();
        } catch (Exception e) {
            return reprompt(aiMessage.text(), """
                Erro: Sua resposta não é um JSON válido.
                Problema encontrado: " + e.getMessage() + ". " +
                Gere Novamente apenas o JSON, sem blocos de código markdown ou texto adicional.
            """);
        }
    }
}
