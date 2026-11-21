package pe.edu.cibertec.matricula_api.screenplay.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class ElCodigoDeError implements Question<String> {

    public static ElCodigoDeError deLaRespuesta() {
        return new ElCodigoDeError();
    }

    @Override
    public String answeredBy(Actor actor) {
        return SerenityRest.lastResponse().jsonPath().getString("codigo");
    }
}