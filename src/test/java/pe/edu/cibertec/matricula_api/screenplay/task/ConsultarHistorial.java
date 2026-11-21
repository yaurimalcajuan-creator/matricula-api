package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

public class ConsultarHistorial implements Task {

    private final String codigoAlumno;

    public ConsultarHistorial(String codigoAlumno) {
        this.codigoAlumno = codigoAlumno;
    }

    public static ConsultarHistorial delAlumno(String codigoAlumno) {
        return new ConsultarHistorial(codigoAlumno);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("accessToken");

        actor.attemptsTo(
                Get.resource("/api/alumnos/{codigo}/historial")
                        .with(req -> req
                                .header("Authorization", "Bearer " + token)
                                .pathParam("codigo", codigoAlumno))
        );
    }
}