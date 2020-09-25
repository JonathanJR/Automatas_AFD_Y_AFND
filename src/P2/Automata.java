package P2;

import java.io.*;
import java.util.*;

/**
 * Clase que implementa los métodos necesarios para trabajar con el autómata de
 * entrada
 *
 * @author Jonathan Jimenez Reina
 * @author Victor Alonso Nuñez
 */
public class Automata implements Cloneable, Proceso {

    //En esta tabla guardaremos las transiciones entre los estados
    String[][] transiciones;

    //Con este diccionario sabremos en que columna de la tabla va cada estado
    HashMap<String, Integer> estados;

    //Con este diccionario sabremos en que columna de la tabla va cada simbolo
    HashMap<String, Integer> simbolos;

    //En estos atributos guardamos los estados incial y actual, y tambien
    //un String en el que se guarda si la solucion es o no aceptada
    String estadoActual;
    String estadoInicial;
    String esAceptada;

    //Conjuntos para guardar los estados finales y los estados solución
    ArrayList<String> estadosFinales;
    ArrayList<String> estadosSolucion;

    //Lista de listas solucion para posibles soluciones a una misma cadena
    ArrayList<ArrayList<String>> soluciones;

    //Boolean para marcar a true cuando la solucion sea encontrada
    boolean solucionEncontrada;

    /**
     * Constructor de la clase - Inicializacion de variables
     */
    public Automata() {
        this.estados = new HashMap<>();
        this.simbolos = new HashMap<>();
        this.estadoActual = null;
        this.estadoInicial = null;
        this.esAceptada = "";
        this.estadosFinales = new ArrayList<>();
        this.estadosSolucion = new ArrayList<>();
        this.solucionEncontrada = false;
        this.soluciones = new ArrayList<>();
    }

    /**
     * Método para ver en que estado nos encontramos
     *
     * @return String con el estado actual
     * @throws Exception si no hay cargado ningún autómata
     */
    public String getEstado() throws Exception {
        if (getEstadoInicial() == null) {
            throw new Exception("No se ha cargado ningún autómata");
        }
        return getEstadoActual();
    }

    /**
     * Método que nos dice si un estado es o no final
     *
     * @param estado Estado que queremos comprobar
     * @return True si es final, False si no lo es
     */
    @Override
    public boolean esFinal(String estado) {
        return getEstadosFinales().contains(estado);
    }

    /**
     * Método que actualiza estadoActual a siguiente estado
     *
     * @param entrada Símbolo de la entrada
     * @throws Exception Si no hay cargado ningún autómata o si la entrada no
     * contiene el símbolo
     */
    public void getSiguienteEstado(char entrada) throws Exception {
        String e = Character.toString(entrada);
        if (getEstadoInicial() == null) {
            throw new Exception("No se ha cargado ningún autómata");
        }
        if (!getSimbolos().containsKey(e)) {
            throw new Exception("Entrada no válida");
        }
        String actual = getTransiciones()[getEstados().get(getEstadoActual())][getSimbolos().get(e)];
        setEstadoActual(actual);
    }

    /**
     * Método para reiniciar el autómata a su estado inicial y poner variables a
     * punto para una nueva ejecución
     */
    public void reiniciarAutomata() {
        setEstadoActual(getEstadoInicial());
        setEstadosSolucion(new ArrayList<>());
        setSolucionEncontrada(false);
    }

    /**
     * Método que nos dice si el automata ha sido o no reconocido
     *
     * @return String con "ACEPTAR" si lo es o "RECHAZAR" si no
     */
    @Override
    public boolean reconocer() {
        String aceptar = "ACEPTAR";
        String rechazar = "RECHAZAR";
        if (getTipoAutomata().equals("indeterminista")) {
            if (isSolucionEncontrada()) {
                setEsAceptada(aceptar);
            } else {
                setEsAceptada(rechazar);
            }
        } else {
            if (getEstadosFinales().contains(getEstadoActual())) {
                setEsAceptada(aceptar);
            } else {
                setEsAceptada(rechazar);
            }
        }
        return aceptar.equals(getEsAceptada());
    }

