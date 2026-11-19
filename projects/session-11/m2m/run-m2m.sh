#!/usr/bin/env bash
# Runs the ATL and the ETL transformation outside Eclipse, with the ATL,
# Epsilon, UML2 and EMF jars of an Eclipse installation on the classpath.
# Usage:
#   ./run-m2m.sh                        both transformations on flow1 and flow2,
#                                       written to output/activity{1,2}{atl,etl}.uml
#   ./run-m2m.sh atl|etl INPUT OUTPUT   one transformation on one model
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"

usage() {
  echo "Usage: $0 [atl|etl INPUT.xmi OUTPUT.uml]" >&2
  exit 2
}
if [ $# -ne 0 ] && { [ $# -ne 3 ] || { [ "$1" != atl ] && [ "$1" != etl ]; }; }; then
  usage
fi

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
    echo "$PLUGINS has no $1 jar. Install ATL, Epsilon and UML2 into this Eclipse first (see the README)." >&2
    exit 2
  fi
  printf '%s' "$jar"
}

CP="$ROOT/target/classes"
for bundle in \
    org.eclipse.m2m.atl.emftvm \
    org.eclipse.m2m.atl.emftvm.trace \
    org.eclipse.m2m.atl.emftvm.compiler \
    org.eclipse.m2m.atl.engine \
    org.eclipse.m2m.atl.dsls \
    org.eclipse.m2m.atl.common \
    org.eclipse.m2m.atl.core \
    org.eclipse.m2m.atl.core.emf \
    org.objectweb.asm \
    org.eclipse.epsilon.common \
    org.eclipse.epsilon.eol.engine \
    org.eclipse.epsilon.erl.engine \
    org.eclipse.epsilon.etl.engine \
    org.eclipse.epsilon.emc.emf \
    org.eclipse.uml2.uml \
    org.eclipse.uml2.uml.resources \
    org.eclipse.uml2.uml.profile.standard \
    org.eclipse.uml2.types \
    org.eclipse.uml2.common \
    org.eclipse.emf.mapping.ecore2xml \
    org.eclipse.emf.common \
    org.eclipse.emf.ecore \
    org.eclipse.emf.ecore.xmi \
    org.antlr.runtime \
    org.apache.commons.collections \
    com.google.guava; do
  CP="$CP:$(newest_jar "$bundle")"
done

mkdir -p "$ROOT/target/classes"
javac -d "$ROOT/target/classes" -cp "$CP" "$ROOT"/src/m2m/*.java "$ROOT/tools/CompileAtl.java"

cd "$ROOT"
# The ATL runner loads compiled EMFTVM bytecode, so compile the .atl file first.
java -cp "$CP" m2m.CompileAtl transformer/mediaflow2activity.atl transformer/mediaflow2activity.emftvm

run() {
  case "$1" in
    atl) java -cp "$CP" m2m.MediaflowToActivityATLRunner "$2" "$3" ;;
    etl) java -cp "$CP" m2m.MediaflowToActivityETLRunner "$2" "$3" ;;
  esac
}

if [ $# -eq 3 ]; then
  run "$@"
else
  for i in 1 2; do
    run atl "input/flow$i.xmi" "output/activity${i}atl.uml"
    run etl "input/flow$i.xmi" "output/activity${i}etl.uml"
  done
fi
