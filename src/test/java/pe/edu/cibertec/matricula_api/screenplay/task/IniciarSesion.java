package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.Map;

public class IniciarSesion implements Task {

    private final String username;
    private final String password;

    public IniciarSesion(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static IniciarSesion conCredenciales(String username, String password) {
        return new IniciarSesion(username, password);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Post.to("/api/auth/login")
                        .with(req -> req
                                .contentType(ContentType.JSON)
                                .body(Map.of(
                                        "username", username,
                                        "password", password
                                )))
        );

        if (SerenityRest.lastResponse().statusCode() == 200) {
            var json = SerenityRest.lastResponse().jsonPath();
            String accessToken = json.getString("accessToken");
            String refreshToken = json.getString("refreshToken");
            actor.remember("accessToken", accessToken);
            actor.remember("refreshToken", refreshToken);
            actor.remember("refreshTokenOriginal", refreshToken);
        }
    }
}