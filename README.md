# Currency Converter (Android)

An Android application that fetches live exchange rates from a web service using HTTP requests and converts an amount between currencies.

Built with **Kotlin** in **Android Studio** as part of the experiment:
*"Implement an Android application to access web services using HTTP requests."*

---

## Features

- Convert an amount between 10 popular currencies (USD, EUR, INR, GBP, JPY, AUD, CAD, CHF, CNY, SGD)
- Live exchange rates fetched over HTTP (no API key required)
- Shows the date of the exchange rate used
- Loading indicator while the request is in progress
- Input validation and error handling (empty or invalid amount, no internet, server errors)
- Clean Material UI with a light blue and lavender theme

## Web Service Used

**[Frankfurter API](https://frankfurter.dev)** - a free, open-source currency exchange rate API.

Example request:

```
GET https://api.frankfurter.dev/v1/latest?amount=100&from=USD&to=INR
```

Example JSON response:

```json
{
  "amount": 100.0,
  "base": "USD",
  "date": "2026-11-10",
  "rates": { "INR": 8800.0 }
}
```


## Tech Stack

| Item | Details |
|------|---------|
| Language | Kotlin |
| IDE | Android Studio |
| UI | XML layouts with Material Components |
| Networking | `HttpURLConnection` (no external libraries) |
| JSON parsing | `org.json.JSONObject` |
| Threading | `kotlin.concurrent.thread` + `runOnUiThread` |

## Project Structure

```
app/src/main/
├── AndroidManifest.xml            # INTERNET permission
├── java/<your.package>/
│   └── MainActivity.kt            # HTTP request, JSON parsing, UI logic
└── res/
    ├── drawable/
    │   └── bg_gradient.xml        # Light blue to lavender gradient background
    └── layout/
        └── activity_main.xml      # Screen layout
```

## How It Works

1. The user enters an amount and selects the **From** and **To** currencies.
2. On tapping **Convert**, the app builds the request URL.
3. `httpGet()` sends an **HTTP GET** request on a **background thread** (Android does not allow network calls on the main thread).
4. The JSON response is parsed with `JSONObject` to read the converted rate and the date.
5. The result is posted back to the **main thread** and shown in the UI.
6. If anything fails (no internet, non-200 response, bad data), an error message is shown instead of crashing.

## Setup and Run

### Requirements
- Android Studio (latest stable version recommended)
- An emulator or a physical Android device with internet access

### Steps
1. Create a new project: **New Project → Empty Views Activity**, language **Kotlin**.
2. Copy the files into the project:
   - `activity_main.xml` → `app/src/main/res/layout/`
   - `bg_gradient.xml` → `app/src/main/res/drawable/`
   - `MainActivity.kt` → replace your existing `MainActivity.kt` (keep your own `package` line)
3. Add the internet permission to `AndroidManifest.xml`, **above** the `<application>` tag:
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   ```
4. *(Optional)* To force light mode so text stays readable on the lavender fields, add this before `setContentView(...)` in `MainActivity.kt`:
   ```kotlin
   androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
       androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
   )
   ```
5. Click **Run**, enter an amount (for example `100`), and tap **Convert**.

## Troubleshooting

| Problem | Fix |
|---------|-----|
| `Unable to resolve host` / network error | Check the emulator or device has internet; cold boot the emulator |
| `Unresolved reference: R` | The `package` line in `MainActivity.kt` must match your project's package |
| App crashes on launch | Make sure the project was created with **Empty Views Activity**, not the Compose template |
| `SecurityException: Permission denied (missing INTERNET)` | Add the `<uses-permission>` line outside the `<application>` tag |
| Inflate error for `Widget.Material3.TextInputLayout.OutlinedBox` | Use `Widget.MaterialComponents.TextInputLayout.OutlinedBox` instead |


## Possible Improvements

- Load the full currency list from `https://api.frankfurter.dev/v1/currencies`
- Add a swap-currencies button
- Rewrite networking using Retrofit or Volley and compare with `HttpURLConnection`
- Add dark mode support
- Cache the last fetched rate for offline use

## License

For educational use.
