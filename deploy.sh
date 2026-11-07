#!/usr/bin/env bash
set -euo pipefail

BRANCH="main"
WORKFLOW_FILE="tests.yml"   # change if your workflow filename differs
COMPOSE_DIR="projects/session-04"
COMPOSE_FILE="${COMPOSE_DIR}/docker-compose.yml"

# --------------------------------------------------------------------
# OPTIONAL: push code to GitHub (commented on purpose)
# --------------------------------------------------------------------
# echo "==> Pushing ${BRANCH} to GitHub"
# git push -u origin "${BRANCH}"

# --------------------------------------------------------------------
# CI trigger (GitHub)
# --------------------------------------------------------------------

gh workflow run "${WORKFLOW_FILE}" --ref "${BRANCH}"

echo "==> Waiting for GitHub Actions to start..."
sleep 10

RUN_ID="$(
  gh run list \
    --workflow "${WORKFLOW_FILE}" \
    --branch "${BRANCH}" \
    --limit 1 \
    --json databaseId \
    --jq '.[0].databaseId'
)"

echo "==> Watching workflow run ${RUN_ID}"

if gh run watch "${RUN_ID}" --exit-status; then
  echo "==> Tests passed ✅"
else
  echo "==> Tests failed ❌  Deployment aborted."
  exit 1
fi

# --------------------------------------------------------------------
# Local deployment (Docker Compose)
# --------------------------------------------------------------------

echo "==> Restarting local app with Docker Compose"

docker compose -f "${COMPOSE_FILE}" down || true
docker compose -f "${COMPOSE_FILE}" build
docker compose -f "${COMPOSE_FILE}" up
