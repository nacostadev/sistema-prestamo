/* ============================================================================
                      BASE DE DATOS GENERAL
                SOFTWARE DE PRÉSTAMO FINANCIERO
   ============================================================================

   DESCRIPCIÓN:
   Este script permite crear la base de datos BDGestionCliente, incluyendo
   sus esquemas, tablas, relaciones, restricciones de integridad, índices
   y datos iniciales necesarios para el desarrollo y las pruebas del
   prototipo de software de préstamo financiero.

   El diseño se fundamenta en las reglas de negocio y los criterios de
   aceptación establecidos en las siguientes historias de usuario:

       HU-01: Gestionar Cliente.
       HU-02: Registrar Préstamo Financiero.
       HU-03: Generar Cronograma de Pagos.

   ============================================================================
                      RESPONSABILIDADES DEL SISTEMA
   ============================================================================

   SQL SERVER:
   - Almacenar y administrar la información de clientes, préstamos y
     cronogramas de pagos.
   - Mantener las relaciones entre las tablas mediante claves primarias
     y foráneas, así como restricciones de integridad.
   - Ejecutar las operaciones de consulta, inserción y actualización
     solicitadas desde Java mediante JDBC (procedimientos almacenados para
     cliente y SQL directo para préstamo y cronograma).
   - Gestionar transacciones para garantizar la consistencia de los datos.

   JAVA (NETBEANS):
   - Implementar las reglas de negocio y los criterios de aceptación.
   - Autenticar al Analista de Crédito.
   - Validar los datos de clientes y las condiciones de los préstamos.
   - Verificar montos, plazos y tasas según el tipo de préstamo.
   - Calcular la TEM, cuota fija, intereses, amortizaciones y saldos.
   - Generar el cronograma de pagos y no permitir su edición manual.
   - Gestionar los formularios y mostrar mensajes al usuario.

   ============================================================================
                       CONSIDERACIONES DE EJECUCIÓN
   ============================================================================

   1. Si BDGestionCliente existe, será eliminada y creada nuevamente.
   2. La eliminación borrará las tablas y los datos almacenados.
   3. Se recomienda realizar una copia de seguridad antes de ejecutar.
   4. El script está destinado al desarrollo y pruebas del prototipo.
   5. Los datos iniciales son simulados y se utilizan para realizar pruebas.
   6. Las operaciones de cliente se ejecutan mediante procedimientos
      almacenados de tipo CRUD (sp_Cliente_xxxx). Los préstamos y los
      cronogramas se guardan y consultan con SQL directo (INSERT y SELECT)
      desde Java. Las reglas de negocio, las validaciones y los cálculos
      financieros se implementan en Java (clases del modelo), no en SQL Server.

   ============================================================================
                              INTEGRANTES
   ============================================================================

   - Acosta Plasencia Naylin Atenas
   - Chuquipoma Medina Sthefany Darley
   - Cruzado Soplopuco Jonathan Levi
   - Mantilla Sánchez Elsa Lucía
   - Robles Loje Ernesto Javier
   - Vásquez Zamora Anthony Aaron

   ============================================================================ */


USE master;
GO
IF DB_ID('BDGestionCliente') IS NOT NULL
BEGIN
    ALTER DATABASE BDGestionCliente SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE BDGestionCliente;
END
GO

EXEC xp_create_subdir 'C:\Datos\GestionCliente';
GO
EXEC xp_create_subdir 'C:\Bases\GestionCliente';
GO

CREATE DATABASE BDGestionCliente
	ON PRIMARY
	(Name = 'GestionClienteData01', Filename = 'C:\Bases\GestionCliente\GestionClienteData01.mdf',
	 SIZE = 20MB, MaxSize = 10GB, FILEGROWTH = 100MB),

	Filegroup Privacidad
	(Name = 'GestionClientePrivacidad01', Filename = 'C:\Datos\GestionCliente\GestionClientePrivacidad01.ndf',
	 SIZE = 50MB, MaxSize = unlimited, FILEGROWTH = 200MB),

	Filegroup Registros
	(Name = 'GestionClienteRegistros01', Filename = 'C:\Bases\GestionCliente\GestionClienteRegistros01.ndf',
	 SIZE = 50MB, MaxSize = unlimited, FILEGROWTH = 200MB)

	LOG ON
	(Name = 'GestionClienteLog01', Filename = 'C:\Bases\GestionCliente\GestionClienteLog01.ldf',
	 SIZE = 100MB, MaxSize = unlimited, FILEGROWTH = 200MB);
GO

USE BDGestionCliente;
GO

-- =======================================================================
--					Creación de los esquemas con consistencia
-- =======================================================================
CREATE SCHEMA Seguridad;
GO
CREATE SCHEMA Personal;
GO
CREATE SCHEMA Cliente;
GO
CREATE SCHEMA Prestamo;
GO
CREATE SCHEMA Cronograma;
GO

-- ==========================================================
-- SEGURIDAD Y PERSONAL - HU-01 RN-07 / HU-02 RN-07
-- Analista de Crédito autenticado en el sistema
-- ==========================================================

CREATE TABLE Personal.Analistas
(
    AnalistasCodigo     NCHAR(4) NOT NULL,
    AnalistasApellidos  NVARCHAR(100) NOT NULL,
    AnalistasNombres    NVARCHAR(100) NOT NULL,

    CONSTRAINT AnalistasCodigoPK PRIMARY KEY (AnalistasCodigo)
)
ON Privacidad;
GO

