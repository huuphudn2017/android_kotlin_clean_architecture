#!/usr/bin/env bash
# Renames the base package to your project's package. Run once, right after copying the template.
#
#   tools/rename-package.sh com.acme.notes "Acme Notes"
#
# Updates package declarations, source folders of every module, namespace/applicationId,
# the KSP service file, rootProject.name and app_name.
set -euo pipefail

OLD_PKG="com.example.base"
NEW_PKG="${1:?Usage: $0 <new.package.name> [\"App Name\"]}"
APP_NAME="${2:-}"

if ! [[ "$NEW_PKG" =~ ^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$ ]]; then
  echo "Invalid package name: $NEW_PKG" >&2
  exit 1
fi

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

OLD_PATH="${OLD_PKG//.//}"
NEW_PATH="${NEW_PKG//.//}"

# 1. Move source folders (every source set of every module).
find . -type d -path "*/$OLD_PATH" -not -path "*/build/*" -not -path "./.gradle/*" | while read -r dir; do
  src_root="${dir%/$OLD_PATH}"
  tmp="$(mktemp -d)"
  cp -R "$dir/." "$tmp/"
  rm -rf "$dir"
  find "$src_root" -type d -empty -delete 2>/dev/null || true
  mkdir -p "$src_root/$NEW_PATH"
  cp -R "$tmp/." "$src_root/$NEW_PATH/"
  rm -rf "$tmp"
done

# 2. Replace the package string in sources and config.
{
  grep -rl --exclude-dir=build --exclude-dir=.gradle --exclude-dir=.git \
    --include='*.kt' --include='*.kts' --include='*.xml' --include='*.md' --include='*.pro' \
    "$OLD_PKG" . || true
  find . -path '*/META-INF/services/*' -not -path '*/build/*' -type f
} | sort -u | while read -r f; do
  sed -i.bak "s/${OLD_PKG//./\\.}/$NEW_PKG/g" "$f" && rm -f "$f.bak"
done

# 3. App and project name.
if [[ -n "$APP_NAME" ]]; then
  strings="app/src/main/res/values/strings.xml"
  sed -i.bak "s|\(<string name=\"app_name\"[^>]*>\)[^<]*|\1$APP_NAME|" "$strings" && rm -f "$strings.bak"
  project_name="$(echo "$APP_NAME" | tr -cd '[:alnum:]')"
  sed -i.bak "s/rootProject.name = \".*\"/rootProject.name = \"$project_name\"/" settings.gradle.kts \
    && rm -f settings.gradle.kts.bak
fi

echo "Renamed $OLD_PKG -> $NEW_PKG"
echo "Verify: ./gradlew clean test assembleDebug"
