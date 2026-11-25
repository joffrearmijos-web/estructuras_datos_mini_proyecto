import java.util.*;

public final class Mediciones {
    /** Almacenamiento estático común: un tiempo por método y estructura durante esta sesión. */
    public static final Map<Estado.Tipo,EnumMap<Flujo.Metodo,Long>> TIEMPOS=new EnumMap<>(Estado.Tipo.class);
    static { for(Estado.Tipo t:Estado.Tipo.values())TIEMPOS.put(t,new EnumMap<>(Flujo.Metodo.class)); }
    public interface Trabajo { Object ejecutar(); }
    public static final class Medicion {
        public final Object resultado;
        public final Long nanosegundos;
        public Medicion(Object resultado,Long nanosegundos) { this.resultado=resultado;this.nanosegundos=nanosegundos; }
    }
    public static Medicion medir(Trabajo trabajo) {
        // TODO TIEMPO: inicio con System.nanoTime(), ejecutar trabajo exactamente una vez,
        // fin con System.nanoTime(); devolver el resultado y fin-inicio en nanosegundos.
        // Esta versión permite usar ArrayList, pero no simula una medición.
        return new Medicion(trabajo.ejecutar(),null);
    }
    public static void registrar(Estado.Tipo tipo,Flujo.Metodo metodo,long nanosegundos) {
        // TODO REGISTRO: rechazar tiempos negativos y guardar en TIEMPOS.get(tipo)
        // solo si ese método todavía no tiene tiempo. No sumar ni reemplazar el primero.
    }
    public static void reiniciar() { for(Map<Flujo.Metodo,Long> tiempos:TIEMPOS.values())tiempos.clear(); }
}
