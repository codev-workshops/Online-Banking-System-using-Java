#!/usr/bin/env bash
# Build the WAR and run it as the ROOT app on Tomcat 9 in the foreground (http://localhost:8080).
set -euo pipefail
cd "$(dirname "$0")/.."
: "${CATALINA_HOME:=$HOME/tools/apache-tomcat-9}"
source scripts/dev-db-env.sh
export DB_URL="${DB_URL:-jdbc:sqlserver://localhost:1433;databaseName=JavaClass;encrypt=true;trustServerCertificate=true}"
export DB_USER="${DB_USER:-sa}"
export DB_PASSWORD="${DB_PASSWORD:-$MSSQL_SA_PASSWORD}"

mvn -q -B package
rm -rf "$CATALINA_HOME/webapps/ROOT" "$CATALINA_HOME/webapps/ROOT.war"
cp target/ROOT.war "$CATALINA_HOME/webapps/ROOT.war"
exec "$CATALINA_HOME/bin/catalina.sh" run
