#!/usr/bin/env bash
# Build the WAR and run it as the ROOT app on Tomcat 9 in the foreground (http://localhost:8080).
set -euo pipefail
cd "$(dirname "$0")/.."
: "${CATALINA_HOME:=$HOME/tools/apache-tomcat-9}"
export DB_URL="${DB_URL:-jdbc:sqlserver://localhost:1433;databaseName=JavaClass;user=sa;password=${MSSQL_SA_PASSWORD:-Banking#Dev2024};encrypt=true;trustServerCertificate=true}"

mvn -q -B package
rm -rf "$CATALINA_HOME/webapps/ROOT" "$CATALINA_HOME/webapps/ROOT.war"
cp target/ROOT.war "$CATALINA_HOME/webapps/ROOT.war"
exec "$CATALINA_HOME/bin/catalina.sh" run
