package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

public class ConsultarPerfil implements Task {

    public static ConsultarPerfil autenticado() {
        return new ConsultarPerfil();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("accessToken");
        actor.attemptsTo(
                Get.resource("/api/auth/perfil")
                        .with(req -> req.header("Authorization", "Bearer " + token))
        );
    }
}