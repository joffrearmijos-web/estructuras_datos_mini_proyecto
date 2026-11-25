#!/usr/bin/env bash
set -euo pipefail
cd -- "$(dirname -- "$0")"
# Un solo comando: compilar, generar el JAR con los datos y abrir la aplicación.
bash ./compilar.sh
exec java -jar dist/BaseColecciones.jar "$@"
