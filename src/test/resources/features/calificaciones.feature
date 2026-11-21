# language: es
@T1 @P3
Característica: Calificación de matrículas
  Como docente de CIBERTEC
  Quiero registrar las notas finales de los cursos pagados
  Para que el historial académico actualice los créditos aprobados y el promedio ponderado

  Esquema del escenario: El docente registra la nota y el historial se actualiza
    Dado que la alumna "Rosa" inicia sesión con usuario "i202610005" y clave "Cibertec2026"
    Y la alumna obtiene el id de su matrícula del curso "PI5620"
    Cuando el docente "D0001" con clave "Cibertec2026" registra la nota <nota> en esa matrícula
    Entonces la calificación responde con código HTTP 200
    Y la matrícula calificada tiene condición "<condicion>"
    Cuando la alumna consulta su historial académico "i202610005"
    Entonces el historial responde con código HTTP 200
    Y el historial muestra condición "<condicion>" para el curso "PI5620", <creditosAprobados> créditos aprobados y promedio ponderado <promedio>
    Y el historial cumple el esquema "schemas/historial.json"

    Ejemplos:
      | nota | condicion   | creditosAprobados | promedio |
      | 19,0 | APROBADO    | 13                | 17,08    |
      | 12,5 | DESAPROBADO | 7                 | 14,08    |

  Escenario: Un docente que no dicta el curso intenta registrar la nota
    Dado que la alumna "Rosa" inicia sesión con usuario "i202610005" y clave "Cibertec2026"
    Y la alumna obtiene el id de su matrícula del curso "PI5620"
    Cuando el docente "D0002" con clave "Cibertec2026" registra la nota 15,0 en esa matrícula
    Entonces la calificación responde con código HTTP 403
    Y el error de calificación tiene código "ACCESO_DENEGADO"
    Y la respuesta de calificación cumple el esquema "schemas/error.json"

  Escenario: No se puede calificar una matrícula que no está pagada
    Dado que la alumna "Rosa" inicia sesión con usuario "i202610005" y clave "Cibertec2026"
    Y la alumna obtiene el id de su matrícula del curso "PS5511"
    Cuando el docente "D0001" con clave "Cibertec2026" registra la nota 16,0 en esa matrícula
    Entonces la calificación responde con código HTTP 409
    Y el error de calificación tiene código "MATRICULA_NO_PAGADA"
    Y la respuesta de calificación cumple el esquema "schemas/error.json"