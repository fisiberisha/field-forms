# Field Forms

Native Android app for the **Lumiform Android Code Challenge**: fetch a hierarchical inspection form from JSON, display it in Compose, cache it offline in Room, persist inspection answers, and handle refresh failures gracefully.

## Features

- **Structured form UI** — **tabbed pages** (one page at a time), nested sections, text questions, image thumbnails (full-screen detail), single- and multi-select choices with **scores shown** when the API provides them
- **Progress** — per-page **“X of Y answered”** for choice questions; **“Last updated …”** after a successful refresh
- **Offline-first** — Room is the source of truth; successful network refresh **replaces** the form cache in a transaction
- **Persisted answers** — choice selections in Room survive process death and form refresh; **selected tab** restored via `SavedStateHandle`
- **Network fallback** — failed refresh keeps cached data; error card stays **fixed above** the list (not inside the scroll)
- **Pull-to-refresh** on the form screen
- **Tests** — unit tests (parsing, mappers, repository mocks, ViewModel) plus **instrumented** Room integration tests

## Tech stack

| Area | Choice |
|------|--------|
| Language & UI | Kotlin, Jetpack Compose, Material 3 |
| Architecture | MVVM (`ui` → `presentation` → `domain` / `data`) |
| Networking | Retrofit, OkHttp, Moshi (polymorphic `"type"` JSON) |
| Async | Coroutines, `Flow`, `StateFlow` |
| Database | Room (normalized schema, DB v2) |
| Images | Coil |
| DI | Hilt |
| Navigation | Navigation Compose |
| Testing | JUnit, kotlinx-coroutines-test, MockK, AndroidJUnit4 + in-memory Room |

## Architecture overview

```
ui/                 Compose screens, navigation, typography
presentation/       FormViewModel, UiState
domain/             Form models, choice selection rules, FormRepository interface
data/
  remote/           Retrofit API, DTOs, Moshi parser
  local/            Room entities, DAO, database
  mapper/           DTO ↔ domain ↔ entities
  repository/       Offline-first FormRepositoryImpl
di/                 Hilt modules (network, database, repository)
```

### Data flow

1. **Refresh** — `FormApiService.getPages()` → map to entities → `FormDao.replaceAll()` inside a Room transaction (choice selections are **not** cleared).
2. **Observe** — `FormDao` exposes `Flow`s for nodes, response sets, options, and choice selections; the repository maps them to domain models.
3. **Answers** — toggles call `FormRepository.updateChoiceSelection()` (serialized read–modify–write).
4. **UI** — `FormViewModel` collects flows, triggers refresh on startup, restores tab index from `SavedStateHandle`; Compose reads `FormUiState`.

### Room schema (relational)

| Table | Purpose |
|-------|---------|
| `form_nodes` | Tree: `parentId`, `sortOrder`, `type`, titles/content/image URL, `responseSetId` for choices |
| `response_sets` | Choice metadata (`multipleSelection`) |
| `response_options` | Options per set (`label`, optional `score`) |
| `choice_selections` | User answers (`questionId`, `responseId` composite key) |

Pages are nodes with `parentId = null`. Child order matches JSON array order via `sortOrder`. DB v2 uses destructive migration on upgrade from v1 (local dev).

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

**Unit tests (JVM, no device):**

```bash
./gradlew testDebugUnitTest
```

Covers JSON parsing, mappers, repository refresh mapping (mock DAO), ViewModel states, choice selection rules, and page progress helpers.

**Instrumented tests (emulator or physical device):**

```bash
./gradlew connectedDebugAndroidTest
```

Uses in-memory Room to verify that a **successful refresh replaces** cached form rows, that a **failed refresh leaves** prior rows, that **choice selections** survive refresh and concurrent updates, and related repository behavior.

## Manual verification checklist

- [ ] First launch loads **Main Page** and **Second Page** via **tabs** (not one long scroll of both)
- [ ] Nested sections and hierarchy typography on Main Page
- [ ] Image thumbnail opens full-screen detail with title; back returns to the form
- [ ] Single-choice and multi-choice work; **tap option label** on multi-choice; **scores** visible on Second Page single-choice options when present
- [ ] Second Page shows **“X of Y answered”** as choices are filled; **“Last updated …”** after a successful refresh
- [ ] **Choice answers** survive any relaunch (Room) — fill choices on Second Page, fully close and reopen the app; selections remain
- [ ] **Tab + last updated** survive **process death** while the task stays in Recents (SavedStateHandle): select Second Page, stop the process (**Run → Stop** in Android Studio, or system kill), reopen from Recents — same tab and timestamp
- [ ] **Not expected:** swipe the app away from Recents, then open from the launcher — tab resets to Main Page and “Last updated” clears; answers still restore from Room
- [ ] After a successful load, airplane mode + relaunch — cached form still visible
- [ ] Failed refresh with cache — error card **above** the list (visible on Second Page without scrolling up) and **Retry**
- [ ] Clear app storage, offline launch — full-screen error and **Retry**
- [ ] Pull-to-refresh while online

## Screenshots (for GitHub)

Add PNGs under `docs/screenshots/` (create the folder), then embed them here for reviewers. Minimum set:

| # | File (suggested) | What to capture |
|---|------------------|-----------------|
| 1 | `01-main-page-tabs.png` | **Main Page** tab, nested sections |
| 2 | `02-second-page-choices.png` | **Second Page** tab, choices, scores, “X of Y answered” |
| 3 | `03-image-detail.png` | Full-screen image detail |
| 4 | `04-offline-banner.png` | Refresh error card **above** list on Second Page with cached content |

Example embed after files exist:

```markdown
![Main Page](docs/screenshots/01-main-page-tabs.png)
![Second Page choices](docs/screenshots/02-second-page-choices.png)
![Image detail](docs/screenshots/03-image-detail.png)
![Offline banner](docs/screenshots/04-offline-banner.png)
```

Check off manual items above once verified; run `./gradlew connectedDebugAndroidTest` on a device/emulator before submit.

## Package

`com.example.fieldforms`

## License

Take-home submission — use as required by the challenge instructions.
