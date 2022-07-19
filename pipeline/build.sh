#!/bin/bash
set -e -o pipefail -o nounset

APPLICATION_VERSION=$(cat application-version.txt)
echo "Application version: $APPLICATION_VERSION"

mvn -Drevision="$APPLICATION_VERSION" -q -s ./pipeline/settings.xml -pl -example clean deploy
APPLICATION_NAME=$(mvn help:evaluate -Dexpression=project.name -q -DforceStdout)
echo "Application name: $APPLICATION_NAME"

# Save environment variables in workspace for later use
echo "$APPLICATION_NAME" > application-name.txt