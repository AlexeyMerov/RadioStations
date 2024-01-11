<div align="center">

# Radio Stations

[![GitHub release](https://img.shields.io/github/v/release/AlexeyMerov/RadioStations)](https://github.com/AlexeyMerov/RadioStations/releases)
![Static Badge](https://img.shields.io/badge/26-blue?logo=android&label=minSdk)
[![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/AlexeyMerov/RadioStations/android.yml?event=push&logo=github&logoColor=white)](https://github.com/AlexeyMerov/RadioStations/actions)
[![License](https://img.shields.io/github/license/AlexeyMerov/RadioStations?color=blue)](LICENSE)

### Sandbox to test my skills and learn new ones

<br>
<p>
  <img src="./preview/1.png" width="19%" /> 
  <img src="./preview/2.png" width="19%" />
  <img src="./preview/3.png" width="19%" /> 
  <img src="./preview/10.gif" width="19%" /> 
  <img src="./preview/4.png" width="19%" /> 
</p>

<br>
<p>
  <img src="./preview/5.png" width="19%" /> 
  <img src="./preview/6.png" width="19%" />
  <img src="./preview/7.png" width="19%" /> 
  <img src="./preview/8.png" width="19%" /> 
  <img src="./preview/9.png" width="19%" /> 
</p>

</div>

### About:

- Portrait, landscape, tablet support.
- Display list of categories or radio stations.
- Different item types.
- Offline mode.
- Favorites (locally).
- Play/control audio as a service.
- Profile picture from gallery or camera.
- Settings for the app.
- Dark/Light theme.
- Design guideline (Material3).
- Animations.
- Static and dynamic shortcuts.
- Performance, architecture, best practices.
- Tests... not 100% coverage.
- Firebase crashlytics, analytics, perfomance.

### Tech stack:

- Multi-module
- Clean Architecture
- Kotlin + KTS
- Coroutines + Flow
- Retrofit + Moshi
- Jetpack Compose + Navigation
- Hilt
- Room + Paging (locally)
- Coil
- Lottie
- ExoPlayer
- JUnit4 + MockK

### Providers:

- Stations: https://opml.radiotime.com/
- Countries: https://restcountries.com/
- Flag images: https://flagpedia.net/ or https://flagcdn.com/

### Bottlenecks:

- Server not the best. Requires some core workarounds, which impact the whole app.
- As an example:
    - single, all-on-one _CategoryEntity_ for categoies and everything. Ideally should be separate.
    - _position_ field for saving in DB. There is a predefined sort from server but no query/response param for that.