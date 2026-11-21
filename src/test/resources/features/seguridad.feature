# language: es
@T1 @P2
Característica: Seguridad de la sesión
  Como responsable de calidad de CIBERTEC
  Quiero validar la rotación de tokens, el cierre de sesión, el bloqueo de cuenta y los permisos
  Para garantizar la seguridad de la API de matrícula

  Escenario: Rotación del refresh token
    Dado que el actor "Ana" accede a la API de seguridad
    Y el actor inicia sesión con usuario "i202610001" y clave "Cibertec2026"
    Cuando el actor renueva su sesión usando su refresh token actual
    Entonces el actor recibe el código HTTP 200
    Y los tokens renovados son distintos a los anteriores
    Cuando el actor intenta renovar su sesión reutilizando el refresh token original
    Entonces el actor recibe el código HTTP 401
    Y el código de error obtenido es "REFRESH_REUTILIZADO"
    Y la respuesta de seguridad cumple el esquema "schemas/error.json"
    Cuando el actor consulta su perfil con el access token actual
    Entonces el actor recibe el código HTTP 401
    Y el código de error obtenido es "TOKEN_INVALIDO"
    Y la respuesta de seguridad cumple el esquema "schemas/error.json"

  Escenario: Cierre de sesión
    Dado que el actor "Ana" accede a la API de seguridad
    Y el actor inicia sesión con usuario "i202610001" y clave "Cibertec2026"
    Cuando el actor cierra su sesión actual
    Entonces el actor recibe el código HTTP 204
    Cuando el actor consulta su perfil con el access token actual
    Entonces el actor recibe el código HTTP 401
    Y el código de error obtenido es "TOKEN_INVALIDO"
    Y la respuesta de seguridad cumple el esquema "schemas/error.json"

  Escenario: Bloqueo de cuenta por intentos fallidos
    Dado que el actor "Jorge" accede a la API de seguridad
    Cuando intenta iniciar sesión fallidamente con usuario "i202610004" validando cada intento:
      | intento | clave       | status | codigo                 |
      | 1       | ClaveMala1  | 401    | CREDENCIALES_INVALIDAS |
      | 2       | ClaveMala2  | 401    | CREDENCIALES_INVALIDAS |
      | 3       | ClaveMala3  | 401    | CREDENCIALES_INVALIDAS |
      | 4       | ClaveMala4  | 401    | CREDENCIALES_INVALIDAS |
      | 5       | ClaveMala5  | 423    | CUENTA_BLOQUEADA       |
    Cuando el actor inicia sesión con usuario "i202610004" y clave "Cibertec2026"
    Entonces el actor recibe el código HTTP 423
    Y el código de error obtenido es "CUENTA_BLOQUEADA"
    Y la respuesta de seguridad cumple el esquema "schemas/error.json"

  Escenario: Alumno sin permiso de administrador
    Dado que el actor "Ana" accede a la API de seguridad
    Y el actor inicia sesión con usuario "i202610001" y clave "Cibertec2026"
    Cuando el actor intenta listar todos los alumnos
    Entonces el actor recibe el código HTTP 403
    Y el código de error obtenido es "ACCESO_DENEGADO"
    Y la respuesta de seguridad cumple el esquema "schemas/error.json"