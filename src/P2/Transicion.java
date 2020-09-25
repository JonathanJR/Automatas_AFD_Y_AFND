package P2;

/**
 * Clase para crear un objeto de tipo Transicion (Simbolo, Estado)
 *
 * @author Jonathan Jimenez Reina
 * @author Victor Alonso Nuñez
 */
public class Transicion {

    private String simbolo;
    private String estado;

    /**
     * Constructor con parámetros
     *
     * @param simbolo Simbolo desde el que se alcanza este estado
     * @param estado Estado alcanzable
     */
    public Transicion(String simbolo, String estado) {
        this.simbolo = simbolo;
        this.estado = estado;
    }

    public Transicion() {
    }

    //GETTERS Y SETTERS
    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Transicion{" + "simbolo=" + simbolo + ", estado=" + estado + '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Transicion t = new Transicion();
        t.setEstado(this.getEstado());
        t.setSimbolo(this.getSimbolo());
        return t;
    }
}
