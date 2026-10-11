#!/usr/bin/env bash
# Baut das Spiel und startet es direkt im aktuellen Terminal.
# (./gradlew run reicht kein echtes TTY durch – Lanterna würde dann ein Swing-Fenster öffnen.)
set -euo pipefail
cd "$(dirname "$0")"
./gradlew -q installDist
exec app/build/install/tuigame/bin/tuigame "$@"
