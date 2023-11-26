# RadioStations (Sandbox)

## Preview

<p>
  <img src="./preview/categories.png" width="19%" /> 
  <img src="./preview/stations.png" width="19%" />
  <img src="./preview/favorites.png" width="19%" /> 
  <img src="./preview/player.png" width="19%" /> 
  <img src="./preview/theme.png" width="19%" /> 
</p>

## About:

- Display list of categories and go for every nested links which represents new categories or radio stations.
- Favorites (locally).
- Settings for the app.
- Play/control audio as a service.
- Different item types.
- Performance, architecture and use best practices.
- Offline mode.
- Design guideline (Material3).
- Animations.
- Dark/Light theme.
- Tests... not 100% coverage.

## Tech stack:

- Multi-module
- Clean Architecture
- Kotlin + KTS
- Coroutines + Flow
- Retrofit + Moshi
- Jetpack Compose + Navigation
- Hilt
- Room
- Coil
- Lottie
- ExoPlayer
- JUnit4 + MockK
-

### Bottlenecks:

Server.

- Response type, the whole 'logic' and everything... not the best.
- The problem is that it requires some core workarounds, which impact the whole app.
- I left only one Entity type and made it all-on-one/dumb.
- The whole 'position' thing for saving in DB is wierd. There is a predefined sort from server but no response param for that.