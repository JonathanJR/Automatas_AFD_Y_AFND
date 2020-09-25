package P2;

/**
 * Interfaz dada por el profesor, la cual debe implementar nuestra clase
 * Automata.java
 *
 * @author Jonathan Jimenez Reina
 * @author Victor Alonso Nuñez
 */
public interface Proceso {

    public abstract boolean esFinal(String estado);//True si estado es un estado final   

    public abstract boolean reconocer(); //True si la cadena es reconocida 

    @Override
    public abstract String toString(); //Muestra las transiciones y estados finales 
}
