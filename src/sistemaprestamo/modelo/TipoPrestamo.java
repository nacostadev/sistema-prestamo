package sistemaprestamo.modelo;

import java.math.BigDecimal;


public class TipoPrestamo {

    private final String codigo;
    private final String nombre;
    private final int plazoMinimo;
    private final int plazoMaximo;
    private final BigDecimal tasaMinima;
    private final BigDecimal tasaMaxima;
    private final BigDecimal montoMinimo;
    private final BigDecimal montoMaximo;

    public TipoPrestamo(String codigo, String nombre, int plazoMinimo, int plazoMaximo,
                        BigDecimal tasaMinima, BigDecimal tasaMaxima,
                        BigDecimal montoMinimo, BigDecimal montoMaximo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.plazoMinimo = plazoMinimo;
        this.plazoMaximo = plazoMaximo;
        this.tasaMinima = tasaMinima;
        this.tasaMaxima = tasaMaxima;
        this.montoMinimo = montoMinimo;
        this.montoMaximo = montoMaximo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPlazoMinimo() {
        return plazoMinimo;
    }

    public int getPlazoMaximo() {
        return plazoMaximo;
    }

    public BigDecimal getTasaMinima() {
        return tasaMinima;
    }

    public BigDecimal getTasaMaxima() {
        return tasaMaxima;
    }

    public BigDecimal getMontoMinimo() {
        return montoMinimo;
    }

    public BigDecimal getMontoMaximo() {
        return montoMaximo;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