CREATE TABLE Seguridad.Usuarios
(
    UsuariosCodigo       NCHAR(4) NOT NULL,
    UsuariosNombre       NVARCHAR(50) NOT NULL,
    UsuariosContrasenia  VARCHAR(100) NOT NULL,
    UsuariosEstado       NCHAR(1) NOT NULL CONSTRAINT UsuariosEstadoDF DEFAULT 'A',
    AnalistasCodigo      NCHAR(4) NOT NULL,

    CONSTRAINT UsuariosPK PRIMARY KEY (UsuariosCodigo),
    CONSTRAINT UsuariosNombreUQ UNIQUE (UsuariosNombre),
    CONSTRAINT UsuariosEstadoCK CHECK (UsuariosEstado IN ('A', 'I')),
    CONSTRAINT UsuariosAnalistasFK FOREIGN KEY (AnalistasCodigo)
        REFERENCES Personal.Analistas(AnalistasCodigo)
)
ON Privacidad;
GO

-- ==========================================================
-- HU-01: GESTIONAR CLIENTE
-- ==========================================================
CREATE TABLE Cliente.TiposDocumento
(
    TiposDocumentoCodigo   NCHAR(4) NOT NULL,
    TiposDocumentoNombre   NVARCHAR(30) NOT NULL,

    CONSTRAINT TiposDocumentoPK PRIMARY KEY (TiposDocumentoCodigo),
    CONSTRAINT TiposDocumentoNombreUQ UNIQUE (TiposDocumentoNombre)
)
ON Registros;
GO

CREATE TABLE Cliente.EstadosCiviles
(
    EstadosCivilesCodigo NCHAR(4) NOT NULL,
    EstadosCivilesNombre NVARCHAR(30) NOT NULL,

    CONSTRAINT EstadosCivilesPK PRIMARY KEY (EstadosCivilesCodigo),
    CONSTRAINT EstadosCivilesNombreUQ UNIQUE (EstadosCivilesNombre)
)
ON Registros;
GO

-- RN-03: campos obligatorios.
-- RN-04: campos opcionales (cónyuge, referencias personales y observaciones).
-- Estado A/I: desactivación del cliente (no se elimina físicamente).
CREATE TABLE Cliente.Clientes
(
    ClientesCodigo                  NCHAR(4) NOT NULL,
    TiposDocumentoCodigo            NCHAR(4) NOT NULL,
    ClientesNumeroDocumento         NVARCHAR(12) NOT NULL,
    ClientesApellidos               NVARCHAR(100) NOT NULL,
    ClientesNombres                 NVARCHAR(100) NOT NULL,
    ClientesFechaNacimiento         DATE NOT NULL,
    EstadosCivilesCodigo            NCHAR(4) NOT NULL,
    ClientesDireccion               NVARCHAR(150) NOT NULL,
    ClientesTelefono                NCHAR(9) NOT NULL,
    ClientesCorreo                  NVARCHAR(100) NOT NULL,
    ClientesCondicionLaboral        NVARCHAR(50) NOT NULL,
    ClientesNivelIngresos           DECIMAL(18,2) NOT NULL,
    ClientesNombreConyuge           NVARCHAR(200) NULL,
    ClientesReferenciasPersonales   NVARCHAR(255) NULL,
    ClientesObservaciones           NVARCHAR(255) NULL,
    ClientesEstado                  NCHAR(1) NOT NULL CONSTRAINT ClientesEstadoDF DEFAULT 'A',

    CONSTRAINT ClientesPK PRIMARY KEY (ClientesCodigo),
    CONSTRAINT ClientesTiposDocumentoFK FOREIGN KEY (TiposDocumentoCodigo)
        REFERENCES Cliente.TiposDocumento(TiposDocumentoCodigo),
    CONSTRAINT ClientesEstadosCivilesFK FOREIGN KEY (EstadosCivilesCodigo)
        REFERENCES Cliente.EstadosCiviles(EstadosCivilesCodigo),
    CONSTRAINT ClientesDocumentoUQ UNIQUE (ClientesNumeroDocumento),
    CONSTRAINT ClientesEstadoCK CHECK (ClientesEstado IN ('A', 'I')),
    CONSTRAINT ClientesNivelIngresosCK CHECK (ClientesNivelIngresos >= 0)
)
ON Privacidad;
GO


-- ==========================================================
-- HU-02: REGISTRAR PRÉSTAMO FINANCIERO
-- ==========================================================

