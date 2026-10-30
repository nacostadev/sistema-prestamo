# SistemaFinanciero

## 1. Descripción del proyecto

**SistemaFinanciero** es un proyecto desarrollado en Java que utiliza el patrón arquitectónico MVC (Modelo-Vista-Controlador), con una estructura organizada para separar la interfaz gráfica, la lógica de negocio, el control de las operaciones y el acceso a la base de datos.

En esta primera etapa se establece la estructura base del proyecto y la configuración de la conexión con MySQL. Sobre esta estructura se implementarán progresivamente las funcionalidades del sistema.

## 2. Tecnologías utilizadas

- **Lenguaje:** Java.
- **IDE:** Apache NetBeans.
- **Arquitectura:** MVC.
- **Interfaz gráfica:** Java Swing.
- **Acceso a datos:** JDBC.
- **Base de datos:** MySQL.
- **Control de versiones:** Git y GitHub.

## 3. Estructura del proyecto

```text
SistemaFinanciero/
├── lib/
│   └── mysql-connector-j-x.x.x.jar
│
├── src/
│   └── sistemafinanciero/
│       ├── Principal.java
│       │
│       ├── modelo/
│       │   ├── Cliente.java
│       │   └── ClienteService.java
│       │
│       ├── vista/
│       │   └── FrmCliente.java
│       │
│       ├── controlador/
│       │   └── ClienteController.java
│       │
│       └── persistencia/
│           ├── Conexion.java
│           └── ClienteRepository.java
│
├── nbproject/
├── build.xml
├── manifest.mf
├── .gitignore
└── README.md
```

> La estructura mostrada es una referencia inicial. Los nombres de las clases pueden ampliarse o cambiar conforme se implementen los módulos del sistema. El archivo `mysql-connector-j-x.x.x.jar` representa la versión del conector utilizada; debe sustituirse por el nombre real del archivo descargado.

## 4. Responsabilidad de los paquetes

### `modelo`

Contiene las clases que representan las entidades del sistema y la lógica de negocio.

- `Cliente.java`: representa los datos de un cliente.
- `ClienteService.java`: contiene las validaciones y reglas de negocio relacionadas con las operaciones de clientes.

### `vista`

Contiene las interfaces gráficas desarrolladas con Java Swing.

- `FrmCliente.java`: formulario para ingresar, visualizar y gestionar los datos de los clientes.

La vista no debe ejecutar consultas SQL directamente.

### `controlador`

Coordina las acciones del usuario entre la vista y la lógica de negocio.

- `ClienteController.java`: recibe las acciones del formulario y solicita las operaciones correspondientes al servicio.

### `persistencia`

Contiene las clases encargadas de la conexión con MySQL y las operaciones de acceso a datos.

- `Conexion.java`: proporciona la conexión JDBC con la base de datos.
- `ClienteRepository.java`: ejecuta las consultas SQL necesarias para registrar, actualizar, eliminar y consultar clientes.

Esta estructura utiliza una clase de acceso a datos denominada `Repository`, sin implementar el patrón DAO.

### `Principal.java`

Es el punto de entrada de la aplicación. Contiene el método `main` y permite iniciar la interfaz gráfica.

## 5. Flujo de funcionamiento

El flujo general de la aplicación será el siguiente:

1. El usuario interactúa con la vista.
2. El controlador recibe la acción.
3. El servicio valida los datos y aplica las reglas de negocio.
4. La clase de persistencia ejecuta las consultas mediante JDBC.
5. MySQL almacena o devuelve la información.
6. El resultado se comunica nuevamente a la vista.

## 6. Configuración de MySQL Connector/J

Para conectar el proyecto Java con MySQL se necesita el controlador JDBC **MySQL Connector/J**, que permite establecer la comunicación entre la aplicación y el servidor de base de datos.

### 6.1. Descargar el conector

Descarga el controlador desde la página oficial:

https://dev.mysql.com/downloads/connector/j/

