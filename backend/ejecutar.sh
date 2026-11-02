#!/usr/bin/env bash
# Arranca el backend. Se ejecuta desde backend/ porque ahí está config/.
set -euo pipefail
cd "$(dirname "$0")"
exec java -Dfile.encoding=UTF-8 -cp "bin:lib/*" co.edu.unbosque.Aplicacion
