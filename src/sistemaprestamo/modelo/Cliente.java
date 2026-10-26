package sistemaprestamo.modelo;

/**
 * Entidad Cliente: representa un registro de la tabla clientes.
 * Solo contiene atributos, constructores, getters y setters.
 */
public class Cliente {

    private int idCliente;
    private String nombres;
    private String apellidos;
    private String dni;
    private String telefono;
    private String correo;
    private String direccion;

    public Cliente() {
    }

    public Cliente(int idCliente, String nombres, String apellidos, String dni,
                   String telefono, String correo, String direccion) {
        this.idCliente = idCliente;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dni = dni;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
    }

    public Cliente(String nombres, String apellidos, String dni,
                   String telefono, String correo, String direccion) {
        this(0, nombres, apellidos, dni, telefono, correo, direccion);
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return idCliente + " - " + nombres + " " + apellidos + " (DNI: " + dni + ")";
    }
}
