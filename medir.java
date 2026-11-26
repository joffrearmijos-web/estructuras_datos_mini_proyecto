public static Medicion medir (Trabajo trabajo) {
    long inicio = System.nanoTime();
    Object resultado = trabajo.ejecutar();
    long fin = System.nanoTime();
    return new Medicion(resultado, fin - inicio );
}
