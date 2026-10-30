#!/usr/bin/env bash
# Sourced by the other scripts: sets MSSQL_SA_PASSWORD for the local dev container.
# Uses the caller's value, else a random one generated once into the git-ignored .dev-db-password.
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PASSWORD_FILE="$REPO_ROOT/.dev-db-password"
if [[ -z "${MSSQL_SA_PASSWORD:-}" ]]; then
  if [[ ! -s "$PASSWORD_FILE" ]]; then
    (umask 077; echo "Dev#$(openssl rand -hex 16)" > "$PASSWORD_FILE")
  fi
  MSSQL_SA_PASSWORD="$(cat "$PASSWORD_FILE")"
fi
export MSSQL_SA_PASSWORD
