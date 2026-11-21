# language: es
@T1 @P1
Característica: Matrícula del alumno
  Como alumno de CIBERTEC
  Quiero cotizar, registrar y pagar mi matrícula
  Para verificar el flujo completo y las reglas de negocio

  Antecedentes:
    Dado que el alumno "i202610001" inicia sesión con clave "Cibertec2026"

  Escenario: Cotizar el curso de mi fila
    Cuando cotiza el curso "QA5517" para el alumno "i202610001"
    Entonces la respuesta HTTP debe ser 200
    Y la cotización muestra 3 créditos, subtotal 360,0, descuento 20 y total 288,0

  Escenario: Matricularse y pagar el curso de mi fila
    Cuando el alumno "i202610001" registra su matrícula en el curso "QA5517"
    Entonces la respuesta HTTP debe ser 201
    Y la matrícula creada tiene estado "REGISTRADA" y monto 288,0
    Y el cuerpo de la respuesta cumple el esquema "schemas/matricula.json"
    Cuando paga la matrícula registrada usando el método "TARJETA"
    Entonces la respuesta HTTP debe ser 200
    Y la matrícula pagada tiene estado "PAGADA" y fecha de pago registrada
    Cuando consulta el listado de sus pagos
    Entonces la respuesta HTTP debe ser 200
    Y existe el pago de la matrícula con método "TARJETA" y formato de operación "^OP-[0-9]{6}$"

  Escenario: Regla de negocio que debe fallar por cruce de horario
    Cuando el alumno "i202610001" registra su matrícula en el curso "PS5511"
    Entonces la respuesta HTTP debe ser 409
    Y el error devuelto tiene código "CRUCE_HORARIO"
    Y el cuerpo de la respuesta cumple el esquema "schemas/error.json"