-- Tipo de préstamo: campo obligatorio (RN-02).
-- RN-05: condiciones de plazo y tasa por tipo. Solo se almacenan como datos;
-- la validación se realiza en Java.
CREATE TABLE Prestamo.TiposPrestamo
(
    TiposPrestamoCodigo       NCHAR(4) NOT NULL,
    TiposPrestamoNombre       NVARCHAR(50) NOT NULL,
    TiposPrestamoPlazoMinimo  INT NOT NULL,
    TiposPrestamoPlazoMaximo  INT NOT NULL,
    TiposPrestamoTasaMinima   DECIMAL(6,4) NOT NULL,
    TiposPrestamoTasaMaxima   DECIMAL(6,4) NOT NULL,

    CONSTRAINT TiposPrestamoPK PRIMARY KEY (TiposPrestamoCodigo),
    CONSTRAINT TiposPrestamoNombreUQ UNIQUE (TiposPrestamoNombre),
    CONSTRAINT TiposPrestamoPlazoCK CHECK
        (TiposPrestamoPlazoMinimo > 0
         AND TiposPrestamoPlazoMaximo >= TiposPrestamoPlazoMinimo),
    CONSTRAINT TiposPrestamoTasaCK CHECK
        (TiposPrestamoTasaMinima > 0
         AND TiposPrestamoTasaMaxima >= TiposPrestamoTasaMinima)
)
ON Registros;
GO


CREATE TABLE Prestamo.ModalidadesPago
(
    ModalidadesPagoCodigo  NCHAR(4) NOT NULL,
    ModalidadesPagoNombre  NVARCHAR(30) NOT NULL,

    CONSTRAINT ModalidadesPagoPK PRIMARY KEY (ModalidadesPagoCodigo),
    CONSTRAINT ModalidadesPagoNombreUQ UNIQUE (ModalidadesPagoNombre)
)
ON Registros;
GO


CREATE TABLE Prestamo.Monedas
(
    MonedasCodigo   NCHAR(4) NOT NULL,
    MonedasNombre   NVARCHAR(30) NOT NULL,
    MonedasSimbolo  NVARCHAR(3) NOT NULL,

    CONSTRAINT MonedasPK PRIMARY KEY (MonedasCodigo),
    CONSTRAINT MonedasNombreUQ UNIQUE (MonedasNombre)
)
ON Registros;
GO


-- Estados mencionados en las HU: Vigente, Moroso, Incumplimiento, En evaluación
CREATE TABLE Prestamo.EstadosPrestamo
(
    EstadosPrestamoCodigo  NCHAR(4) NOT NULL,
    EstadosPrestamoNombre  NVARCHAR(30) NOT NULL,

    CONSTRAINT EstadosPrestamoPK PRIMARY KEY (EstadosPrestamoCodigo),
    CONSTRAINT EstadosPrestamoNombreUQ UNIQUE (EstadosPrestamoNombre)
)
ON Registros;
GO


CREATE TABLE Prestamo.Garantias
(
    GarantiasCodigo       NCHAR(4) NOT NULL,
    GarantiasDescripcion  NVARCHAR(150) NOT NULL,

    CONSTRAINT GarantiasPK PRIMARY KEY (GarantiasCodigo)
)
ON Registros;
GO


-- RN-04: límites mínimo y máximo de monto según la política crediticia.
-- Solo se almacenan como datos; la validación se realiza en Java.
CREATE TABLE Prestamo.PoliticaCredito
(
    PoliticaCreditoCodigo       NCHAR(4) NOT NULL,
    TiposPrestamoCodigo         NCHAR(4) NOT NULL,
    PoliticaCreditoMontoMinimo  DECIMAL(18,2) NOT NULL,
    PoliticaCreditoMontoMaximo  DECIMAL(18,2) NOT NULL,

    CONSTRAINT PoliticaCreditoPK PRIMARY KEY (PoliticaCreditoCodigo),
    CONSTRAINT PoliticaCreditoTiposPrestamoFK
        FOREIGN KEY (TiposPrestamoCodigo)
        REFERENCES Prestamo.TiposPrestamo(TiposPrestamoCodigo),
    CONSTRAINT PoliticaCreditoTiposPrestamoUQ UNIQUE (TiposPrestamoCodigo),
    CONSTRAINT PoliticaCreditoMontoCK CHECK
        (PoliticaCreditoMontoMinimo > 0
         AND PoliticaCreditoMontoMaximo >= PoliticaCreditoMontoMinimo)
)
ON Registros;
GO


-- RN-02: campos obligatorios (cliente, tipo, monto, plazo, TEA, modalidad,
--        fecha de desembolso y moneda).
-- RN-03: campos opcionales (garantía, observaciones y descripción del destino).
-- El estado se registra como Vigente (lo asigna Java al registrar).
-- La TEA se almacena como fracción decimal (0.2000 = 20 %).
CREATE TABLE Prestamo.Prestamos
(
    PrestamosCodigo              NCHAR(6) NOT NULL,
    ClientesCodigo               NCHAR(4) NOT NULL,
    TiposPrestamoCodigo          NCHAR(4) NOT NULL,
    PrestamosMontoSolicitado     DECIMAL(18,2) NOT NULL,
    PrestamosPlazoCuotas         INT NOT NULL,
    PrestamosTEA                 DECIMAL(6,4) NOT NULL,
    ModalidadesPagoCodigo        NCHAR(4) NOT NULL,
    PrestamosFechaDesembolso     DATE NOT NULL,
    MonedasCodigo                NCHAR(4) NOT NULL,
    GarantiasCodigo              NCHAR(4) NULL,
    PrestamosObservaciones       NVARCHAR(255) NULL,
    PrestamosDescripcionDestino  NVARCHAR(255) NULL,
    EstadosPrestamoCodigo        NCHAR(4) NOT NULL,

    CONSTRAINT PrestamosPK PRIMARY KEY (PrestamosCodigo),
    CONSTRAINT PrestamosClientesFK FOREIGN KEY (ClientesCodigo)
        REFERENCES Cliente.Clientes(ClientesCodigo),
    CONSTRAINT PrestamosTiposPrestamoFK FOREIGN KEY (TiposPrestamoCodigo)
        REFERENCES Prestamo.TiposPrestamo(TiposPrestamoCodigo),
    CONSTRAINT PrestamosModalidadesPagoFK FOREIGN KEY (ModalidadesPagoCodigo)
        REFERENCES Prestamo.ModalidadesPago(ModalidadesPagoCodigo),
    CONSTRAINT PrestamosMonedasFK FOREIGN KEY (MonedasCodigo)
        REFERENCES Prestamo.Monedas(MonedasCodigo),
    CONSTRAINT PrestamosGarantiasFK FOREIGN KEY (GarantiasCodigo)
        REFERENCES Prestamo.Garantias(GarantiasCodigo),
    CONSTRAINT PrestamosEstadosPrestamoFK FOREIGN KEY (EstadosPrestamoCodigo)
        REFERENCES Prestamo.EstadosPrestamo(EstadosPrestamoCodigo),
    CONSTRAINT PrestamosMontoCK CHECK (PrestamosMontoSolicitado > 0),
    CONSTRAINT PrestamosPlazoCK CHECK (PrestamosPlazoCuotas > 0),
    CONSTRAINT PrestamosTEACK CHECK (PrestamosTEA > 0)
)
ON Registros;
GO

