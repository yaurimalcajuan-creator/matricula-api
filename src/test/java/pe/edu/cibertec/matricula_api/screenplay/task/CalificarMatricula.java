package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Patch;

import java.util.Map;

public class CalificarMatricula implements Task {

    private final int idMatricula;
    private final double nota;

    public CalificarMatricula(int idMatricula, double nota) {
        this.idMatricula = idMatricula;
        this.nota = nota;
    }

    public static CalificarMatricula conIdYNota(int idMatricula, double nota) {
        return new CalificarMatricula(idMatricula, nota);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("accessToken");

        actor.attemptsTo(
                Patch.to("/api/matriculas/{id}/nota")
                        .with(req -> req
                                .header("Authorization", "Bearer " + token)
                                .pathParam("id", idMatricula)
                                .contentType(ContentType.JSON)
                                .body(Map.of("nota", nota)))
        );
    }
}