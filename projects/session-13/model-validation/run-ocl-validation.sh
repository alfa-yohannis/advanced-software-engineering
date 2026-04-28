#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
PLUGINS="${PLUGINS:-/data1/programs/eclipse/plugins}"

CP="$ROOT/target/classes:$(ls \
  "$PLUGINS"/org.eclipse.ocl{,.ecore,.common}_*.jar \
  "$PLUGINS"/org.eclipse.emf.{common,ecore,ecore.xmi}_*.jar \
  "$PLUGINS"/lpg.runtime.java_*.jar \
  "$PLUGINS"/org.apache.commons.collections_*.jar | tr '\n' ':')"

mkdir -p "$ROOT/target/classes"
javac -d "$ROOT/target/classes" -cp "$CP" "$ROOT/src/validation/MediaflowOclValidationRunner.java"

cd "$ROOT"
java -cp "$CP" validation.MediaflowOclValidationRunner "$@"
