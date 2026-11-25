import java.util.*;

/** Flujo fijo y validadores proporcionados. No cambien códigos, valores ni orden. */
public final class Flujo {
    public enum Metodo {
        BUSCAR("Buscar persona","buscarPersona"), CORREGIR("Corregir país","corregirPais"),
        REGISTRAR("Registrar inscripción","registrarInscripcion"), CANCELAR("Cancelar inscripción","cancelarInscripcion"),
        CONTAR("Contar personas por país","contarPersonasPorPais");
        public final String etiqueta,funcion;
        Metodo(String etiqueta,String funcion) { this.etiqueta=etiqueta;this.funcion=funcion; }
        @Override public String toString() { return etiqueta; }
    }
    private static <T extends Entidad> Map<String,String> firmas(Collection<T> c) {
        Map<String,String> r=new TreeMap<>();
        for(T t:c) {
            if(t==null || r.put(t.id,t.firma())!=null)throw new IllegalStateException("Entidad nula o código duplicado.");
        }
        return r;
    }
    private static <T extends Entidad> void validarAlmacen(Estado.Almacen<T> a,List<T> esperado) {
        if(!firmas(a.valores()).equals(firmas(esperado)))throw new IllegalStateException("El contenido final de una colección es incorrecto.");
        if(a.lista!=null) {
            List<String> x=new ArrayList<>(),y=new ArrayList<>();
            for(T t:a.lista)x.add(t.firma());for(T t:esperado)y.add(t.firma());
            if(!x.equals(y))throw new IllegalStateException("La operación alteró el orden de la lista.");
        }
        if(a.mapa!=null)for(Map.Entry<String,T> x:a.mapa.entrySet())
            if(!x.getKey().equals(x.getValue().id))throw new IllegalStateException("Clave distinta al código de la entidad.");
    }
    public static final class Plan {
        final Metodo metodo;
        final List<Pais> paises;
        final List<Persona> personas;
        final List<Inscripcion> inscripciones;
        final Object esperado;
        Plan(Datos datos,Metodo metodo) {
            this.metodo=metodo;paises=new ArrayList<>(datos.paises);personas=new ArrayList<>(datos.personas);
            inscripciones=new ArrayList<>(datos.inscripciones);
            if(metodo.ordinal()>=Metodo.CORREGIR.ordinal())for(int i=0;i<personas.size();i++) {
                Persona p=personas.get(i);if(p.id.equals("P00002"))personas.set(i,new Persona(p.id,p.nombre,"CRI"));
            }
            if(metodo.ordinal()>=Metodo.REGISTRAR.ordinal())
                inscripciones.add(new Inscripcion("I06000-N1","P06000","Taller de Java"));
            if(metodo.ordinal()>=Metodo.CANCELAR.ordinal())
                inscripciones.removeIf(i->i.id.equals("I00002-1") || i.id.equals("I00001-1"));
            switch(metodo) {
                case BUSCAR: esperado=Arrays.asList("P00001|Participante 00001|ECU","P06000|Participante 06000|PAN",null,null);break;
                case CORREGIR: esperado=Arrays.asList(true,false,false,false);break;
                case REGISTRAR: esperado=Arrays.asList(true,false,false,false,false);break;
                case CANCELAR: esperado=Arrays.asList(true,true,false,false);break;
                default:
                    Map<String,Integer> conteo=new TreeMap<>();for(Pais p:paises)conteo.put(p.id,500);esperado=conteo;
            }
        }
        Object ejecutar(Estado e) {
            switch(metodo) {
                case BUSCAR: return Arrays.asList(Operaciones.buscarPersona(e,"P00001"),Operaciones.buscarPersona(e,"P06000"),
                    Operaciones.buscarPersona(e,"P99999"),Operaciones.buscarPersona(e,null));
                case CORREGIR: return Arrays.asList(Operaciones.corregirPais(e,"P00002","CRI"),
                    Operaciones.corregirPais(e,"P99999","ECU"),Operaciones.corregirPais(e,"P00003","ZZZ"),
                    Operaciones.corregirPais(e,"P00002","CRI"));
                case REGISTRAR: return Arrays.asList(Operaciones.registrarInscripcion(e,"I06000-N1","P06000","Taller de Java"),
                    Operaciones.registrarInscripcion(e,"I06000-N1","P00003","Conferencias"),
                    Operaciones.registrarInscripcion(e,"I06000-N2","P06000","Taller de Java"),
                    Operaciones.registrarInscripcion(e,"I99999-N1","P99999","Conferencias"),
                    Operaciones.registrarInscripcion(e,"I06000-N3","P06000","Actividad inexistente"));
                case CANCELAR: return Arrays.asList(Operaciones.cancelarInscripcion(e,"I00002-1"),
                    Operaciones.cancelarInscripcion(e,"I00001-1"),Operaciones.cancelarInscripcion(e,"I99999-1"),
                    Operaciones.cancelarInscripcion(e,"I00002-1"));
                default: return Operaciones.contarPersonasPorPais(e);
            }
        }
        void validar(Estado e,Object resultado) {
            Object salida=resultado;
            if(metodo==Metodo.BUSCAR) {
                if(!(resultado instanceof List))throw new IllegalStateException("Resultado de búsqueda inválido.");
                List<String> nombres=new ArrayList<>();
                for(Object p:(List<?>)resultado)nombres.add(p==null?null:((Persona)p).firma());salida=nombres;
            }
            if(!esperado.equals(salida))throw new IllegalStateException("Resultados distintos a los del README en "+metodo.funcion+".");
            validarAlmacen(e.paises,paises);validarAlmacen(e.personas,personas);validarAlmacen(e.inscripciones,inscripciones);
        }
    }
    public static final class Resultado {
        public final Object salida;
        public final boolean repetido;
        public final boolean tiempoRegistrado;
        Resultado(Object salida,boolean repetido,boolean registrado) { this.salida=salida;this.repetido=repetido;tiempoRegistrado=registrado; }
    }
    private static Map<Estado.Tipo,EnumMap<Metodo,Long>> copiarTiempos() {
        Map<Estado.Tipo,EnumMap<Metodo,Long>> r=new EnumMap<>(Estado.Tipo.class);
        for(Estado.Tipo tipo:Estado.Tipo.values())r.put(tipo,new EnumMap<>(Mediciones.TIEMPOS.get(tipo)));return r;
    }
    public static Resultado probar(Estado e,Datos datos,Metodo metodo) {
        if(e.probados.contains(metodo))return new Resultado(null,true,Mediciones.TIEMPOS.get(e.tipo).containsKey(metodo));
        if(metodo.ordinal()!=e.siguientePaso)throw new IllegalStateException("Sigan el orden 1 a 5. Siguiente: "+Metodo.values()[e.siguientePaso]);
        Plan plan=new Plan(datos,metodo);Estado.Instantanea foto=e.foto(); // Fuera del intervalo.
        Map<Estado.Tipo,EnumMap<Metodo,Long>> antes=copiarTiempos();
        try {
            Mediciones.Medicion m=Mediciones.medir(()->plan.ejecutar(e));
            if(m==null || (m.nanosegundos!=null && m.nanosegundos<0))throw new IllegalStateException("Medición inválida.");
            plan.validar(e,m.resultado); // Fuera del intervalo.
            if(m.nanosegundos!=null)Mediciones.registrar(e.tipo,metodo,m.nanosegundos);
            Map<Estado.Tipo,EnumMap<Metodo,Long>> despues=copiarTiempos();
            Long guardado=despues.get(e.tipo).get(metodo);
            if(guardado!=null && !guardado.equals(m.nanosegundos))throw new IllegalStateException("El tiempo registrado no coincide con la medición.");
            if(guardado!=null)despues.get(e.tipo).remove(metodo);
            if(!despues.equals(antes))throw new IllegalStateException("El registro modificó otros métodos o estructuras.");
            e.probados.add(metodo);e.siguientePaso++;
            return new Resultado(m.resultado,false,guardado!=null);
        } catch(RuntimeException error) {
            e.restaurar(foto);
            for(Estado.Tipo tipo:Estado.Tipo.values()) {
                Mediciones.TIEMPOS.get(tipo).clear();Mediciones.TIEMPOS.get(tipo).putAll(antes.get(tipo));
            }
            throw error;
        }
    }
}