-- Refuerzo de RN01, RN04, RN05 y RN06 para inserciones directas.
-- Los valores de política se mantienen en las filas actuales de PoliticaCredito
-- y TiposPrestamo; no se redefinen en este trigger.
CREATE OR ALTER TRIGGER Prestamo.trg_Prestamos_ValidarHU02
ON Prestamo.Prestamos
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (
        SELECT 1
        FROM inserted AS i
        LEFT JOIN Cliente.Clientes AS c ON c.ClientesCodigo = i.ClientesCodigo
        WHERE c.ClientesCodigo IS NULL OR c.ClientesEstado <> 'A'
    )
        THROW 51001, 'HU-02 RN01: el cliente debe estar registrado y activo.', 1;

    IF EXISTS (
        SELECT 1
        FROM inserted AS i
        LEFT JOIN Prestamo.TiposPrestamo AS t ON t.TiposPrestamoCodigo = i.TiposPrestamoCodigo
        LEFT JOIN Prestamo.PoliticaCredito AS pc ON pc.TiposPrestamoCodigo = i.TiposPrestamoCodigo
        WHERE t.TiposPrestamoCodigo IS NULL
           OR pc.TiposPrestamoCodigo IS NULL
           OR i.PrestamosMontoSolicitado < pc.PoliticaCreditoMontoMinimo
           OR i.PrestamosMontoSolicitado > pc.PoliticaCreditoMontoMaximo
    )
        THROW 51002, 'HU-02 RN04: el monto está fuera de los límites crediticios.', 1;

    IF EXISTS (
        SELECT 1
        FROM inserted AS i
        INNER JOIN Prestamo.TiposPrestamo AS t ON t.TiposPrestamoCodigo = i.TiposPrestamoCodigo
        WHERE i.PrestamosPlazoCuotas < t.TiposPrestamoPlazoMinimo
           OR i.PrestamosPlazoCuotas > t.TiposPrestamoPlazoMaximo
           OR i.PrestamosTEA < t.TiposPrestamoTasaMinima
           OR i.PrestamosTEA > t.TiposPrestamoTasaMaxima
    )
        THROW 51003, 'HU-02 RN05: el plazo o la TEA no cumplen las condiciones del tipo.', 1;

    IF EXISTS (
        SELECT 1
        FROM inserted AS i
        INNER JOIN Prestamo.Prestamos AS p
            ON p.ClientesCodigo = i.ClientesCodigo
           AND p.PrestamosCodigo <> i.PrestamosCodigo
        INNER JOIN Prestamo.EstadosPrestamo AS e
            ON e.EstadosPrestamoCodigo = p.EstadosPrestamoCodigo
        WHERE e.EstadosPrestamoNombre IN (N'Moroso', N'Incumplimiento')
    )
        THROW 51004, 'HU-02 RN06: el cliente tiene un préstamo moroso o en incumplimiento.', 1;
END;
GO


-- ==========================================================
-- HU-03: GENERAR CRONOGRAMA DE PAGOS
-- ==========================================================

-- RN-06: el cronograma se guarda y se asocia al préstamo.
CREATE TABLE Cronograma.Cronogramas
(
    CronogramasCodigo  NCHAR(6) NOT NULL,
    PrestamosCodigo    NCHAR(6) NOT NULL,

    CONSTRAINT CronogramasPK PRIMARY KEY (CronogramasCodigo),
    CONSTRAINT CronogramasPrestamosFK FOREIGN KEY (PrestamosCodigo)
        REFERENCES Prestamo.Prestamos(PrestamosCodigo),
    CONSTRAINT CronogramasPrestamosUQ UNIQUE (PrestamosCodigo)
)
ON Registros;
GO


