GitHub Explorer (Android)

A simple GitHub repository search app built with Java, MVVM, Room, and Retrofit, featuring pagination, debounced search, and Firebase Analytics + Crashlytics.

🚀 Features

🔍 Real-time GitHub search (starts typing → fetch)

💾 Offline caching via Room DB

📄 Numbered pagination bar

🧭 Material 3 dark theme UI

⚡ Debounced search (no button needed)

📊 Firebase Analytics & Crashlytics integrated



🧩 Tech Stack

Language: Java

Architecture: MVVM + LiveData

Network: Retrofit2, OkHttp

Database: Room

UI: Material 3, RecyclerView

Crash & Analytics: Firebase



⚙️ Setup

Clone the project and open in Android Studio.

Add your google-services.json in the /app folder.

Update dependencies:

implementation "com.squareup.retrofit2:retrofit:2.11.0"
implementation "androidx.room:room-runtime:2.6.1"
implementation "com.google.firebase:firebase-analytics"
implementation "com.google.firebase:firebase-crashlytics"


Run on device or emulator (Internet required).



📱 How It Works

Start typing → search auto-fetches results.

Clear the field → default list (“android”) returns.

Tap repo → view details → “Open in GitHub”.

Pagination chips at bottom for page navigation.



🧠 Firebase Events

search: logged when user searches.

paginate: logged when user switches page.

Crashes auto-reported via Crashlytics.



🛠️ License

For demo/educational use only.

