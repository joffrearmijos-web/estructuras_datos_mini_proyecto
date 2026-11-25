import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public final class App extends JPanel {
    private final Datos datos;
    private final Map<Estado.Tipo,Estado> estadosLibres=new EnumMap<>(Estado.Tipo.class);
    private final Map<Estado.Tipo,String> informesLibres=new EnumMap<>(Estado.Tipo.class);
    private final Map<Estado.Tipo,String> filtros=new EnumMap<>(Estado.Tipo.class);
    private final Map<Estado.Tipo,Map<String,Integer>> conteos=new EnumMap<>(Estado.Tipo.class);
    private final JTabbedPane secciones=new JTabbedPane();
    private final JTextField idPersona=new JTextField("P06000",14);
    private final JTextField nuevaInscripcion=new JTextField("I06000-N1",14);
    private final JTextField codigoCancelar=new JTextField("I00002-1",14);
    private final JComboBox<String> paisDestino=new JComboBox<>();
    private final JComboBox<String> actividad=new JComboBox<>(Datos.ACTIVIDADES.toArray(new String[0]));
    private final JTextArea salidaLibre=new JTextArea();
    private final DefaultTableModel conteoPaises=modelo("País","Personas");
    private final JTabbedPane entidades=new JTabbedPane();
    private final Map<Estado.Tipo,Estado> estados=new EnumMap<>(Estado.Tipo.class);
    private List<Estado.Tipo> elegidas=new ArrayList<>(Arrays.asList(Estado.Tipo.ArrayList,Estado.Tipo.LinkedList,Estado.Tipo.HashSet,Estado.Tipo.TreeSet));
    private final JComboBox<Estado.Tipo> activa=new JComboBox<>();
    private final List<AbstractButton> controles=new ArrayList<>();
    private final JLabel estadoTexto=new JLabel(),totales=new JLabel();
    private final JTextArea salida=new JTextArea(5,70);
    private final DefaultTableModel personas=modelo("ID","Persona","País"),paises=modelo("Código","País"),inscripciones=modelo("Código","Persona","Actividad");
    private final DefaultTableModel tiempos=modelo("Método","ArrayList","LinkedList","HashSet","TreeSet");
    private final Grafico grafico=new Grafico();
    private final Map<Estado.Tipo,String> informes=new EnumMap<>(Estado.Tipo.class);
    private static DefaultTableModel modelo(String... columnas) {
        return new DefaultTableModel(columnas,0) { @Override public boolean isCellEditable(int f,int c) { return false; } };
    }
    private Estado estadoComparacion() { Estado.Tipo t=(Estado.Tipo)activa.getSelectedItem();return estados.computeIfAbsent(t,k->new Estado(k,datos)); }
    private Estado estadoLibre() { Estado.Tipo t=(Estado.Tipo)activa.getSelectedItem();return estadosLibres.computeIfAbsent(t,k->new Estado(k,datos)); }
    private Estado actual() { return secciones.getSelectedIndex()==1?estadoComparacion():estadoLibre(); }
    public App(Datos datos) {
        this.datos=datos;setLayout(new BorderLayout(8,8));setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        setBackground(Color.WHITE);setPreferredSize(new Dimension(1200,800));
        JPanel arriba=new JPanel();arriba.setLayout(new BoxLayout(arriba,BoxLayout.Y_AXIS));arriba.setOpaque(false);
        JLabel titulo=new JLabel("CONGRESO · PERSONAS, PAÍSES E INSCRIPCIONES");
        titulo.setFont(new Font("SansSerif",Font.BOLD,20));titulo.setForeground(new Color(22,63,104));arriba.add(titulo);
        arriba.add(Box.createVerticalStrut(6));
        arriba.add(new JLabel("CSV: 6000 personas · 12 países · 6900 inscripciones iniciales. ArrayList funcional."));
        JPanel seleccion=new JPanel(new FlowLayout(FlowLayout.LEFT,6,4));seleccion.setOpaque(false);seleccion.setAlignmentX(LEFT_ALIGNMENT);
        seleccion.add(new JLabel("Colección activa:"));seleccion.add(activa);
        JButton elegir=boton("Elegir las otras 3 estructuras");elegir.addActionListener(ev->elegir());seleccion.add(elegir);arriba.add(seleccion);
        arriba.add(estadoTexto);arriba.add(Box.createVerticalStrut(5));add(arriba,BorderLayout.NORTH);

        for(Pais pais:datos.paises)paisDestino.addItem(pais.id+" · "+pais.nombre);
        for(int i=0;i<paisDestino.getItemCount();i++)if(paisDestino.getItemAt(i).startsWith("CRI"))paisDestino.setSelectedIndex(i);
        actividad.setSelectedItem("Taller de Java");
        JPanel formulario=new JPanel();formulario.setLayout(new BoxLayout(formulario,BoxLayout.Y_AXIS));
        formulario.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));formulario.setBackground(Color.WHITE);
        agregarCampo(formulario,"ID de persona",idPersona);
        JButton buscar=boton("Buscar persona");buscar.setName("libre_buscar");buscar.addActionListener(ev->operarLibre(Flujo.Metodo.BUSCAR));agregarBoton(formulario,buscar);
        agregarCampo(formulario,"Nuevo país",paisDestino);
        JButton corregir=boton("Aplicar cambio de país");corregir.setName("libre_corregir");corregir.addActionListener(ev->operarLibre(Flujo.Metodo.CORREGIR));agregarBoton(formulario,corregir);
        agregarCampo(formulario,"Código de nueva inscripción",nuevaInscripcion);agregarCampo(formulario,"Actividad",actividad);
        JButton registrar=boton("Registrar inscripción");registrar.setName("libre_registrar");registrar.addActionListener(ev->operarLibre(Flujo.Metodo.REGISTRAR));agregarBoton(formulario,registrar);
        agregarCampo(formulario,"Código de inscripción a cancelar",codigoCancelar);
        JButton cancelar=boton("Cancelar inscripción");cancelar.setName("libre_cancelar");cancelar.addActionListener(ev->operarLibre(Flujo.Metodo.CANCELAR));agregarBoton(formulario,cancelar);
        JButton contar=boton("Contar personas por país");contar.setName("libre_contar");contar.addActionListener(ev->operarLibre(Flujo.Metodo.CONTAR));agregarBoton(formulario,contar);
        JButton todas=boton("Ver todas las personas");todas.addActionListener(ev->{filtros.remove(estadoLibre().tipo);actualizar();entidades.setSelectedIndex(0);});agregarBoton(formulario,todas);
        JButton restaurar=boton("Restaurar datos de uso libre");restaurar.addActionListener(ev->{Estado e=estadoLibre();e.reiniciar(datos);filtros.remove(e.tipo);conteos.remove(e.tipo);informesLibres.remove(e.tipo);actualizar();});agregarBoton(formulario,restaurar);
        formulario.add(Box.createVerticalGlue());
        JScrollPane scrollFormulario=new JScrollPane(formulario);scrollFormulario.setMinimumSize(new Dimension(280,200));scrollFormulario.setPreferredSize(new Dimension(315,400));
        scrollFormulario.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JTable tablaPersonas=new JTable(personas);tablaPersonas.setRowHeight(24);tablaPersonas.setName("tabla_personas");
        tablaPersonas.getSelectionModel().addListSelectionListener(ev->{if(!ev.getValueIsAdjusting() && tablaPersonas.getSelectedRow()>=0)idPersona.setText(personas.getValueAt(tablaPersonas.getSelectedRow(),0).toString());});
        entidades.addTab("Personas",new JScrollPane(tablaPersonas));entidades.addTab("Países",tabla(paises));
        JTable tablaInscripciones=new JTable(inscripciones);tablaInscripciones.setRowHeight(24);
        tablaInscripciones.getSelectionModel().addListSelectionListener(ev->{if(!ev.getValueIsAdjusting() && tablaInscripciones.getSelectedRow()>=0){int fila=tablaInscripciones.getSelectedRow();codigoCancelar.setText(inscripciones.getValueAt(fila,0).toString());idPersona.setText(inscripciones.getValueAt(fila,1).toString());}});
        entidades.addTab("Inscripciones",new JScrollPane(tablaInscripciones));entidades.addTab("Conteo por país",tabla(conteoPaises));
        entidades.setMinimumSize(new Dimension(320,180));
        JSplitPane trabajo=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,scrollFormulario,entidades);trabajo.setResizeWeight(0);trabajo.setDividerLocation(315);trabajo.setBorder(null);
        JScrollPane resultadoLibre=panelSalida(salidaLibre,"Resultado de la operación");
        JSplitPane gestion=new JSplitPane(JSplitPane.VERTICAL_SPLIT,trabajo,resultadoLibre);gestion.setResizeWeight(.82);gestion.setDividerLocation(460);gestion.setBorder(null);
        secciones.addTab("Gestión interactiva",gestion);

        JPanel acciones=new JPanel();acciones.setLayout(new BoxLayout(acciones,BoxLayout.Y_AXIS));acciones.setBorder(BorderFactory.createTitledBorder("Escenarios comunes · 1 a 5"));
        for(Flujo.Metodo m:Flujo.Metodo.values()) {
            JButton btn=boton((m.ordinal()+1)+". "+m.etiqueta);btn.setName("prueba_"+m.name());btn.addActionListener(ev->probar(m));agregarBoton(acciones,btn);
        }
        JButton reiniciar=boton("Reiniciar comparación");reiniciar.addActionListener(ev->{if(JOptionPane.showConfirmDialog(this,"¿Restaurar el CSV y borrar los tiempos de la comparación?","Nueva comparación",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)reiniciar();});agregarBoton(acciones,reiniciar);
        JButton csv=boton("Exportar tiempos CSV");csv.addActionListener(ev->exportar());agregarBoton(acciones,csv);acciones.add(Box.createVerticalGlue());
        JScrollPane listaAcciones=new JScrollPane(acciones);listaAcciones.setPreferredSize(new Dimension(270,300));listaAcciones.setMinimumSize(new Dimension(245,180));
        JScrollPane tablaTiempos=tabla(tiempos);tablaTiempos.setMinimumSize(new Dimension(350,170));tablaTiempos.setPreferredSize(new Dimension(700,190));
        grafico.setMinimumSize(new Dimension(350,140));grafico.setPreferredSize(new Dimension(700,220));
        JSplitPane visuales=new JSplitPane(JSplitPane.VERTICAL_SPLIT,tablaTiempos,grafico);visuales.setResizeWeight(.5);visuales.setDividerLocation(200);visuales.setBorder(null);
        JSplitPane areaPruebas=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,listaAcciones,visuales);areaPruebas.setResizeWeight(0);areaPruebas.setDividerLocation(270);areaPruebas.setBorder(null);
        JSplitPane pruebas=new JSplitPane(JSplitPane.VERTICAL_SPLIT,areaPruebas,panelSalida(salida,"Resultado del escenario"));pruebas.setResizeWeight(.78);pruebas.setDividerLocation(420);pruebas.setBorder(null);
        JPanel comparacion=new JPanel(new BorderLayout(6,6));comparacion.add(new JLabel("Estas pruebas usan una copia inicial independiente del uso libre y conservan sus cambios durante el flujo."),BorderLayout.NORTH);
        comparacion.add(pruebas,BorderLayout.CENTER);comparacion.add(totales,BorderLayout.SOUTH);secciones.addTab("Comparación de tiempos",comparacion);
        secciones.addChangeListener(ev->{if(activa.getSelectedItem()!=null)actualizar();});add(secciones,BorderLayout.CENTER);
        JLabel firma=new JLabel("MSc. Lessette Carolina Zambrano Zurita – Docente UEES");firma.setForeground(new Color(90,100,110));add(firma,BorderLayout.SOUTH);
        for(Estado.Tipo t:elegidas)activa.addItem(t);activa.addActionListener(ev->actualizar());actualizar();
    }
    private static void agregarCampo(JPanel p,String etiqueta,JComponent campo) {
        JLabel l=new JLabel(etiqueta);l.setAlignmentX(LEFT_ALIGNMENT);p.add(l);p.add(Box.createVerticalStrut(3));
        campo.setAlignmentX(LEFT_ALIGNMENT);campo.setMaximumSize(new Dimension(Integer.MAX_VALUE,30));p.add(campo);p.add(Box.createVerticalStrut(6));
    }
    private static void agregarBoton(JPanel p,JButton b) {
        b.setAlignmentX(LEFT_ALIGNMENT);b.setMaximumSize(new Dimension(Integer.MAX_VALUE,33));p.add(b);p.add(Box.createVerticalStrut(7));
    }
    private JScrollPane panelSalida(JTextArea texto,String titulo) {
        texto.setEditable(false);texto.setLineWrap(true);texto.setWrapStyleWord(true);texto.setFont(new Font("SansSerif",Font.PLAIN,13));
        JScrollPane scroll=new JScrollPane(texto);scroll.setBorder(BorderFactory.createTitledBorder(titulo));
        scroll.setMinimumSize(new Dimension(300,90));scroll.setPreferredSize(new Dimension(600,115));return scroll;
    }
    private void operarLibre(Flujo.Metodo metodo) {
        Estado e=estadoLibre();String id=idPersona.getText().trim();String codigo=nuevaInscripcion.getText().trim();
        String cancelar=codigoCancelar.getText().trim();String pais=((String)paisDestino.getSelectedItem()).substring(0,3);
        String act=(String)actividad.getSelectedItem();ocupado(true);salidaLibre.setText("Ejecutando "+metodo.etiqueta+"…");
        new SwingWorker<Object,Void>() {
            protected Object doInBackground() {
                Estado.Instantanea antes=e.foto();
                try {
                    switch(metodo) {
                        case BUSCAR:return Operaciones.buscarPersona(e,id);
                        case CORREGIR:return Operaciones.corregirPais(e,id,pais);
                        case REGISTRAR:return Operaciones.registrarInscripcion(e,codigo,id,act);
                        case CANCELAR:return Operaciones.cancelarInscripcion(e,cancelar);
                        default:return Operaciones.contarPersonasPorPais(e);
                    }
                }catch(RuntimeException error){e.restaurar(antes);throw error;}
            }
            protected void done() {
                try {
                    Object r=get();String texto;
                    if(metodo==Flujo.Metodo.BUSCAR) {
                        filtros.put(e.tipo,id);entidades.setSelectedIndex(0);
                        if(r==null)texto="No existe la persona "+id+". Pueden escribir otro ID y buscar de nuevo.";
                        else {Persona p=(Persona)r;long n=e.inscripciones.valores().stream().filter(i->i.idPersona.equals(p.id)).count();texto="Persona encontrada: "+p.id+" · "+p.nombre+" · País: "+p.codigoPais+" · Inscripciones: "+n+". La tabla muestra esta persona y sus inscripciones.";}
                    } else if(metodo==Flujo.Metodo.CONTAR) {
                        @SuppressWarnings("unchecked") Map<String,Integer> mapa=(Map<String,Integer>)r;conteos.put(e.tipo,new TreeMap<>(mapa));entidades.setSelectedIndex(3);
                        texto="Conteo completo por país: "+mapa+". Personas totales: "+mapa.values().stream().mapToInt(Integer::intValue).sum()+".";
                    } else {
                        boolean realizado=Boolean.TRUE.equals(r);
                        if(realizado)conteos.remove(e.tipo);
                        if(metodo==Flujo.Metodo.CORREGIR) {filtros.put(e.tipo,id);entidades.setSelectedIndex(0);texto=realizado?"País actualizado: "+id+" ahora pertenece a "+pais+".":"No se cambió el país: verifiquen la persona o si ya pertenece al país seleccionado.";}
                        else if(metodo==Flujo.Metodo.REGISTRAR) {filtros.put(e.tipo,id);entidades.setSelectedIndex(2);texto=realizado?"Inscripción registrada: "+codigo+" · "+id+" · "+act+".":"No se registró: revisen el ID, el código de inscripción o si la persona ya está inscrita en esa actividad.";}
                        else {texto=realizado?"Inscripción cancelada: "+cancelar+". Se conserva la persona.":"No existe la inscripción "+cancelar+". No se modificaron los datos.";}
                    }
                    informesLibres.put(e.tipo,texto);
                }catch(Exception error){Throwable c=error.getCause()!=null?error.getCause():error;informesLibres.put(e.tipo,"No se completó la operación: "+c.getMessage());}
                finally{ocupado(false);actualizar();}
            }
        }.execute();
    }
    private JScrollPane tabla(DefaultTableModel modelo) { JTable t=new JTable(modelo);t.setRowHeight(24);return new JScrollPane(t); }
    private JButton boton(String nombre) { JButton b=new JButton(nombre);controles.add(b);return b; }
    private void ocupado(boolean b) {
        activa.setEnabled(!b);for(AbstractButton x:controles)x.setEnabled(!b);
        for(int i=0;i<secciones.getTabCount();i++)secciones.setEnabledAt(i,!b);
        idPersona.setEnabled(!b);nuevaInscripcion.setEnabled(!b);codigoCancelar.setEnabled(!b);paisDestino.setEnabled(!b);actividad.setEnabled(!b);
    }
    private void elegir() {
        Estado.Tipo[] opciones={Estado.Tipo.LinkedList,Estado.Tipo.HashSet,Estado.Tipo.TreeSet,Estado.Tipo.HashMap,Estado.Tipo.TreeMap};
        JPanel p=new JPanel(new GridLayout(0,2,6,6));List<JComboBox<Estado.Tipo>> cajas=new ArrayList<>();
        for(int i=0;i<3;i++) { JComboBox<Estado.Tipo> c=new JComboBox<>(opciones);c.setSelectedItem(elegidas.get(i+1));cajas.add(c);p.add(new JLabel("Alternativa "+(i+1)));p.add(c); }
        p.add(new JLabel("Aplicar reinicia la comparación."));
        if(JOptionPane.showConfirmDialog(this,p,"Elijan tres estructuras distintas",JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION)return;
        List<Estado.Tipo> nuevas=new ArrayList<>();nuevas.add(Estado.Tipo.ArrayList);for(JComboBox<Estado.Tipo> c:cajas)nuevas.add((Estado.Tipo)c.getSelectedItem());
        if(new HashSet<>(nuevas).size()!=4) { JOptionPane.showMessageDialog(this,"Las tres alternativas deben ser distintas.");return; }
        elegidas=nuevas;estados.clear();estadosLibres.clear();filtros.clear();conteos.clear();informesLibres.clear();informes.clear();Mediciones.reiniciar();
        // Sustituir el modelo evita eventos transitorios con selección vacía.
        activa.setModel(new DefaultComboBoxModel<>(elegidas.toArray(new Estado.Tipo[0])));actualizar();
    }
    private void reiniciar() { for(Estado e:estados.values())e.reiniciar(datos);Mediciones.reiniciar();informes.clear();actualizar(); }
    private void probar(Flujo.Metodo m) {
        Estado e=estadoComparacion();ocupado(true);salida.setText("Ejecutando "+m.funcion+" en "+e.tipo+"…");
        new SwingWorker<Flujo.Resultado,Void>() {
            protected Flujo.Resultado doInBackground() { return Flujo.probar(e,datos,m); }
            protected void done() {
                try {
                    Flujo.Resultado r=get();String texto;
                    if(r.repetido)texto="Este escenario ya se completó. Se conservan su resultado, estado y primer tiempo; el clic no vuelve a modificar los datos.";
                    else {
                        texto="ESCENARIO VALIDADO · "+m.funcion+" · "+e.tipo+"\n";
                        texto+=r.tiempoRegistrado?"Primer tiempo registrado.\n":"Operación funcional, sin tiempo registrado. Completen los TODO de Mediciones.java.\n";
                        if(m==Flujo.Metodo.CONTAR) {
                            Map<?,?> conteo=(Map<?,?>)r.salida;for(Pais pais:datos.paises)texto+=pais.id+" · "+pais.nombre+": "+conteo.get(pais.id)+" personas\n";
                        } else texto+="Resultados en el orden del README: "+r.salida;
                    }
                    informes.put(e.tipo,texto);
                } catch(Exception error) {
                    Throwable c=error.getCause()!=null?error.getCause():error;
                    informes.put(e.tipo,"ESCENARIO PENDIENTE · "+m.funcion+"\n"+c.getMessage()+"\nSe conserva el estado anterior al intento. Revisen el TODO o sigan el orden del flujo.");
                } finally { ocupado(false);actualizar(); }
            }
        }.execute();
    }
    static String ms(long ns) { return String.format(Locale.ROOT,"%.6f",ns/1_000_000.0); }
    private void actualizar() {
        Estado e=actual();estadoTexto.setText("Estado activo: "+e.tipo+" · Personas: "+e.personas.valores().size()+" · Países: "+e.paises.valores().size()
            +" · Inscripciones: "+e.inscripciones.valores().size()+(secciones.getSelectedIndex()==0?" · Uso libre": " · Próximo paso: "+(e.siguientePaso==5?"flujo completado":(e.siguientePaso+1)+". "+Flujo.Metodo.values()[e.siguientePaso].etiqueta)));
        Estado libre=estadoLibre();String filtro=filtros.get(libre.tipo);
        personas.setRowCount(0);List<Persona> pe=new ArrayList<>(libre.personas.valores());
        if(filtro!=null)pe.removeIf(p->!p.id.equals(filtro));pe.sort(Comparator.comparing(p->p.id));
        for(int i=0;i<Math.min(pe.size(),500);i++) { Persona p=pe.get(i);personas.addRow(new Object[]{p.id,p.nombre,p.codigoPais}); }
        paises.setRowCount(0);List<Pais> pp=new ArrayList<>(libre.paises.valores());pp.sort(Comparator.comparing(p->p.id));for(Pais p:pp)paises.addRow(new Object[]{p.id,p.nombre});
        inscripciones.setRowCount(0);List<Inscripcion> ii=new ArrayList<>(libre.inscripciones.valores());
        if(filtro!=null)ii.removeIf(i->!i.idPersona.equals(filtro));ii.sort(Comparator.comparing(i->i.id));
        for(int n=0;n<Math.min(ii.size(),500);n++) { Inscripcion i=ii.get(n);inscripciones.addRow(new Object[]{i.id,i.idPersona,i.actividad}); }
        Object[] columnas=new Object[5];columnas[0]="Método";for(int i=0;i<4;i++)columnas[i+1]=elegidas.get(i);tiempos.setColumnIdentifiers(columnas);tiempos.setRowCount(0);
        for(Flujo.Metodo m:Flujo.Metodo.values()) {
            Object[] fila=new Object[5];fila[0]=m.etiqueta;
            for(int i=0;i<4;i++) { Estado.Tipo t=elegidas.get(i);Long ns=Mediciones.TIEMPOS.get(t).get(m);Estado x=estados.get(t);
                fila[i+1]=ns!=null?ms(ns):(x!=null && x.probados.contains(m)?"Probado · sin tiempo":"Pendiente"); }
            tiempos.addRow(fila);
        }
        StringBuilder resumen=new StringBuilder("<html>");for(Estado.Tipo t:elegidas) {
            long suma=0;Map<Flujo.Metodo,Long> tt=Mediciones.TIEMPOS.get(t);for(long ns:tt.values())suma+=ns;
            if(resumen.length()>6)resumen.append("<br>");resumen.append(t).append(": ").append(tt.size()).append("/5 medidos; total ").append(tt.isEmpty()?"—":ms(suma)+" ms");
        }totales.setText(resumen.append("</html>").toString());
        conteoPaises.setRowCount(0);Map<String,Integer> conteo=conteos.get(libre.tipo);
        if(conteo!=null)for(Map.Entry<String,Integer> x:conteo.entrySet())conteoPaises.addRow(new Object[]{x.getKey(),x.getValue()});
        salidaLibre.setText(informesLibres.getOrDefault(libre.tipo,"Escriban un ID y pulsen Buscar persona. Pueden cambiar su país, registrar y cancelar inscripciones con los campos de la izquierda.\nEl uso libre conserva sus cambios y no altera las pruebas comparables. Seleccionen una fila para copiar sus códigos al formulario."));salidaLibre.setCaretPosition(0);
        salida.setText(informes.getOrDefault(estadoComparacion().tipo,"Sigan el flujo 1 a 5. La misma instancia conserva los cambios de cada paso.\n"
            +"Las tablas de personas e inscripciones muestran hasta 500 filas; las operaciones usan todos los registros.\n"
            +"ArrayList ya funciona; las otras tres estructuras y los TODO de tiempos deben completarse."));salida.setCaretPosition(0);grafico.repaint();
    }
    private void exportar() {
        JFileChooser d=new JFileChooser();d.setSelectedFile(new File("tiempos_congreso.csv"));if(d.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)return;
        try(BufferedWriter w=Files.newBufferedWriter(d.getSelectedFile().toPath(),StandardCharsets.UTF_8)) {
            w.write("estructura,metodo,tiempo_ns,estado\n");
            for(Estado.Tipo t:elegidas)for(Flujo.Metodo m:Flujo.Metodo.values()) {
                Long ns=Mediciones.TIEMPOS.get(t).get(m);Estado e=estados.get(t);
                w.write(t+","+m.funcion+","+(ns==null?"":ns)+","+(ns!=null?"MEDIDO":e!=null && e.probados.contains(m)?"PROBADO_SIN_TIEMPO":"PENDIENTE")+"\n");
            }JOptionPane.showMessageDialog(this,"CSV guardado.");
        } catch(IOException error) { JOptionPane.showMessageDialog(this,error.getMessage()); }
    }
    private final class Grafico extends JPanel {
        final Color[] colores={new Color(31,78,121),new Color(41,143,151),new Color(143,105,58),new Color(103,100,157)};
        Grafico() { setBackground(Color.WHITE); }
        protected void paintComponent(Graphics original) {
            super.paintComponent(original);Graphics2D g=(Graphics2D)original.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            long max=0;for(Estado.Tipo t:elegidas)for(long ns:Mediciones.TIEMPOS.get(t).values())max=Math.max(max,ns);
            int ancho=getWidth(),alto=getHeight();g.setColor(Color.DARK_GRAY);
            if(max==0) { g.drawString("Gráfico de barras: aparecerá cuando registren mediciones reales.",18,30);g.dispose();return; }
            int izquierda=85,base=alto-35,tope=30,util=base-tope;double grupo=(ancho-izquierda-20)/5.0;
            g.drawLine(izquierda,tope,izquierda,base);g.drawLine(izquierda,base,ancho-10,base);g.drawString(ms(max)+" ms",4,tope+5);
            for(int i=0;i<4;i++) { int lx=izquierda+i*(ancho-izquierda)/4;g.setColor(colores[i]);g.fillRect(lx,8,10,10);g.drawString(elegidas.get(i).toString(),lx+14,18); }
            String[] nombres={"Buscar","Corregir país","Registrar","Cancelar","Contar por país"};
            for(Flujo.Metodo m:Flujo.Metodo.values()) {
                int x=(int)(izquierda+m.ordinal()*grupo);int barra=Math.max(5,(int)(grupo/6));
                for(int i=0;i<4;i++) { Long ns=Mediciones.TIEMPOS.get(elegidas.get(i)).get(m);if(ns==null)continue;
                    int h=(int)(ns/(double)max*util);g.setColor(colores[i]);g.fillRect(x+i*(barra+2)+6,base-h,barra,Math.max(1,h)); }
                g.setColor(Color.DARK_GRAY);g.drawString(nombres[m.ordinal()],x+5,base+18);
            }g.dispose();
        }
    }
    public static void main(String[] args) throws Exception {
        Datos datos=new Datos();
        if(args.length>0 && args[0].equals("--verificar-base")) {
            for(Estado.Tipo t:Estado.Tipo.values()) { Estado e=new Estado(t,datos);Object identidad=e.personas.instancia();e.reiniciar(datos);
                if(identidad!=e.personas.instancia() || e.personas.valores().size()!=6000)throw new AssertionError(t);
                System.out.println(t+": 12 países, 6000 personas y 6900 inscripciones; instancia persistente."); }
            Estado e=new Estado(Estado.Tipo.ArrayList,datos);for(Flujo.Metodo m:Flujo.Metodo.values())Flujo.probar(e,datos,m);
            if(e.siguientePaso!=5 || !Mediciones.TIEMPOS.get(e.tipo).isEmpty())throw new AssertionError("Base ArrayList");
            System.out.println("ArrayList: cinco escenarios funcionales validados; sin tiempos simulados.");return;
        }
        SwingUtilities.invokeLater(()->{ try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }catch(Exception ignored){}
            App panel=new App(datos);JFrame ventana=new JFrame("Congreso · Gestión y comparación de estructuras");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);ventana.setContentPane(panel);
            Dimension pantalla=Toolkit.getDefaultToolkit().getScreenSize();ventana.setSize(Math.min(1250,pantalla.width-50),Math.min(900,pantalla.height-70));
            ventana.setMinimumSize(new Dimension(880,640));ventana.setLocationRelativeTo(null);ventana.setVisible(true); });
    }
}