-- RN-07: número de cuota, fecha de vencimiento, saldo anterior, interés,
--        amortización, cuota y saldo pendiente.
CREATE TABLE Cronograma.DetalleCronograma
(
    DetalleCronogramaCodigo  NCHAR(8) NOT NULL,
    CronogramasCodigo        NCHAR(6) NOT NULL,
    DetalleNumeroCuota       INT NOT NULL,
    DetalleFechaVencimiento  DATE NOT NULL,
    DetalleSaldoAnterior     DECIMAL(18,2) NOT NULL,
    DetalleInteres           DECIMAL(18,2) NOT NULL,
    DetalleAmortizacion      DECIMAL(18,2) NOT NULL,
    DetalleCuota             DECIMAL(18,2) NOT NULL,
    DetalleSaldoPendiente    DECIMAL(18,2) NOT NULL,

    CONSTRAINT DetalleCronogramaPK PRIMARY KEY (DetalleCronogramaCodigo),
    CONSTRAINT DetalleCronogramaCronogramasFK FOREIGN KEY (CronogramasCodigo)
        REFERENCES Cronograma.Cronogramas(CronogramasCodigo),
    CONSTRAINT DetalleCronogramaNumeroUQ UNIQUE (CronogramasCodigo, DetalleNumeroCuota),
    CONSTRAINT DetalleCronogramaNumeroCK CHECK (DetalleNumeroCuota > 0),
    CONSTRAINT DetalleCronogramaMontosCK CHECK
        (DetalleSaldoAnterior >= 0
         AND DetalleInteres >= 0
         AND DetalleAmortizacion >= 0
         AND DetalleCuota >= 0
         AND DetalleSaldoPendiente >= 0)
)
ON Registros;
GO


/* ============================================================================
   PROCEDIMIENTOS ALMACENADOS DE CLIENTE (CRUD)
   ============================================================================

   CRITERIO DE DISEÑO
   - Los procedimientos solo insertan, actualizan y consultan. NO validan reglas
     de negocio ni realizan cálculos.
   - Las reglas de las historias de usuario (documento duplicado, cliente con
     préstamos, morosidad, límites de monto, plazo y TEA, estado vigente, etc.)
     y los cálculos financieros (TEM, cuota, intereses, amortizaciones, saldos)
     se implementan en Java (clases Cliente, Prestamo y Cronograma del modelo).
   - La integridad básica la mantienen las restricciones de las tablas (PK, FK,
     UNIQUE y CHECK).
   - El INSERT genera el código siguiente de la tabla (CL01). Es generación de
     clave, no una regla de negocio.
   - Préstamo y cronograma no usan procedimientos: Java ejecuta INSERT y SELECT
     directos y genera sus códigos (PR0001, CR0001, DC000001).
   - Se invocan con el nombre completo (esquema.procedimiento).

   PROCEDIMIENTOS
     Cliente.sp_Cliente_Listar              HU-01 CA-01 (consulta)
     Cliente.sp_Cliente_Registrar           HU-01 CA-02
     Cliente.sp_Cliente_Actualizar          HU-01 CA-04
     Cliente.sp_Cliente_Desactivar          HU-01 RN-06 (solo cambia el estado)

   SUPUESTOS (las HU no los definen; se aplican en Java):
   - "Préstamos activos" (HU-01 RN-06): según el equipo, es un préstamo en estado
     'Vigente'. No se permite desactivar un cliente que tenga un préstamo vigente.
   - Morosidad (HU-02 RN-06): estados 'Moroso' e 'Incumplimiento'.
   - Todo préstamo nuevo se registra en estado 'Vigente'.
   - Cronograma (HU-03 RN-09): ninguna HU permite cambiar las condiciones de un
     préstamo, por lo que un préstamo solo tiene un cronograma (lo garantiza la
     restricción UNIQUE de Cronograma.Cronogramas). El cronograma se muestra al
     generarlo (HU-03 CA-03); ninguna HU pide consultarlo después.
   - Límite de códigos por el formato de la clave: 99 clientes, 9999 préstamos,
     9999 cronogramas y 999999 cuotas.
   ============================================================================ */


-- ============================================================================
-- HU-01: GESTIONAR CLIENTE
-- ============================================================================

-- Consulta clientes (activos e inactivos). Sin parámetros devuelve todos.
-- @ClientesCodigo: un cliente exacto. @Busqueda: texto contenido en el número
-- de documento, apellidos o nombres.
CREATE PROCEDURE Cliente.sp_Cliente_Listar
    @ClientesCodigo NCHAR(4)      = NULL,
    @Busqueda       NVARCHAR(100) = NULL
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        c.ClientesCodigo,
        c.TiposDocumentoCodigo,
        td.TiposDocumentoNombre,
        c.ClientesNumeroDocumento,
        c.ClientesApellidos,
        c.ClientesNombres,
        c.ClientesFechaNacimiento,
        c.EstadosCivilesCodigo,
        ec.EstadosCivilesNombre,
        c.ClientesDireccion,
        c.ClientesTelefono,
        c.ClientesCorreo,
        c.ClientesCondicionLaboral,
        c.ClientesNivelIngresos,
        c.ClientesNombreConyuge,
        c.ClientesReferenciasPersonales,
        c.ClientesObservaciones,
        c.ClientesEstado
    FROM Cliente.Clientes AS c
    INNER JOIN Cliente.TiposDocumento AS td
        ON td.TiposDocumentoCodigo = c.TiposDocumentoCodigo
    INNER JOIN Cliente.EstadosCiviles AS ec
        ON ec.EstadosCivilesCodigo = c.EstadosCivilesCodigo
    WHERE (@ClientesCodigo IS NULL OR c.ClientesCodigo = @ClientesCodigo)
      AND (@Busqueda IS NULL
           OR c.ClientesNumeroDocumento LIKE N'%' + @Busqueda + N'%'
           OR c.ClientesApellidos       LIKE N'%' + @Busqueda + N'%'
           OR c.ClientesNombres         LIKE N'%' + @Busqueda + N'%')
    ORDER BY c.ClientesCodigo;
