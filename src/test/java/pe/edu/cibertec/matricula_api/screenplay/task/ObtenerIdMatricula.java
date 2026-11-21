package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import java.util.Map;

public class ObtenerIdMatricula implements Task {

    private final String codigoCurso;

    public ObtenerIdMatricula(String codigoCurso) {
        this.codigoCurso = codigoCurso;
    }

    public static ObtenerIdMatricula delCurso(String codigoCurso) {
        return new ObtenerIdMatricula(codigoCurso);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("accessToken");

        actor.attemptsTo(
                Get.resource("/api/matriculas")
                        .with(req -> req.header("Authorization", "Bearer " + token))
        );

        var matriculas = SerenityRest.lastResponse().jsonPath().getList("$", Map.class);
        Integer idEncontrado = matriculas.stream()
                .filter(m -> codigoCurso.equals(m.get("codigoCurso")))
                .map(m -> ((Number) m.get("id")).intValue())
                .findFirst()
                .orElseThrow(() -> new AssertionError("No se encontró matrícula para el curso " + codigoCurso));

        actor.remember("matriculaId", idEncontrado);
    }
}