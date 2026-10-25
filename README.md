# Field Forms

Native Android app for the **Lumiform Android Code Challenge**: fetch a hierarchical inspection form from JSON, display it in Compose, cache it offline in Room, and handle refresh failures gracefully.

## Features

- **Structured form UI** — pages, nested sections, text questions, image thumbnails (full-screen detail), single- and multi-select choices
- **Offline-first** — Room is the source of truth; successful network refresh replaces the cache in a transaction
- **Network fallback** — failed refresh keeps cached data visible with an inline error card and retry
- **Tests** — JSON parsing, mappers, repository, ViewModel, and choice selection logic

## Tech stack

| Area | Choice |
|------|--------|
| Language & UI | Kotlin, Jetpack Compose, Material 3 |
| Architecture | MVVM (`ui` → `presentation` → `domain` / `data`) |
| Networking | Retrofit, OkHttp, Moshi (polymorphic `"type"` JSON) |
| Async | Coroutines, `Flow`, `StateFlow` |
| Database | Room (normalized schema) |
| Images | Coil |
| DI | Hilt |
| Navigation | Navigation Compose |
| Testing | JUnit, kotlinx-coroutines-test, MockK |

## Architecture overview

```
ui/                 Compose screens, navigation, typography
presentation/       FormViewModel, UiState, choice selection rules
domain/             Form models, FormRepository interface
data/
  remote/           Retrofit API, DTOs, Moshi parser
  local/            Room entities, DAO, database
  mapper/           DTO ↔ domain ↔ entities
  repository/       Offline-first FormRepositoryImpl
di/                 Hilt modules (network, database, repository)
```

### Data flow

1. **Refresh** — `FormApiService.getPages()` → map to entities → `FormDao.replaceAll()` inside a Room transaction.
2. **Observe** — `FormDao` exposes `Flow`s for nodes, response sets, and options; the repository combines them into `Flow<List<FormPage>>`.
3. **UI** — `FormViewModel` collects the flow and triggers refresh on startup; Compose reads `FormUiState`.

### Room schema (relational)

| Table | Purpose |
|-------|---------|
| `form_nodes` | Tree: `parentId`, `sortOrder`, `type`, titles/content/image URL, `responseSetId` for choices |
| `response_sets` | Choice metadata (`multipleSelection`) |
| `response_options` | Options per set (`label`, optional `score`) |

Pages are nodes with `parentId = null`. Child order matches JSON array order via `sortOrder`.

### API

Simulated endpoint (Gist):

`https://gist.githubusercontent.com/aruana-lumiform/383e1291df3e0cc1d49aeb14d45f5b94/raw/lumiform-android-test.json`

## Setup

**Requirements:** Android Studio (recent), JDK 17+ (Embedded JBR is fine), Android SDK 37, min SDK 24.

1. Clone the repository and open the project in Android Studio.
2. Sync Gradle.
3. Run the **app** configuration on an emulator or device with internet for the first load.

### Command line

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"  # macOS example

./gradlew assembleDebug
./gradlew installDebug
```

## Tests

```bash
./gradlew testDebugUnitTest
```

Coverage includes polymorphic JSON parsing, DTO/entity mappers, repository refresh behavior, ViewModel states (success, cache, errors), and choice selection rules.

## Manual verification checklist

- [ ] First launch loads pages, sections, and text with hierarchy typography
- [ ] Image thumbnail opens full-screen detail with title; back returns to the form
- [ ] Single-choice question allows one selection; multi-choice allows several
- [ ] After a successful load, enable airplane mode and relaunch — cached form still visible
- [ ] With cache present, failed refresh shows the error card at the top and **Retry**
- [ ] Clear app storage, offline launch shows full-screen error and **Retry**

## Screenshots (for GitHub)

Suggested captures for the README or PR:

1. Main form with both pages visible (scroll)
2. Nested sections (e.g. Subsection 1.1)
3. Image detail screen
4. Choice questions on Second Page
5. Offline banner with cached content

## Package

`com.example.fieldforms`

## License

Take-home submission — use as required by the challenge instructions.