END
GO


-- Inserta un cliente (queda Activo) y devuelve su código.
CREATE PROCEDURE Cliente.sp_Cliente_Registrar
    @TiposDocumentoCodigo        NCHAR(4),
    @NumeroDocumento             NVARCHAR(12),
    @Apellidos                   NVARCHAR(100),
    @Nombres                     NVARCHAR(100),
    @FechaNacimiento             DATE,
    @EstadosCivilesCodigo        NCHAR(4),
    @Direccion                   NVARCHAR(150),
    @Telefono                    NCHAR(9),
    @Correo                      NVARCHAR(100),
    @CondicionLaboral            NVARCHAR(50),
    @NivelIngresos               DECIMAL(18,2),
    @NombreConyuge               NVARCHAR(200) = NULL,
    @ReferenciasPersonales       NVARCHAR(255) = NULL,
    @Observaciones               NVARCHAR(255) = NULL,
    @ClientesCodigo              NCHAR(4) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @Siguiente INT;

    SELECT @Siguiente = ISNULL(MAX(CAST(SUBSTRING(ClientesCodigo, 3, 2) AS INT)), 0) + 1
    FROM Cliente.Clientes;

    SET @ClientesCodigo = N'CL' + RIGHT(N'00' + CAST(@Siguiente AS NVARCHAR(3)), 2);

    INSERT INTO Cliente.Clientes
        (ClientesCodigo, TiposDocumentoCodigo, ClientesNumeroDocumento,
         ClientesApellidos, ClientesNombres, ClientesFechaNacimiento,
         EstadosCivilesCodigo, ClientesDireccion, ClientesTelefono,
         ClientesCorreo, ClientesCondicionLaboral, ClientesNivelIngresos,
         ClientesNombreConyuge, ClientesReferenciasPersonales,
         ClientesObservaciones)
    VALUES
        (@ClientesCodigo, @TiposDocumentoCodigo, @NumeroDocumento,
         @Apellidos, @Nombres, @FechaNacimiento,
         @EstadosCivilesCodigo, @Direccion, @Telefono,
         @Correo, @CondicionLaboral, @NivelIngresos,
         @NombreConyuge, @ReferenciasPersonales,
         @Observaciones);
END
GO


-- Actualiza los datos de un cliente. No modifica su estado.
CREATE PROCEDURE Cliente.sp_Cliente_Actualizar
    @ClientesCodigo              NCHAR(4),
    @TiposDocumentoCodigo        NCHAR(4),
    @NumeroDocumento             NVARCHAR(12),
    @Apellidos                   NVARCHAR(100),
    @Nombres                     NVARCHAR(100),
    @FechaNacimiento             DATE,
    @EstadosCivilesCodigo        NCHAR(4),
    @Direccion                   NVARCHAR(150),
    @Telefono                    NCHAR(9),
    @Correo                      NVARCHAR(100),
    @CondicionLaboral            NVARCHAR(50),
    @NivelIngresos               DECIMAL(18,2),
    @NombreConyuge               NVARCHAR(200) = NULL,
    @ReferenciasPersonales       NVARCHAR(255) = NULL,
    @Observaciones               NVARCHAR(255) = NULL
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE Cliente.Clientes
    SET TiposDocumentoCodigo          = @TiposDocumentoCodigo,
        ClientesNumeroDocumento       = @NumeroDocumento,
        ClientesApellidos             = @Apellidos,
        ClientesNombres               = @Nombres,
        ClientesFechaNacimiento       = @FechaNacimiento,
        EstadosCivilesCodigo          = @EstadosCivilesCodigo,
        ClientesDireccion             = @Direccion,
        ClientesTelefono              = @Telefono,
        ClientesCorreo                = @Correo,
        ClientesCondicionLaboral      = @CondicionLaboral,
        ClientesNivelIngresos         = @NivelIngresos,
        ClientesNombreConyuge         = @NombreConyuge,
        ClientesReferenciasPersonales = @ReferenciasPersonales,
        ClientesObservaciones         = @Observaciones
    WHERE ClientesCodigo = @ClientesCodigo;
END
GO


-- Desactiva un cliente (no se elimina físicamente).
CREATE PROCEDURE Cliente.sp_Cliente_Desactivar
    @ClientesCodigo NCHAR(4)
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE Cliente.Clientes
    SET ClientesEstado = N'I'
    WHERE ClientesCodigo = @ClientesCodigo;
END
GO


