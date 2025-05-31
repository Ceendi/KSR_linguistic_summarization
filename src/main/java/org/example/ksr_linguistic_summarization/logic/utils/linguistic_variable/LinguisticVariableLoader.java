package org.example.ksr_linguistic_summarization.logic.utils.linguistic_variable;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

public class LinguisticVariableLoader {
    public static List<LinguisticVariableDTO> load() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream input = LinguisticVariableLoader.class.getResourceAsStream("/org/example/ksr_linguistic_summarization/LinguisticVariableDefinitions.json")
        ) {
            if (input == null) throw new IllegalStateException("Nie znaleziono pliku z definicjami zmiennych lingwistycznych!");
            LinguisticVariablesRootDTO root = mapper.readValue(input, LinguisticVariablesRootDTO.class);
            return root.linguistic_variables;
        }
    }
}
