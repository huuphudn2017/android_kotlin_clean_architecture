# Build

```bash
./gradlew assembleDebug          # applicationId suffix .debug, dev API URL
./gradlew assembleRelease        # R8 + resource shrinking, prod API URL
./gradlew test                   # JVM unit tests, no device needed
./gradlew lintDebug
```

- Versions live in `gradle/libs.versions.toml` only. Upgrade them together: AGP, Kotlin, KSP and
  the Compose compiler plugin must stay compatible. AGP is capped by Android Studio: a newer AGP
  than the IDE knows fails sync with "incompatible version of the Android Gradle plugin" even though
  the command line still builds. Upgrade Studio first.
- AGP 9 compiles Kotlin itself (built-in Kotlin): do not apply `org.jetbrains.kotlin.android`, and
  there is no `kotlinOptions` block — the JVM target follows `compileOptions`.
- The configuration cache is on (`gradle.properties`). Build logic must not read project state at
  execution time; prefer declared task inputs (see the KSP inputs in `app/build.gradle.kts`).
- Per-environment values are `buildConfigField`s in `app/build.gradle.kts`, read through `AppConfig`.
- Release signing reads `app/key.properties` (not committed):

  ```properties
  storeFile=/path/to/release.jks
  storePassword=...
  keyAlias=...
  keyPassword=...
  ```

  Without it, release builds are signed with the debug key (fine for testing, not for publishing).
- Room schemas are exported to `core-data/schemas/` (Room Gradle plugin, `room { schemaDirectory }`). Commit them; bump the database version and add a
  migration for every schema change.
- Keep `kotlin.daemon.jvmargs` in `gradle.properties`; a small Kotlin daemon heap fails large Compose
  files with a misleading "Exception during IR lowering".
- R8 is on for release. Add `-keep` rules in `app/proguard-rules.pro` for classes read by reflection
  (Gson DTOs live under `data/**/dto/`).

## Git

- Branch from `master`: `feature/<name>/<task-id>/<summary>`, `hotfix/<name>/<task-id>/<summary>`.
- Large binaries (`*.mp4`, `*.mp3`, `*.aar`, `*.so`) go through Git LFS: `git lfs track "*.ext"` and
  commit `.gitattributes` before the files.
