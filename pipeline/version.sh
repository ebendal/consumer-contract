#!/bin/bash
set -e -o pipefail

# Output v1.0.0 (tag on current commit) OR v1.0.0-1-gbdf13d2 (tag on previous commit)
DESCRIBE_OUTPUT=$(git describe --always)

echo "Git describe output: $DESCRIBE_OUTPUT"

if [[ ! $DESCRIBE_OUTPUT =~ ^v[0-9]+.*$ ]]; then
  echo "Error: cannot handle Git describe output"
  echo "Make sure to tag the git repository with an initial version number"
  echo "Do not use annotated tags for anything else than version numbers"
  exit 1
fi

# Parse describe output
IFS='-' read -r -a DESCRIBE_OUTPUT_ARRAY <<<"$DESCRIBE_OUTPUT"

# Get the tag name part of the output and remove the leading v
VERSION=${DESCRIBE_OUTPUT_ARRAY[0]:1}

if [ "${#DESCRIBE_OUTPUT_ARRAY[@]}" -gt 1 ]; then
  echo "No tag on current commit"
  CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
  echo "Current branch: $CURRENT_BRANCH"
  IFS='.'
  read -r -a SEMANTIC_VERSION <<<"$VERSION"
  if [[ (-n $(git branch --list "v${SEMANTIC_VERSION[0]}.x") || -n $(git branch --list "v${SEMANTIC_VERSION[0]}.x.x")) && ($CURRENT_BRANCH == "master" || $CURRENT_BRANCH == "main") ]]; then
    echo "Applying major version increment"
    SEMANTIC_VERSION[0]=$((SEMANTIC_VERSION[0] + 1))
    SEMANTIC_VERSION[1]=0
    if [ ${#SEMANTIC_VERSION[@]} -gt 2 ]; then
      SEMANTIC_VERSION[2]=0
    fi
  elif [[ ${#SEMANTIC_VERSION[@]} -gt 2 && -n $(git branch --list "v${SEMANTIC_VERSION[0]}.${SEMANTIC_VERSION[1]}.x") && ($CURRENT_BRANCH == "master" || $CURRENT_BRANCH == "main" || $CURRENT_BRANCH == "v${SEMANTIC_VERSION[0]}.x.x") ]]; then
    echo "Applying minor version increment"
    SEMANTIC_VERSION[1]=$((SEMANTIC_VERSION[1] + 1))
    SEMANTIC_VERSION[2]=0
  else
    echo "Applying patch version increment"
    SEMANTIC_VERSION[${#SEMANTIC_VERSION[@]} - 1]=$((${SEMANTIC_VERSION[${#SEMANTIC_VERSION[@]} - 1]} + 1))
  fi
  VERSION="${SEMANTIC_VERSION[*]}"
  if [[ $CURRENT_BRANCH == "master" || $CURRENT_BRANCH == "main" || $CURRENT_BRANCH == "v${SEMANTIC_VERSION[0]}.x" || $CURRENT_BRANCH == "v${SEMANTIC_VERSION[0]}.x.x" || $CURRENT_BRANCH == "v${SEMANTIC_VERSION[0]}.${SEMANTIC_VERSION[1]}.x" ]]; then
    echo "Current branch is a versioned branch (either main/master or a version .x branch)"
    TAG_NAME="v$VERSION"
    git tag -a "$TAG_NAME" -m ""
    echo "Tag created: $TAG_NAME"
    if [[ $1 == "--push" ]]; then
      git push origin "$TAG_NAME"
      echo "Tag pushed: $TAG_NAME"
    fi
  else
    VERSION="$VERSION-SNAPSHOT"
  fi
  unset IFS
fi
echo "Version: $VERSION"

# Save environment variables in workspace for later use
echo "$VERSION" >application-version.txt
