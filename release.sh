#!/bin/bash

# Get the current version from gradle.properties
version=$(grep -oP 'version=\K.*(?=-SNAPSHOT)' gradle.properties)

# Remove the "-SNAPSHOT" suffix from gradle.properties
sed -i 's/^\(version=\).*-SNAPSHOT$/\1'"$version"'/' gradle.properties

# Create a git tag for the version and push changes
git add gradle.properties
git commit -m "release: $version"
git tag "$version" -m "Release version $version"
git push origin "$version"

# Fetch tags and build the project
git fetch --tags origin
./gradlew clean build

# Create a GitHub release
gh release create "$version" --latest --verify-tag --generate-notes --title "$version" jars/mServerLinks-*-*.jar

# Publish the release artifact
./gradlew publish -PforceSign=true # Maven
./gradlew publishMods # Modrinth
./gradlew publishAllPublicationsToHangar syncAllPagesToHangar # Hangar

# Increment the version and add the "-SNAPSHOT" suffix to gradle.properties
new_version=$(echo "$version" | awk -F. '{$NF++;print}' | sed 's/ /./g')-SNAPSHOT
sed -i 's/^\(version=\).*$/\1'"$new_version"'/' gradle.properties

# Commit the change to gradle.properties and push
git add gradle.properties
git commit -m "snapshot: $new_version"
git push
