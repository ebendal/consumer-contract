#!/bin/bash
set -e -o pipefail -o nounset

APPLICATION_VERSION=$(cat application-version.txt)
echo "Application version: $APPLICATION_VERSION"
SIGN_KEY=$(echo $SIGN_KEY | base64 -d)
mvn -Drevision="$APPLICATION_VERSION" -q -s ./pipeline/settings.xml clean verify -Dpactbroker.url=http://localhost:9292 -Dpactbroker.auth.username=pact-user -Dpactbroker.auth.password=pact-password
mvn -Drevision="$APPLICATION_VERSION" -q -s ./pipeline/settings.xml -pl '!example' clean deploy
APPLICATION_NAME=$(mvn help:evaluate -Dexpression=project.name -q -DforceStdout)
echo "Application name: $APPLICATION_NAME"

# Save environment variables in workspace for later use
echo "$APPLICATION_NAME" > application-name.txt