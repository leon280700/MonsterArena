#!/usr/bin/env bash
# Baut das Spiel und startet es direkt im aktuellen Terminal.
set -euo pipefail
cd "$(dirname "$0")"
./gradlew -q installDist
exec app/build/install/monsterarena/bin/monsterarena "$@"
