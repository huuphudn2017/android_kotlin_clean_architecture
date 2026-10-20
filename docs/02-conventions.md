# Conventions

## State and coroutines

- ViewModels expose one `StateFlow<XxxState>`; screens use `collectAsStateWithLifecycle()`. No LiveData.
- Derive state with `combine(...).stateIn(viewModelScope, WhileSubscribed(5_000), initial)` when it
  comes from flows; use `MutableStateFlow.update {}` for local UI state.
- One-off events (snackbar, toast) go through a `Channel` exposed as a `Flow`, not through state.
- Do not add `Dispatchers.IO` to `viewModelScope.launch` without a blocking call to justify it. Room,
  DataStore and Retrofit `suspend` functions already run off the main thread.
- Manager scopes use `CoroutineScope(SupervisorJob() + Dispatchers.IO)`; without `SupervisorJob` one
  failure cancels every later `launch`.

## Naming

- `XxxScreen.kt`, `XxxViewModel.kt`, `XxxState.kt`; feature-only composables in
  `presentation/xxx/composable/`, shared ones in `presentation/composable/`.
- Do not create new `object` singletons for logic that can be injected; singletons are for app-wide
  managers only.
- ViewModels never reference other ViewModels.

## Navigation

- Declare routes in `AppRoute`. Routes with arguments get a `create(...)` builder; never build route
  strings at call sites.
- ViewModels navigate through the injected `NavigatorManager`.

## UI

- Material 3 components and `MaterialTheme.colorScheme` / `typography` only. No hardcoded colors in
  composables — add brand colors to `ui/theme/Color.kt`.
- Spacing from `Spacing` (`ui/theme/Dimens.kt`).
- Icons from `androidx.compose.material.icons` (`Icons.Filled.*`, `Icons.AutoMirrored.*` for
  directional icons). Add `material-icons-extended` only if you need icons outside the core set.
- Global dialogs: `DialogManager.showMessage/showConfirm` (destructive actions pass
  `isDestructive = true`). Screen-owned dialogs (forms) are plain composables driven by state.
- Wrap actions that must not run twice in `ClickThrottleManager.throttle("key") { ... }`.
- Every user-visible string lives in `res/values/strings.xml` with a `values-vi` translation.

## Theme

- `ThemeMode` (SYSTEM / LIGHT / DARK) and dynamic color are stored in `StorageManager` and applied in
  `MainActivity`. Read dark mode with `themeMode.isDark()`, not `isSystemInDarkTheme()`.
- The window background (`values/colors.xml`, `values-night/colors.xml`) should match `background` in
  `Color.kt` to avoid a flash at startup.

## Errors

- `ApiError.message` is for logs. User text comes from `ApiError.toUserMessage(context)`; its `when` is
  exhaustive, so a new error variant fails compilation until it is handled.
- `apiCall` rethrows `CancellationException`; keep that order if you edit it (`ApiCallTest` pins it).

## Comments

- English, short, and only where the code cannot say it: intent, constraints, non-obvious trade-offs.
