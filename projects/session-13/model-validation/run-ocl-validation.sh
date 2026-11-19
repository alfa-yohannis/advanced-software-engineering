#!/usr/bin/env bash
# Compiles MediaflowOclValidationRunner and runs it outside Eclipse, with the
# OCL and EMF jars of an Eclipse installation on the classpath.
# Usage: ./run-ocl-validation.sh [inputFileOrDirectory] [oclPath] [metamodelPath]
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"

if [ -z "${ECLIPSE_HOME:-}" ]; then
  echo "ECLIPSE_HOME is not set. Set it to the Eclipse folder that contains plugins/, for example:" >&2
  echo "  export ECLIPSE_HOME=\$HOME/eclipse/modeling-2026-03/eclipse" >&2
  exit 2
fi
PLUGINS="$ECLIPSE_HOME/plugins"
if [ ! -d "$PLUGINS" ]; then
  echo "$PLUGINS does not exist. ECLIPSE_HOME must be the Eclipse folder that contains plugins/." >&2
  exit 2
fi

# Prints the newest jar of a bundle. An Eclipse that was updated can hold several.
newest_jar() {
  local jar
  jar="$(ls "$PLUGINS/$1"_*.jar 2>/dev/null | sort -V | tail -n 1)"
  if [ -z "$jar" ]; then
    echo "$PLUGINS has no $1 jar. Install OCL and EMF into this Eclipse first (see the README)." >&2
    exit 2
  fi
  printf '%s' "$jar"
}

CP="$ROOT/target/classes"
for bundle in \
    org.eclipse.ocl \
    org.eclipse.ocl.ecore \
    org.eclipse.ocl.common \
    org.eclipse.emf.common \
    org.eclipse.emf.ecore \
    org.eclipse.emf.ecore.xmi \
    lpg.runtime.java \
    org.apache.commons.collections; do
  CP="$CP:$(newest_jar "$bundle")"
done

mkdir -p "$ROOT/target/classes"
javac -d "$ROOT/target/classes" -cp "$CP" "$ROOT/src/validation/MediaflowOclValidationRunner.java"

cd "$ROOT"
java -cp "$CP" validation.MediaflowOclValidationRunner "$@"