    /**
     * Método para cargar el autómata desde un fichero
     *
     * @param fichero Fichero con el autómata
     * @return Lista de arrays de strings con la tabla del autómata
     * @throws java.lang.Exception Si no puede cargar el automata
     */
    public ArrayList<String[]> cargarAutomata(File fichero) throws Exception {
        //Usaremos esta lista para almacenar los estados
        ArrayList<String> listaEstados = new ArrayList<>();
        //Usaremos esta lista para devolver para rellenar la tabla
        ArrayList<String[]> tablaEstados = new ArrayList<>();
        //Vaciamos los diccionario y la lista de estados finales
        getEstados().clear();
        getSimbolos().clear();
        getEstadosFinales().clear();

        //Nos preparamos para leer
        FileReader r = new FileReader(fichero);
        BufferedReader b = new BufferedReader(r);
        String linea;
        String[] parts;
        //Leemos los estados posibles e inicializamos el diccionario
        linea = b.readLine().trim();
        parts = linea.split(" ");
        if (!"ESTADOS:".equals(parts[0])) {
            throw new Exception();
        }
        for (int i = 1; i < parts.length; i++) {
            getEstados().put(parts[i].trim(), i);
            listaEstados.add(parts[i].trim());
        }
        //Introducimos el estado muerto
        getEstados().put("M", 0);
        //Leemos el estado inicial
        linea = b.readLine().trim();
        parts = linea.split(" ");
        if (!"INICIAL:".equals(parts[0])) {
            throw new Exception();
        }
        setEstadoInicial(parts[1].trim());
        setEstadoActual(getEstadoInicial());
        //Leemos los estados finales y los introducimos en la lista
        linea = b.readLine().trim();
        parts = linea.split(" ");
        if (!"FINALES:".equals(parts[0].trim())) {
            throw new Exception();
        }
        for (int i = 1; i < parts.length; i++) {
            getEstadosFinales().add(parts[i].trim());
        }

        //Leemos las transiciones y vamos actualizando el diccionario de símbolos
        linea = b.readLine().trim();
        if (!"TRANSICIONES:".equals(linea)) {
            throw new Exception();
        }
        linea = b.readLine().trim();
        int nsimb = 0;
        do {
            String[] aux = new String[3];
            parts = linea.split(" ", 2);
            aux[0] = parts[0];
            parts = parts[1].trim().split(" ", 2);
            aux[2] = parts[1].trim();
            aux[1] = parts[0].trim().replaceAll("'", "");
            tablaEstados.add(aux);
            if (!getSimbolos().containsKey(aux[1])) {
                getSimbolos().put(aux[1], nsimb);
                nsimb++;
            }
            linea = b.readLine().trim();
        } while (!"FIN".equals(linea.trim()));
        //Inicializamos la tabla de transiciones al estado muerto
        setTransiciones(new String[getEstados().size()][getSimbolos().size()]);
        for (int i = 0; i < getEstados().size(); i++) {
            for (int j = 0; j < getSimbolos().size(); j++) {
                getTransiciones()[i][j] = "M";
            }
        }
        //Actualizamos las transiciones
        for (int i = 0; i < tablaEstados.size(); i++) {
            String[] aux = tablaEstados.get(i);
            String s = getTransiciones()[getEstados().get(aux[0])][getSimbolos().get(aux[1])];
            getTransiciones()[getEstados().get(aux[0])][getSimbolos().get(aux[1])] = aux[2];
            if (!"M".equals(s)) {
                getTransiciones()[getEstados().get(aux[0])][getSimbolos().get(aux[1])] = s + "," + aux[2];
            }
        }
        //Rellenamos y devolvemos la lista de la tabla
        tablaEstados.clear();
        String[] n = new String[getSimbolos().size()];
        //Bucle for transformando el hashmap a un MapSet a traves de entrySet y 
        //ahi si puedo consultar tanto la clave como el valor
        for (int i = 0; i < n.length; i++) {
            for (Map.Entry simb : getSimbolos().entrySet()) {
                if ((int) simb.getValue() == i) {
                    n[i] = (String) simb.getKey();
                }
            }
        }
        tablaEstados.add(n);
        for (int i = 0; i < listaEstados.size(); i++) {
            n = new String[getSimbolos().size() + 1];
            n[0] = listaEstados.get(i);
            for (int j = 0; j < getSimbolos().size(); j++) {
                n[j + 1] = getTransiciones()[getEstados().get(n[0])][j];
            }
            if (getEstadosFinales().contains(n[0])) {
                n[0] = n[0] + "*";
            }
            if (n[0].equals(getEstadoInicial())) {
                n[0] = "-" + n[0];
            }
            tablaEstados.add(n);
        }

        return tablaEstados;
    }

