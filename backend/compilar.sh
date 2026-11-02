#!/usr/bin/env bash
# Compila el backend en bin/. Requiere JDK 21.
set -euo pipefail
cd "$(dirname "$0")"
rm -rf bin
mkdir -p bin
find src -name '*.java' > bin/fuentes.txt
javac -encoding UTF-8 -d bin @bin/fuentes.txt
rm bin/fuentes.txt
echo "Compilado en bin/"
