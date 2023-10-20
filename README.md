# RadioStations (Sandbox)

### About:

- Display list of categories and go for every nested links which represents new categories or radio stations.
- Different types of items.
- Play audio.
- Be good with performance, architecture and use best practices.
- Animations and up-to-date design guideline.
- Tests... after taking a deep breath.

### Bottlenecks:

Server.

- Response type, the whole 'logic' and everything... not the best.
- The problem is that it requires some core workarounds, which impact the whole app.
- I left only one Entity type and made it all-on-one/dumb.
- The whole 'position' thing for saving in DB is wierd. There is a predefined sort from server but no response param for that.

### Tech stack:

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