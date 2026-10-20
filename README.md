# Base

Android starter project: Kotlin, Jetpack Compose, Material 3, Hilt, Room, Retrofit, DataStore.
Three Gradle modules with layering enforced by the compiler, a KSP generator for assets, and a
small Todo app as the reference feature.

## Start a new project

```bash
cp -R base ~/projects/my-app && cd ~/projects/my-app
git init
tools/rename-package.sh com.company.myapp "My App"
./gradlew clean test assembleDebug
```

Then set the API URLs in `app/build.gradle.kts` (`API_BASE_URL`) and replace the Todo sample
with your features.

## Requirements

| | |
|---|---|
| Android Studio | 2026.1+ |
| JDK | 17+ |
| Kotlin / KSP | 2.4.21 / 2.3.12 (KSP2) |
| Gradle / AGP | 9.8.2 / 9.3.0 (built-in Kotlin) |
| Min / Target / Compile SDK | 24 / 37 / 37 |

## What's included

| Area | |
|---|---|
| Architecture | `:core-domain` (pure Kotlin) ← `:core-data` ← `:app` |
| UI | Compose + Material 3, light/dark/system theme, optional dynamic color, Material icons |
| Navigation | `AppRoute` + `NavigatorManager` (injectable) |
| Data | Room (`AppDatabase`), DataStore (`StorageManager`), Retrofit + shared OkHttp |
| Errors | `ApiResult` / `ApiError` (sealed) + `apiCall {}` + `toUserMessage()` |
| Dialogs | `DialogManager` (callable from ViewModels) + `DialogHost` |
| Codegen | `:assets-processor` → `generated.Assets` / `generated.RawRes` |
| Sample | Todo list: filter, add/edit, swipe to delete with undo, clear completed |
| Tests | JVM unit tests in `:core-domain`, `:core-data`, `:assets-processor` |

Docs: `docs/` (start with `CLAUDE.md`).
