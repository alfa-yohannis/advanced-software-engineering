#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
PLUGINS="${PLUGINS:-/data1/programs/eclipse/plugins}"

CP="$ROOT/target/classes:$(ls \
  "$PLUGINS"/org.eclipse.epsilon.{common,eol.engine,erl.engine,evl.engine,emc.emf}_2.9.0*.jar \
  "$PLUGINS"/org.eclipse.emf.{common,ecore,ecore.xmi}_*.jar \
  "$PLUGINS"/org.antlr.runtime_*.jar \
  "$PLUGINS"/org.apache.commons.collections_*.jar \
  "$PLUGINS"/com.google.guava_*.jar | tr '\n' ':')"

mkdir -p "$ROOT/target/classes"
javac -d "$ROOT/target/classes" -cp "$CP" "$ROOT/src/validation/MediaflowEvlValidationRunner.java"

cd "$ROOT"
java -cp "$CP" validation.MediaflowEvlValidationRunner "$@"