Selecciona la distribución compatible con tu entorno. Si descargas un archivo ZIP, descomprímelo y localiza el archivo `.jar`.

### 6.2. Crear la carpeta `lib`

En la carpeta raíz del proyecto `SistemaFinanciero`, crea una carpeta llamada `lib`.

Copia dentro el archivo del conector. La estructura quedará así:

```text
SistemaFinanciero/
├── lib/
│   └── mysql-connector-j-x.x.x.jar
├── src/
├── nbproject/
├── build.xml
└── README.md
```

### 6.3. Agregar el `.jar` en NetBeans

1. Abre el proyecto `SistemaFinanciero` en NetBeans.
2. Ubica la sección **Libraries** en el panel del proyecto.
3. Haz clic derecho en `Libraries`.
4. Selecciona **Add JAR/Folder**.
5. Busca la carpeta `lib` dentro del proyecto.
6. Selecciona el archivo `mysql-connector-j-x.x.x.jar`.
7. Pulsa **Open**.
8. Comprueba que el conector aparezca en `Libraries`.

Es recomendable utilizar el archivo `.jar` ubicado dentro de la carpeta `lib` del proyecto y no una copia guardada en otra ubicación de la computadora.

## 7. Configuración de la conexión

En `persistencia/Conexion.java` se establecerán los parámetros de conexión a MySQL.

Ejemplo de configuración:

```java
package sistemafinanciero.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL =
            "jdbc:mysql://localhost:3306/sistemafinanciero";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                URL,
                USUARIO,
                CONTRASENA
        );
    }
}
```

**Importante:** el ejemplo presupone que el servidor MySQL está instalado y ejecutándose, que la base de datos `sistemafinanciero` existe y que las credenciales son correctas. Debes adaptar el usuario, la contraseña, el puerto y el nombre de la base de datos a tu entorno.

No publiques contraseñas reales en el repositorio.

## 8. Probar la conexión

Para comprobar que JDBC puede conectarse a MySQL, crea temporalmente una clase de prueba o ejecuta el siguiente método desde una clase Java del proyecto:

```java
package sistemafinanciero;

import java.sql.Connection;
import java.sql.SQLException;
import sistemafinanciero.persistencia.Conexion;

public class PruebaConexion {

    public static void main(String[] args) {
        try (Connection conexion = Conexion.getConnection()) {
            System.out.println(
                    "Conexión con MySQL establecida correctamente."
            );
        } catch (SQLException excepcion) {
            System.err.println(
                    "Error al conectar con MySQL: "
                    + excepcion.getMessage()
            );
        }
    }
}
```

La clase `PruebaConexion.java` es temporal y opcional. Si se crea, debe guardarse en el paquete `sistemafinanciero`. No es necesario incorporarla al sistema definitivo.

### Posibles errores

- **No suitable driver:** comprueba que el `.jar` esté agregado correctamente en `Libraries`.
- **Access denied:** verifica el usuario y la contraseña.
- **Connection refused:** comprueba que MySQL esté ejecutándose y que el puerto sea correcto.
- **Unknown database:** verifica que exista la base de datos configurada en la URL.

## 9. Control de versiones con Git

El proyecto se administra mediante Git y se aloja en GitHub.

Antes de realizar un commit, revisa los archivos modificados:

```bash
git status
```

Para registrar los cambios:

```bash
git add .
git commit -m "Documenta estructura inicial de SistemaFinanciero"
git push
```

Si se decide versionar el conector JDBC, debe estar ubicado dentro de `lib/` y no estar excluido por `.gitignore`.

También se debe evitar subir contraseñas, archivos temporales y configuraciones privadas de NetBeans.

## 10. Estado actual del proyecto

En esta etapa se define la estructura base que servirá para el desarrollo progresivo de SistemaFinanciero.

Las siguientes actividades consisten en implementar las entidades, construir las interfaces, desarrollar las reglas de negocio y conectar las operaciones con la base de datos.

La estructura se mantendrá sencilla, con responsabilidades separadas y sin incorporar patrones o capas innecesarias.