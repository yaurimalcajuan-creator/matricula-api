package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import io.restassured.http.ContentType;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.model.environment.SystemEnvironmentVariables;

import java.util.List;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;

public class MatriculaStepDefinitions {

    private final String baseUrl = EnvironmentSpecificConfiguration
            .from(SystemEnvironmentVariables.currentEnvironmentVariables())
            .getProperty("restapi.baseurl");

    private String accessToken;
    private Integer matriculaId;

    @Dado("que el alumno {string} inicia sesión con clave {string}")
    public void iniciarSesion(String username, String password) {
        SerenityRest.given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "username", username,
                        "password", password
                ))
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200);

        this.accessToken = SerenityRest.lastResponse().jsonPath().getString("accessToken");
        assertThat("El token de acceso no debe ser nulo", this.accessToken, notNullValue());
    }

    @Cuando("cotiza el curso {string} para el alumno {string}")
    public void cotizarCurso(String codigoCurso, String codigoAlumno) {
        SerenityRest.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "codigoAlumno", codigoAlumno,
                        "cursos", List.of(codigoCurso)
                ))
                .when()
                .post("/api/matriculas/cotizacion");
    }

    @Cuando("el alumno {string} registra su matrícula en el curso {string}")
    public void registrarMatricula(String codigoAlumno, String codigoCurso) {
        SerenityRest.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "codigoAlumno", codigoAlumno,
                        "codigoCurso", codigoCurso
                ))
                .when()
                .post("/api/matriculas");

        if (SerenityRest.lastResponse().statusCode() == 201) {
            this.matriculaId = SerenityRest.lastResponse().jsonPath().getInt("id");
        }
    }

    @Cuando("paga la matrícula registrada usando el método {string}")
    public void pagarMatricula(String metodoPago) {
        assertThat("El id de la matrícula debe existir antes de pagar", this.matriculaId, notNullValue());

        SerenityRest.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .pathParam("id", this.matriculaId)
                .body(Map.of("metodo", metodoPago))
                .when()
                .patch("/api/matriculas/{id}/pagar");
    }

    @Cuando("consulta el listado de sus pagos")
    public void consultarPagos() {
        SerenityRest.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/api/pagos");
    }

    @Entonces("la respuesta HTTP debe ser {int}")
    public void verificarCodigoHttp(int statusEsperado) {
        assertThat(SerenityRest.lastResponse().statusCode(), equalTo(statusEsperado));
    }

    @Y("la cotización muestra {int} créditos, subtotal {double}, descuento {int} y total {double}")
    public void verificarCotizacion(int creditos, double subtotal, int descuento, double total) {
        var json = SerenityRest.lastResponse().jsonPath();
        assertThat(json.getInt("creditos"), equalTo(creditos));
        assertThat(json.getDouble("subtotal"), closeTo(subtotal, 0.01));
        assertThat(json.getInt("descuento"), equalTo(descuento));
        assertThat(json.getDouble("total"), closeTo(total, 0.01));
    }

    @Y("la matrícula creada tiene estado {string} y monto {double}")
    public void verificarMatriculaCreada(String estadoEsperado, double montoEsperado) {
        var json = SerenityRest.lastResponse().jsonPath();
        assertThat(json.getString("estado"), equalTo(estadoEsperado));
        assertThat(json.getDouble("monto"), closeTo(montoEsperado, 0.01));
        assertThat(this.matriculaId, notNullValue());
    }

    @Y("la matrícula pagada tiene estado {string} y fecha de pago registrada")
    public void verificarMatriculaPagada(String estadoEsperado) {
        var json = SerenityRest.lastResponse().jsonPath();
        assertThat(json.getString("estado"), equalTo(estadoEsperado));
        assertThat(json.getString("fechaPago"), notNullValue());
    }

    @Y("existe el pago de la matrícula con método {string} y formato de operación {string}")
    public void verificarPagoEnListado(String metodoEsperado, String patronOperacion) {
        var pagos = SerenityRest.lastResponse().jsonPath().getList("$", Map.class);

        var pagoEncontrado = pagos.stream()
                .filter(p -> p.get("idMatricula") != null
                        && ((Number) p.get("idMatricula")).intValue() == this.matriculaId)
                .findFirst()
                .orElse(null);

        assertThat("Debe existir un pago asociado al idMatricula " + this.matriculaId, pagoEncontrado, notNullValue());
        assertThat((String) pagoEncontrado.get("metodo"), equalTo(metodoEsperado));
        assertThat((String) pagoEncontrado.get("numeroOperacion"), matchesPattern(patronOperacion));
    }

    @Y("el error devuelto tiene código {string}")
    public void verificarCodigoError(String codigoEsperado) {
        assertThat(SerenityRest.lastResponse().jsonPath().getString("codigo"), equalTo(codigoEsperado));
    }

    @Y("el cuerpo de la respuesta cumple el esquema {string}")
    public void verificarEsquemaJson(String rutaEsquema) {
        SerenityRest.lastResponse().then()
                .body(matchesJsonSchemaInClasspath(rutaEsquema));
    }
}