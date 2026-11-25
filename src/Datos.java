import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Lee el CSV original incluido como recurso del ejecutable. La carga no se mide. */
public final class Datos {
    public final List<Pais> paises;
    public final List<Persona> personas;
    public final List<Inscripcion> inscripciones;
    public final int filas;
    public static final Set<String> ACTIVIDADES=Collections.unmodifiableSet(new LinkedHashSet<>(
        Arrays.asList("Conferencias","Taller de Java","Taller de datos")));
    public Datos() throws IOException {
        InputStream recurso=Datos.class.getResourceAsStream("/datos/congreso_datos.csv");
        if(recurso==null) throw new IOException("Falta el recurso datos/congreso_datos.csv. Vuelvan a compilar con los archivos incluidos.");
        Map<String,Pais> pp=new LinkedHashMap<>();Map<String,Persona> pe=new LinkedHashMap<>();
        Map<String,Inscripcion> ii=new LinkedHashMap<>();int n=0;
        try(BufferedReader r=new BufferedReader(new InputStreamReader(recurso,StandardCharsets.UTF_8))) {
            String cabecera=r.readLine();
            if(cabecera==null || !cabecera.replace("\ufeff","").equals("id_persona,nombre_persona,codigo_pais,nombre_pais,codigo_inscripcion,actividad"))
                throw new IOException("Cabecera CSV inesperada.");
            String linea;while((linea=r.readLine())!=null) {
                if(linea.isEmpty())continue;List<String> c=campos(linea);n++;
                if(c.size()!=6)throw new IOException("Fila CSV inválida: "+n);
                Pais pais=new Pais(c.get(2),c.get(3));Persona persona=new Persona(c.get(0),c.get(1),c.get(2));
                Pais previoPais=pp.putIfAbsent(pais.id,pais);Persona previoPersona=pe.putIfAbsent(persona.id,persona);
                if((previoPais!=null && !previoPais.firma().equals(pais.firma())) ||
                   (previoPersona!=null && !previoPersona.firma().equals(persona.firma())))
                    throw new IOException("Entidad con datos contradictorios en fila "+n);
                if(!c.get(4).isEmpty()) {
                    if(!ACTIVIDADES.contains(c.get(5)))throw new IOException("Actividad no permitida.");
                    Inscripcion ins=new Inscripcion(c.get(4),persona.id,c.get(5));
                    if(ii.putIfAbsent(ins.id,ins)!=null)throw new IOException("Inscripción duplicada: "+ins.id);
                } else if(!c.get(5).isEmpty())throw new IOException("Actividad sin código de inscripción.");
            }
        }
        filas=n;paises=Collections.unmodifiableList(new ArrayList<>(pp.values()));
        personas=Collections.unmodifiableList(new ArrayList<>(pe.values()));
        inscripciones=Collections.unmodifiableList(new ArrayList<>(ii.values()));
        if(filas!=7500 || paises.size()!=12 || personas.size()!=6000 || inscripciones.size()!=6900)
            throw new IOException("El CSV no coincide con la carga común: 7500 filas, 12 países, 6000 personas y 6900 inscripciones.");
    }
    private static List<String> campos(String linea) throws IOException {
        List<String> r=new ArrayList<>();StringBuilder campo=new StringBuilder();boolean comillas=false;
        for(int i=0;i<linea.length();i++) {
            char c=linea.charAt(i);
            if(c=='"') {
                if(comillas && i+1<linea.length() && linea.charAt(i+1)=='"') { campo.append('"');i++; }
                else comillas=!comillas;
            } else if(c==',' && !comillas) { r.add(campo.toString());campo.setLength(0); }
            else campo.append(c);
        }
        if(comillas)throw new IOException("Comillas CSV sin cerrar.");r.add(campo.toString());return r;
    }
}