/* ============================================================================
   DATOS INICIALES - SOFTWARE DE PRÉSTAMO FINANCIERO
   HU-01: GESTIONAR CLIENTE
   HU-02: REGISTRAR PRÉSTAMO FINANCIERO
   HU-03: GENERAR CRONOGRAMA DE PAGOS

   Datos utilizados únicamente para pruebas del sistema.
   Contraseña temporal de los usuarios: Prueba2026!
   Los hashes BCrypt se verifican desde Java (NetBeans).
   ============================================================================ */

-- ============================================================================
-- SEGURIDAD Y PERSONAL
-- ============================================================================

INSERT INTO Personal.Analistas
    (AnalistasCodigo, AnalistasApellidos, AnalistasNombres)
VALUES
    ('AN01', N'Acosta Plasencia', N'Naylin Atenas'),
    ('AN02', N'Chuquipoma Medina', N'Sthefany Darley'),
    ('AN03', N'Mantilla Sánchez', N'Elsa Lucía');
GO


INSERT INTO Seguridad.Usuarios
    (UsuariosCodigo, UsuariosNombre, UsuariosContrasenia, AnalistasCodigo)
VALUES
    ('U001', N'nacosta',
     '$2a$10$khbcoa1Z6hxKHpS.TepVle8dU6XFVhuLGPW2XQSQZ09VFQy6zIOM6',
     'AN01'),

    ('U002', N'schuquipoma',
     '$2a$10$C.49G8M5dkLlctpapM19iOkk.b.qG/OMfqcw2oz0Gr30peakbhFMm',
     'AN02'),

    ('U003', N'emantilla',
     '$2a$10$O4WFF4UhjimWyqam79ocVugJupGhyCNH3vob.ENvmH00cY0gm5xkO',
     'AN03');
GO


-- ============================================================================
-- HU-01: GESTIONAR CLIENTE
-- ============================================================================

-- HU-01 RN-01: DNI, pasaporte o carnet de extranjería
INSERT INTO Cliente.TiposDocumento
    (TiposDocumentoCodigo, TiposDocumentoNombre)
VALUES
    ('TD01', N'DNI'),
    ('TD02', N'Pasaporte'),
    ('TD03', N'Carnet de Extranjería');
GO


-- Valores supuestos: las HU no definen la lista de estados civiles
INSERT INTO Cliente.EstadosCiviles
    (EstadosCivilesCodigo, EstadosCivilesNombre)
VALUES
    ('EC01', N'Soltero(a)'),
    ('EC02', N'Casado(a)'),
    ('EC03', N'Divorciado(a)'),
    ('EC04', N'Viudo(a)'),
    ('EC05', N'Conviviente');
GO


INSERT INTO Cliente.Clientes
    (ClientesCodigo, TiposDocumentoCodigo, ClientesNumeroDocumento,
     ClientesApellidos, ClientesNombres, ClientesFechaNacimiento,
     EstadosCivilesCodigo, ClientesDireccion, ClientesTelefono,
     ClientesCorreo, ClientesCondicionLaboral, ClientesNivelIngresos,
     ClientesNombreConyuge, ClientesReferenciasPersonales)
VALUES
    ('CL01', 'TD01', N'45871236',
     N'Ramírez Torres', N'Juan Carlos', '1985-06-15',
     'EC02', N'Av. Perú 123, Trujillo', '987654321',
     N'jramirez@example.com', N'Dependiente', 2500.00,
     N'Rosa Elena Flores Díaz', N'Pedro Ramírez Sosa, Carla Torres Vega'),

    ('CL02', 'TD01', N'72943815',
     N'Gómez López', N'María Fernanda', '1987-08-20',
     'EC01', N'Calle Lima 456, Trujillo', '912345678',
     N'mgomez@example.com', N'Independiente', 1800.00,
     NULL, N'Luis Gómez Peña'),

    ('CL03', 'TD01', N'60391842',
     N'Vásquez Quispe', N'Luis Alberto', '1989-03-11',
     'EC05', N'Jr. Zela 789, Trujillo', '934561278',
     N'lvasquez@example.com', N'Dependiente', 3200.00,
     NULL, N'Mónica Quispe Ruiz'),

    ('CL04', 'TD01', N'73928501',
     N'Sánchez Reyes', N'Ana Lucía', '1990-07-23',
     'EC02', N'Urb. Las Palmas, Trujillo', '976341289',
     N'asanchez@example.com', N'Independiente', 2100.00,
     N'Miguel Ángel Ortiz Salas', N'Jorge Sánchez Vidal'),

    ('CL05', 'TD03', N'000234567',
     N'Torres Morales', N'José Miguel', '1992-04-30',
     'EC01', N'Av. América Sur 100, Trujillo', '965832147',
     N'jtorres@example.com', N'Desempleado', 1500.00,
     NULL, NULL);
GO


-- ============================================================================
-- HU-02: REGISTRAR PRÉSTAMO FINANCIERO
-- ============================================================================

-- HU-02 RN-05: condiciones de plazo y tasa por tipo de préstamo.
-- Valores simulados pendientes de confirmación del docente.
INSERT INTO Prestamo.TiposPrestamo
    (TiposPrestamoCodigo, TiposPrestamoNombre,
     TiposPrestamoPlazoMinimo, TiposPrestamoPlazoMaximo,
     TiposPrestamoTasaMinima, TiposPrestamoTasaMaxima)
