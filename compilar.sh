#!/usr/bin/env bash
set -euo pipefail
cd -- "$(dirname -- "$0")"
for herramienta in java javac jar; do
  if ! command -v "$herramienta" >/dev/null 2>&1; then
    echo "Falta $herramienta. Instalen un JDK 11 o superior y configuren PATH." >&2
    exit 1
  fi
done
mkdir -p build/classes/datos dist
rm -f dist/BaseColecciones.jar
javac --release 11 -encoding UTF-8 -d build/classes src/*.java
cp datos/congreso_datos.csv build/classes/datos/congreso_datos.csv
jar cfe dist/BaseColecciones.jar App -C build/classes .
echo "Ejecutable generado: dist/BaseColecciones.jar"
echo "Abran desde el terminal: java -jar dist/BaseColecciones.jar"
