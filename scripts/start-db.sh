#!/usr/bin/env bash
# Start a local SQL Server 2022 container and apply db/schema.sql (idempotent).
set -euo pipefail
cd "$(dirname "$0")/.."
NAME="${DB_CONTAINER:-banking-mssql}"
SA_PASSWORD="${MSSQL_SA_PASSWORD:-Banking#Dev2024}"
SQLCMD=(/opt/mssql-tools18/bin/sqlcmd -C -S localhost -U sa -P "$SA_PASSWORD" -b)

if ! docker ps -a --format '{{.Names}}' | grep -qx "$NAME"; then
  docker run -d --name "$NAME" -e ACCEPT_EULA=Y -e MSSQL_SA_PASSWORD="$SA_PASSWORD" \
    -p 1433:1433 mcr.microsoft.com/mssql/server:2022-latest >/dev/null
else
  docker start "$NAME" >/dev/null
fi

for _ in $(seq 1 60); do
  docker exec "$NAME" "${SQLCMD[@]}" -Q "SELECT 1" >/dev/null 2>&1 && break
  sleep 2
done
docker exec -i "$NAME" "${SQLCMD[@]}" -i /dev/stdin < db/schema.sql >/dev/null
echo "SQL Server ready on localhost:1433 (database JavaClass)"
