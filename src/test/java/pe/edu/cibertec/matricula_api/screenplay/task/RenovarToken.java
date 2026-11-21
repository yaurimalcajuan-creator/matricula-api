package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.Map;

public class RenovarToken implements Task {

    private final String claveMemoria;

    public RenovarToken(String claveMemoria) {
        this.claveMemoria = claveMemoria;
    }

    public static RenovarToken conSuRefreshTokenActual() {
        return new RenovarToken("refreshToken");
    }

    public static RenovarToken conElRefreshTokenOriginal() {
        return new RenovarToken("refreshTokenOriginal");
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String refreshAEnviar = actor.recall(claveMemoria);

        actor.attemptsTo(
                Post.to("/api/auth/refresh")
                        .with(req -> req
                                .contentType(ContentType.JSON)
                                .body(Map.of("refreshToken", refreshAEnviar)))
        );

        if (SerenityRest.lastResponse().statusCode() == 200) {
            var json = SerenityRest.lastResponse().jsonPath();
            String accessAnterior = actor.recall("accessToken");
            String refreshAnterior = actor.recall("refreshToken");
            actor.remember("accessTokenAnterior", accessAnterior);
            actor.remember("refreshTokenAnterior", refreshAnterior);
            actor.remember("accessToken", json.getString("accessToken"));
            actor.remember("refreshToken", json.getString("refreshToken"));
        }
    }
}