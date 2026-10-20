# Architecture

## Modules

```
:app               presentation/, manager/, ui/, app/ (config, DI)     Android app
  │
  ▼
:core-data         Room, Retrofit APIs, DTOs, repository implementations Android library
  │
  ▼
:core-domain       entities, repository interfaces, use cases, ApiResult Pure Kotlin (JVM)

:assets-processor  KSP processor, build time only
```

- Dependencies only point down. A layer violation (domain importing data, data importing app) is a
  **compile error**, not a review comment.
- `:core-domain` has no Android on its classpath: no `Context`, `Log`, Compose or `org.json`. Keep it
  a JVM module.
- Hilt modules live in `:app` (`app/di/`): wiring is the composition root's job. Classes in the core
  modules only declare `@Inject constructor`.
- Repository pairs: `IXxxRepository` in `:core-domain`, `XxxRepositoryImpl` in `:core-data`, file name
  equals type name, bound in `app/di/RepositoryModule.kt`.

## `:app` layout

```
app/src/main/java/com/example/base/
├── App.kt                    Application (Hilt)
├── app/
│   ├── config/AppConfig.kt   build-time config (read BuildConfig only here)
│   └── di/                   NetworkModule, DatabaseModule, RepositoryModule, NavigatorModule
├── manager/                  app-wide singletons
│   ├── dialog/               DialogManager + DialogHost
│   ├── navigation/           AppRoute, NavigatorManager
│   ├── network/              HttpClientManager (shared OkHttp)
│   ├── storage/              StorageManager (DataStore)
│   └── ClickThrottleManager.kt
├── presentation/
│   ├── MainActivity.kt
│   ├── app/AppScreen.kt      NavHost + DialogHost
│   ├── composable/           shared UI (EmptyState, ApiErrorMessage)
│   ├── todo/                 sample feature
│   └── setting/              theme settings
└── ui/theme/                 Color, Theme, Type, Dimens, ThemeMode
```

## Data flow

```
Screen ──collect──▶ ViewModel.state (StateFlow)
  │ events                │
  ▼                       ▼
ViewModel ──▶ UseCase ──▶ IRepository ──▶ RepositoryImpl ──▶ Room / Retrofit
          └─▶ StorageManager (settings)
          └─▶ NavigatorManager / DialogManager
```

- Settings go `ViewModel → StorageManager` directly; no repository for key-value preferences.
- Use cases hold business rules (validation, ordering). Skip the use case when there is no logic.

## Adding a feature

| Step | Where |
|---|---|
| Entity + repository interface (+ use case) | `:core-domain` |
| DAO/entity or API/DTO + `XxxRepositoryImpl` | `:core-data` |
| `@Binds` / `@Provides` | `app/di/` |
| Route | `manager/navigation/AppRoute.kt` + `presentation/app/AppScreen.kt` |
| `XxxState`, `XxxViewModel`, `XxxScreen`, `composable/` | `presentation/xxx/` |

Remote calls: wrap them in `apiCall { }` inside the repository (map DTOs inside the block), return
`ApiResult<T>`, and show failures with `showApiError(context, error)`.

## Generated assets

`:assets-processor` scans `app/src/main/assets/` and `app/src/main/res/raw*/` on every build:

```kotlin
import com.example.base.generated.Assets
import com.example.base.generated.RawRes

AsyncImage(model = Assets.Images.emptyState, ...)   // "file:///android_asset/images/empty_state.png"
mediaPlayer = MediaPlayer.create(context, RawRes.notifySound)
```

Folders become nested objects, file names become camelCase (`_` prefix if they start with a digit,
numeric suffix on clashes). Both objects always exist, even when the folders are empty.