    /**
     * Metodo que cuando se lee un autómata me dice si es determinista o no
     * determinista mirando si entre los símbolos hay un "lambda" o si en algun
     * estado se puede ir a varios estados desde un mismo símbolo
     *
     * @return Devuelve el tipo, determinista o indeterminista
     */
    public String getTipoAutomata() {
        String s = "";
        for (int i = 0; i < getTransiciones().length; i++) {
            for (int j = 0; j < getTransiciones()[0].length; j++) {
                s = (getTransiciones()[i][j]);
                if (s.contains(",")) {
                    return "indeterminista";
                } else {
                    for (Map.Entry<String, Integer> entry : getSimbolos().entrySet()) {
                        if ("@".equals(entry.getKey())) {
                            return "indeterminista";
                        }
                    }
                }
            }
        }
        return "determinista";

    }

    /**
     * Método que nos devuelve una lista de transiciones desde el estado pasado
     * como parametro
     *
     * @param estado Estado desde el cual hay que encontrar las transiciones
     * @return Lista de transiciones de tipo Transicion
     */
    public ArrayList<Transicion> transicionesDe(String estado) {
        Integer fila = getEstados().get(estado);
        ArrayList<Transicion> trans = new ArrayList<>();
        String s = "";
        for (int y = 0; y < getTransiciones()[fila].length; y++) {
            String est = getTransiciones()[fila][y];
            if (!est.equals("M")) {
                for (Map.Entry simb : getSimbolos().entrySet()) {
                    if ((int) simb.getValue() == y) {
                        s = (String) simb.getKey();
                        if (est.length() > 1) {
                            char[] chars = est.toCharArray();
                            for (int i = 0; i < chars.length; i++) {
                                if (chars[i] != ',') {
                                    trans.add(new Transicion(s, String.valueOf(chars[i])));
                                }
                            }
                        } else {
                            trans.add(new Transicion(s, est));
                        }
                    }
                }
            }
        }
        return trans;
    }

