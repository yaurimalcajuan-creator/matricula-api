package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import pe.edu.cibertec.matricula_api.screenplay.questions.ElCodigoDeError;
import pe.edu.cibertec.matricula_api.screenplay.questions.LaRespuesta;
import pe.edu.cibertec.matricula_api.screenplay.task.CerrarSesion;
import pe.edu.cibertec.matricula_api.screenplay.task.ConsultarPerfil;
import pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion;
import pe.edu.cibertec.matricula_api.screenplay.task.ListarAlumnos;
import pe.edu.cibertec.matricula_api.screenplay.task.RenovarToken;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

public class SeguridadStepDefinitions {

    private final String baseUrl = EnvironmentSpecificConfiguration
            .from(SystemEnvironmentVariables.currentEnvironmentVariables())
            .getProperty("restapi.baseurl");

    @Dado("que el actor {string} accede a la API de seguridad")
    public void accederApiSeguridad(String nombreActor) {
        theActorCalled(nombreActor).whoCan(CallAnApi.at(baseUrl));
    }

    @Cuando("el actor inicia sesión con usuario {string} y clave {string}")
    public void iniciarSesion(String username, String password) {
        theActorInTheSpotlight().attemptsTo(IniciarSesion.conCredenciales(username, password));
    }

    @Cuando("el actor renueva su sesión usando su refresh token actual")
    public void renovarTokenActual() {
        theActorInTheSpotlight().attemptsTo(RenovarToken.conSuRefreshTokenActual());
    }

    @Cuando("el actor intenta renovar su sesión reutilizando el refresh token original")
    public void renovarTokenOriginal() {
        theActorInTheSpotlight().attemptsTo(RenovarToken.conElRefreshTokenOriginal());
    }

    @Cuando("el actor cierra su sesión actual")
    public void cerrarSesion() {
        theActorInTheSpotlight().attemptsTo(CerrarSesion.actual());
    }

    @Cuando("el actor consulta su perfil con el access token actual")
    public void consultarPerfil() {
        theActorInTheSpotlight().attemptsTo(ConsultarPerfil.autenticado());
    }

    @Cuando("el actor intenta listar todos los alumnos")
    public void listarAlumnos() {
        theActorInTheSpotlight().attemptsTo(ListarAlumnos.todos());
    }

    @Cuando("intenta iniciar sesión fallidamente con usuario {string} validando cada intento:")
    public void intentosFallidosDeLogin(String username, DataTable tabla) {
        var filas = tabla.asMaps(String.class, String.class);

        for (var fila : filas) {
            String clave = fila.get("clave");
            int statusEsperado = Integer.parseInt(fila.get("status"));
            String codigoEsperado = fila.get("codigo");

            theActorInTheSpotlight().attemptsTo(IniciarSesion.conCredenciales(username, clave));
            theActorInTheSpotlight().should(
                    seeThat("el código HTTP del intento", LaRespuesta.codigoDeEstado(), equalTo(statusEsperado)),
                    seeThat("el código de error del intento", ElCodigoDeError.deLaRespuesta(), equalTo(codigoEsperado)),
                    seeThatResponse("cumple schemas/error.json",
                            res -> res.body(matchesJsonSchemaInClasspath("schemas/error.json")))
            );
        }
    }

    @Entonces("el actor recibe el código HTTP {int}")
    public void verificarCodigoHttp(int statusEsperado) {
        theActorInTheSpotlight().should(
                seeThat("el código HTTP", LaRespuesta.codigoDeEstado(), equalTo(statusEsperado))
        );
    }

    @Y("los tokens renovados son distintos a los anteriores")
    public void verificarTokensNuevos() {
        var actor = theActorInTheSpotlight();
        String accessActual = actor.recall("accessToken");
        String accessAnterior = actor.recall("accessTokenAnterior");
        String refreshActual = actor.recall("refreshToken");
        String refreshAnterior = actor.recall("refreshTokenAnterior");

        assertThat(accessActual, notNullValue());
        assertThat(refreshActual, notNullValue());
        assertThat(accessActual, not(equalTo(accessAnterior)));
        assertThat(refreshActual, not(equalTo(refreshAnterior)));
    }

    @Y("el código de error obtenido es {string}")
    public void verificarCodigoError(String codigoEsperado) {
        theActorInTheSpotlight().should(
                seeThat("el código de error", ElCodigoDeError.deLaRespuesta(), equalTo(codigoEsperado))
        );
    }

    @Y("la respuesta de seguridad cumple el esquema {string}")
    public void verificarEsquema(String rutaEsquema) {
        theActorInTheSpotlight().should(
                seeThatResponse("cumple el esquema " + rutaEsquema,
                        res -> res.body(matchesJsonSchemaInClasspath(rutaEsquema)))
        );
    }
}