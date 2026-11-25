import java.util.Objects;

/** Identidades inmutables: hashCode, equals y orden se basan en el código. */
abstract class Entidad implements Comparable<Entidad> {
    public final String id;
    protected Entidad(String id) { this.id=Objects.requireNonNull(id); }
    public abstract String firma();
    @Override public final int compareTo(Entidad otra) { return id.compareTo(otra.id); }
    @Override public final int hashCode() { return Objects.hash(getClass(),id); }
    @Override public final boolean equals(Object otra) {
        return otra!=null && getClass()==otra.getClass() && id.equals(((Entidad)otra).id);
    }
}
final class Pais extends Entidad {
    public final String nombre;
    public Pais(String codigo,String nombre) { super(codigo);this.nombre=nombre; }
    public String firma() { return id+"|"+nombre; }
    @Override public String toString() { return firma(); }
}
final class Persona extends Entidad {
    public final String nombre,codigoPais;
    public Persona(String id,String nombre,String codigoPais) { super(id);this.nombre=nombre;this.codigoPais=codigoPais; }
    public String firma() { return id+"|"+nombre+"|"+codigoPais; }
    @Override public String toString() { return firma(); }
}
final class Inscripcion extends Entidad {
    public final String idPersona,actividad;
    public Inscripcion(String codigo,String idPersona,String actividad) { super(codigo);this.idPersona=idPersona;this.actividad=actividad; }
    public String firma() { return id+"|"+idPersona+"|"+actividad; }
    @Override public String toString() { return firma(); }
}
