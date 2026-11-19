#!/usr/bin/env bash
# Compiles the Java code that EMF generated from mediaflow.ecore, then loads
# MediaFlow models with it, prints what they contain and validates them.
# Usage: ./check-models.sh [model ...]   (default: models/flow1.model models/flow2.model)
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
    echo "$PLUGINS has no $1 jar. Install EMF into this Eclipse first (see the README)." >&2
    exit 2
  fi
  printf '%s' "$jar"
}

CP="$ROOT/target/classes"
for bundle in org.eclipse.emf.common org.eclipse.emf.ecore org.eclipse.emf.ecore.xmi; do
  CP="$CP:$(newest_jar "$bundle")"
done

SOURCES=()
while IFS= read -r file; do
  SOURCES+=("$file")
done < <(find "$ROOT/src" -name '*.java')

mkdir -p "$ROOT/target/classes"
javac -d "$ROOT/target/classes" -cp "$CP" "${SOURCES[@]}" "$ROOT/tools/CheckModels.java"

cd "$ROOT"
java -cp "$CP" CheckModels "$@"