VALUES
    ('TP01', N'Préstamo Personal',     3,  36, 0.1500, 0.4500),
    ('TP02', N'Préstamo Vehicular',   12,  60, 0.1000, 0.3000),
    ('TP03', N'Préstamo Hipotecario', 60, 240, 0.0800, 0.2000),
    ('TP04', N'Préstamo Negocio',      6,  48, 0.1200, 0.3500);
GO


-- Modalidad mensual según el cálculo definido en HU-03
INSERT INTO Prestamo.ModalidadesPago
    (ModalidadesPagoCodigo, ModalidadesPagoNombre)
VALUES
    ('MP01', N'Mensual');
GO


INSERT INTO Prestamo.Monedas
    (MonedasCodigo, MonedasNombre, MonedasSimbolo)
VALUES
    ('MO01', N'Sol peruano', N'S/');
GO


-- Estados mencionados en las HU:
--   Vigente (HU-03 RN-01), Moroso e Incumplimiento (HU-02 RN-06),
--   En evaluación (HU-01 RN-06)
INSERT INTO Prestamo.EstadosPrestamo
    (EstadosPrestamoCodigo, EstadosPrestamoNombre)
VALUES
    ('EP01', N'Vigente'),
    ('EP02', N'Moroso'),
    ('EP03', N'Incumplimiento'),
    ('EP04', N'En evaluación');
GO


-- Valores supuestos: la garantía es opcional y las HU no definen su lista
INSERT INTO Prestamo.Garantias
    (GarantiasCodigo, GarantiasDescripcion)
VALUES
    ('GA01', N'Garantía hipotecaria sobre inmueble'),
    ('GA02', N'Garantía vehicular'),
    ('GA03', N'Aval personal');
GO


-- HU-02 RN-04: límites de montos según política crediticia.
-- Valores simulados pendientes de confirmación del docente.
INSERT INTO Prestamo.PoliticaCredito
    (PoliticaCreditoCodigo, TiposPrestamoCodigo,
     PoliticaCreditoMontoMinimo, PoliticaCreditoMontoMaximo)
VALUES
    ('PC01', 'TP01',   500.00,  30000.00),
    ('PC02', 'TP02',  5000.00, 120000.00),
    ('PC03', 'TP03', 20000.00, 500000.00),
    ('PC04', 'TP04',  1000.00,  80000.00);
GO


-- ============================================================================
-- PRÉSTAMOS DE PRUEBA
-- ============================================================================
-- PR0001: Vigente. Permite generar cronograma (HU-03 RN-01).
--         Datos del ejemplo del anexo de HU-03. Impide desactivar a CL01.
-- PR0002: Moroso. Bloquea nuevos préstamos de CL02 (HU-02 RN-06).
-- PR0003: Incumplimiento. Bloquea nuevos préstamos de CL03 (HU-02 RN-06).
-- PR0004: En evaluación. Impide desactivar a CL04 (HU-01 RN-06).
-- CL05 no tiene préstamos: permite probar desactivación y registro de préstamo.
-- ============================================================================


INSERT INTO Prestamo.Prestamos
    (PrestamosCodigo, ClientesCodigo, TiposPrestamoCodigo,
     PrestamosMontoSolicitado, PrestamosPlazoCuotas, PrestamosTEA,
     ModalidadesPagoCodigo, PrestamosFechaDesembolso, MonedasCodigo,
     GarantiasCodigo, PrestamosObservaciones, PrestamosDescripcionDestino,
     EstadosPrestamoCodigo)
VALUES
    ('PR0001', 'CL01', 'TP01', 10000.00, 5, 0.2000,
     'MP01', '2026-04-01', 'MO01', NULL, NULL, NULL, 'EP01'),

    ('PR0002', 'CL02', 'TP01', 5000.00, 12, 0.3000,
     'MP01', '2025-10-01', 'MO01', NULL, NULL, NULL, 'EP02'),

    ('PR0003', 'CL03', 'TP04', 8000.00, 6, 0.2500,
     'MP01', '2025-01-01', 'MO01', NULL, NULL, NULL, 'EP03'),

    ('PR0004', 'CL04', 'TP01', 6000.00, 12, 0.2200,
     'MP01', '2026-10-01', 'MO01', NULL, NULL, NULL, 'EP04');
GO


-- ============================================================================
-- VERIFICACIÓN DE TABLAS
-- ============================================================================

SELECT
    s.name AS Esquema,
    t.name AS Tabla
FROM sys.tables AS t
INNER JOIN sys.schemas AS s
    ON s.schema_id = t.schema_id
WHERE s.name IN
    (N'Seguridad', N'Personal', N'Cliente', N'Prestamo', N'Cronograma')
ORDER BY s.name, t.name;
GO


-- ============================================================================
-- VERIFICACIÓN DE PRÉSTAMOS REGISTRADOS
-- ============================================================================

SELECT
    p.PrestamosCodigo,
    p.ClientesCodigo,
    e.EstadosPrestamoNombre,
    p.PrestamosMontoSolicitado,
    p.PrestamosPlazoCuotas,
    p.PrestamosTEA
FROM Prestamo.Prestamos AS p
INNER JOIN Prestamo.EstadosPrestamo AS e
    ON e.EstadosPrestamoCodigo = p.EstadosPrestamoCodigo
ORDER BY p.PrestamosCodigo;
GO

-- Los cronogramas se generan y guardan desde Java (NetBeans).
