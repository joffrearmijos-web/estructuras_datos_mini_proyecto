import java.util.*;

/** El mismo tipo se aplica a países, personas e inscripciones. */
public final class Estado {
    public enum Tipo { ArrayList,LinkedList,HashSet,TreeSet,HashMap,TreeMap }
    public static final class Almacen<T extends Entidad> {
        public final List<T> lista;
        public final Set<T> conjunto;
        public final Map<String,T> mapa;
        public Almacen(Tipo tipo) {
            // Instanciación proporcionada: no vuelvan a crear estas colecciones en los métodos.
            if(tipo==Tipo.ArrayList) { lista=new ArrayList<>();conjunto=null;mapa=null; }
            else if(tipo==Tipo.LinkedList) { lista=new LinkedList<>();conjunto=null;mapa=null; }
            else if(tipo==Tipo.HashSet) { lista=null;conjunto=new HashSet<>();mapa=null; }
            else if(tipo==Tipo.TreeSet) { lista=null;conjunto=new TreeSet<>();mapa=null; }
            else if(tipo==Tipo.HashMap) { lista=null;conjunto=null;mapa=new HashMap<>(); }
            else { lista=null;conjunto=null;mapa=new TreeMap<>(); }
        }
        public Collection<T> valores() { return lista!=null?lista:conjunto!=null?conjunto:mapa.values(); }
        public Object instancia() { return lista!=null?lista:conjunto!=null?conjunto:mapa; }
        /** Infraestructura de carga/restauración. No sustituye los métodos que deben implementar. */
        void cargar(Collection<T> valores) {
            if(lista!=null) { lista.clear();lista.addAll(valores); }
            else if(conjunto!=null) { conjunto.clear();conjunto.addAll(valores); }
            else { mapa.clear();for(T valor:valores)mapa.put(valor.id,valor); }
        }
    }
    public final Tipo tipo;
    public final Almacen<Pais> paises;
    public final Almacen<Persona> personas;
    public final Almacen<Inscripcion> inscripciones;
    public int siguientePaso=0;
    public final EnumSet<Flujo.Metodo> probados=EnumSet.noneOf(Flujo.Metodo.class);
    public Estado(Tipo tipo,Datos datos) {
        this.tipo=tipo;paises=new Almacen<>(tipo);personas=new Almacen<>(tipo);inscripciones=new Almacen<>(tipo);
        reiniciar(datos);
    }
    public void reiniciar(Datos datos) {
        paises.cargar(datos.paises);personas.cargar(datos.personas);inscripciones.cargar(datos.inscripciones);
        siguientePaso=0;probados.clear();
    }
    public Instantanea foto() { return new Instantanea(this); }
    public void restaurar(Instantanea foto) {
        paises.cargar(foto.paises);personas.cargar(foto.personas);inscripciones.cargar(foto.inscripciones);
    }
    public static final class Instantanea {
        final List<Pais> paises;final List<Persona> personas;final List<Inscripcion> inscripciones;
        Instantanea(Estado e) {
            paises=new ArrayList<>(e.paises.valores());personas=new ArrayList<>(e.personas.valores());
            inscripciones=new ArrayList<>(e.inscripciones.valores());
        }
    }
}