    /**
     * Algoritmo recursivo que dada una entrada, un estado y un conjunto vacio
     * de String, va recorriendo la cadena comprobando si es leida por el
     * autómata para finalmente asignar el camino solucion a una variable global
     * "estadosSolucion".
     *
     * @param entrada Cadena que introduce el usuario para evaluar
     * @param estado Estado inicial
     * @param aux Lista de String vacía que se utilizará para añadir estados
     * solucion
     */
    public void AFND(String entrada, String estado, ArrayList<String> aux) {
        ArrayList<Transicion> trans = transicionesDe(estado);
        //Añado el estado en el que me encuentro al array que sera la solución
        aux.add(estado);

        //Solo entro aqui para asignar la solución cuando no queden símbolos por
        //consumir y si el estado en el que estoy es final
        if (entrada.isEmpty() && getEstadosFinales().contains(estado)) {
            setEstadosSolucion(aux); //Asigno la solucion encontrada a la variable global
            getSoluciones().add(aux);
            setSolucionEncontrada(true);

        } else {
            if (!trans.isEmpty()) { //Si hay transiciones desde el estado en el que estoy
                for (Transicion t : trans) { //Recorro las transiciones -> Ramas del algoritmo recursivo
                    //Entro en este if si hay transiciones desde este estado o es un estado final
                    //Si no no tiene sentido entrar ya que voy a un estado desde el que no puedo salir y no es final
                    if (!transicionesDe(t.getEstado()).isEmpty() || getEstadosFinales().contains(t.getEstado())) {
                        if (t.getSimbolo().equals("@")) { //Si el símbolo es lambda llamo a la funcion recursivamente sin consumir símbolo
                            //LLamo a la funcion recursivamente pasandole la entrada, el estado y una copia del array que será solución
                            AFND(entrada, t.getEstado(), (ArrayList<String>) aux.clone());
                        } else {
                            if (!entrada.isEmpty()) { //Compruebo que la entrada no esté vacía
                                if (t.getSimbolo().equals(String.valueOf(entrada.charAt(0)))) { //Si el simbolo de este estado es igual al primer simbolo de la cadena
                                    //LLamo a la funcion recursiva pasandole la entrada menos el simbolo consumido, el estado y una copia del array que será solución
                                    AFND(entrada.substring(1, entrada.length()), t.getEstado(), (ArrayList<String>) aux.clone());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Método clone para la copia de un objeto tipo Autómata
     *
     * @return Objeto clonado
     * @throws CloneNotSupportedException Si no puede clonarlo
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        Automata a = new Automata();
        a.setEstadoActual(this.getEstadoActual());
        a.setEstadoInicial(this.getEstadoActual());
        a.setEstados((HashMap<String, Integer>) this.getEstados().clone());
        a.setEstadosFinales((ArrayList<String>) this.getEstadosFinales().clone());
        a.setEstadosSolucion((ArrayList<String>) this.getEstadosSolucion().clone());
        a.setSimbolos((HashMap<String, Integer>) this.getSimbolos().clone());
        a.setEsAceptada(this.getEsAceptada());
        a.setSolucionEncontrada(this.isSolucionEncontrada());

        a.setTransiciones(new String[this.getTransiciones().length][]);

        for (int r = 0; r < this.getTransiciones().length; r++) {
            a.getTransiciones()[r] = this.getTransiciones()[r].clone();
        }
        //a.transiciones = this.transiciones.clone();
        return a;
    }

    //GETTERS Y SETTERS
    public String[][] getTransiciones() {
        return transiciones;
    }

    public void setTransiciones(String[][] transiciones) {
        this.transiciones = transiciones;
    }

    public HashMap<String, Integer> getEstados() {
        return estados;
    }

    public void setEstados(HashMap<String, Integer> estados) {
        this.estados = estados;
    }

    public HashMap<String, Integer> getSimbolos() {
        return simbolos;
    }

    public void setSimbolos(HashMap<String, Integer> simbolos) {
        this.simbolos = simbolos;
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    public String getEstadoInicial() {
        return estadoInicial;
    }

    public void setEstadoInicial(String estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public String getEsAceptada() {
        return esAceptada;
    }

    public void setEsAceptada(String esAceptada) {
        this.esAceptada = esAceptada;
    }

    public ArrayList<String> getEstadosFinales() {
        return estadosFinales;
    }

    public void setEstadosFinales(ArrayList<String> estadosFinales) {
        this.estadosFinales = estadosFinales;
    }

    public ArrayList<String> getEstadosSolucion() {
        return estadosSolucion;
    }

    public void setEstadosSolucion(ArrayList<String> estadosSolucion) {
        this.estadosSolucion = estadosSolucion;
    }

    public boolean isSolucionEncontrada() {
        return solucionEncontrada;
    }

    public void setSolucionEncontrada(boolean solucionEncontrada) {
        this.solucionEncontrada = solucionEncontrada;
    }

    public ArrayList<ArrayList<String>> getSoluciones() {
        return soluciones;
    }

    public void setSoluciones(ArrayList<ArrayList<String>> soluciones) {
        this.soluciones = soluciones;
    }

}
