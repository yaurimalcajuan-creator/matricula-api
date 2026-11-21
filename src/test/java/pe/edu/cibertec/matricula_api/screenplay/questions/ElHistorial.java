package pe.edu.cibertec.matricula_api.screenplay.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Question;

import java.util.Map;

public class ElHistorial {

    public static Question<Integer> creditosAprobados() {
        return actor -> SerenityRest.lastResponse().jsonPath().getInt("creditosAprobados");
    }

    public static Question<Double> promedioPonderado() {
        return actor -> SerenityRest.lastResponse().jsonPath().getDouble("promedioPonderado");
    }

    public static Question<String> condicionDelCurso(String codigoCurso) {
        return actor -> {
            var cursos = SerenityRest.lastResponse().jsonPath().getList("cursos", Map.class);
            return cursos.stream()
                    .filter(c -> codigoCurso.equals(c.get("codigoCurso")))
                    .map(c -> (String) c.get("condicion"))
                    .findFirst()
                    .orElse(null);
        };
    }
}