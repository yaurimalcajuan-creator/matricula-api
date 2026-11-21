package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import pe.edu.cibertec.matricula_api.screenplay.questions.ElCodigoDeError;
import pe.edu.cibertec.matricula_api.screenplay.questions.ElHistorial;
import pe.edu.cibertec.matricula_api.screenplay.questions.LaRespuesta;
import pe.edu.cibertec.matricula_api.screenplay.task.CalificarMatricula;
import pe.edu.cibertec.matricula_api.screenplay.task.ConsultarHistorial;
import pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion;
import pe.edu.cibertec.matricula_api.screenplay.task.ObtenerIdMatricula;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;

public class CalificacionStepDefinitions {

    private final String baseUrl = EnvironmentSpecificConfiguration
            .from(SystemEnvironmentVariables.currentEnvironmentVariables())
            .getProperty("restapi.baseurl");

    private String nombreAlumna;
    private String nombreDocente;
    private Integer matriculaId;

    @Dado("que la alumna {string} inicia sesión con usuario {string} y clave {string}")
    public void alumnaIniciaSesion(String nombre, String username, String password) {
        this.nombreAlumna = nombre;
        var alumna = theActorCalled(nombre);
        alumna.whoCan(CallAnApi.at(baseUrl));
        alumna.attemptsTo(IniciarSesion.conCredenciales(username, password));
    }

    @Y("la alumna obtiene el id de su matrícula del curso {string}")
    public void alumnaObtieneIdMatricula(String codigoCurso) {
        var alumna = theActorCalled(nombreAlumna);
        alumna.attemptsTo(ObtenerIdMatricula.delCurso(codigoCurso));
        this.matriculaId = alumna.recall("matriculaId");
    }

    @Cuando("el docente {string} con clave {string} registra la nota {double} en esa matrícula")
    public void docenteRegistraNota(String codigoDocente, String password, double nota) {
        this.nombreDocente = codigoDocente;
        var docente = theActorCalled(codigoDocente);
        docente.whoCan(CallAnApi.at(baseUrl));
        docente.attemptsTo(
                IniciarSesion.conCredenciales(codigoDocente, password),
                CalificarMatricula.conIdYNota(matriculaId, nota)
        );
    }

    @Entonces("la calificación responde con código HTTP {int}")
    public void verificarStatusCalificacion(int statusEsperado) {
        theActorCalled(nombreDocente).should(
                seeThat("el código HTTP de calificación", LaRespuesta.codigoDeEstado(), equalTo(statusEsperado))
        );
    }

    @Y("la matrícula calificada tiene condición {string}")
    public void verificarCondicionMatricula(String condicionEsperada) {
        theActorCalled(nombreDocente).should(
                seeThat("la condición de la matrícula", LaRespuesta.campo("condicion"), equalTo(condicionEsperada))
        );
    }

    @Cuando("la alumna consulta su historial académico {string}")
    public void alumnaConsultaHistorial(String codigoAlumno) {
        theActorCalled(nombreAlumna).attemptsTo(ConsultarHistorial.delAlumno(codigoAlumno));
    }

    @Entonces("el historial responde con código HTTP {int}")
    public void verificarStatusHistorial(int statusEsperado) {
        theActorCalled(nombreAlumna).should(
                seeThat("el código HTTP del historial", LaRespuesta.codigoDeEstado(), equalTo(statusEsperado))
        );
    }

    @Y("el historial muestra condición {string} para el curso {string}, {int} créditos aprobados y promedio ponderado {double}")
    public void verificarHistorialActualizado(String condicion, String codigoCurso, int creditosAprobados, double promedio) {
        theActorCalled(nombreAlumna).should(
                seeThat("la condición en el historial", ElHistorial.condicionDelCurso(codigoCurso), equalTo(condicion)),
                seeThat("los créditos aprobados", ElHistorial.creditosAprobados(), equalTo(creditosAprobados)),
                seeThat("el promedio ponderado", ElHistorial.promedioPonderado(), closeTo(promedio, 0.01))
        );
    }

    @Y("el historial cumple el esquema {string}")
    public void verificarEsquemaHistorial(String rutaEsquema) {
        theActorCalled(nombreAlumna).should(
                seeThatResponse("cumple el esquema " + rutaEsquema,
                        res -> res.body(matchesJsonSchemaInClasspath(rutaEsquema)))
        );
    }

    @Y("el error de calificación tiene código {string}")
    public void verificarErrorCalificacion(String codigoEsperado) {
        theActorCalled(nombreDocente).should(
                seeThat("el código de error de calificación", ElCodigoDeError.deLaRespuesta(), equalTo(codigoEsperado))
        );
    }

    @Y("la respuesta de calificación cumple el esquema {string}")
    public void verificarEsquemaErrorCalificacion(String rutaEsquema) {
        theActorCalled(nombreDocente).should(
                seeThatResponse("cumple el esquema " + rutaEsquema,
                        res -> res.body(matchesJsonSchemaInClasspath(rutaEsquema)))
        );
    }
}