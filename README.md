# RadioStations (Test task)

### Task:

- Take a link and go for every nested links which represents categories.
- Display different types of items.
- If you have time - play audio.
- Be good with performance, architecture and use best practices.

As a result i believe i made a "friendly-to-expand" app, and it can get new features much faster, with less pain and more reliability.

### Bottlenecks:

Server.

- Response type, the whole 'logic' and everything is just... not the best.
- The problem is that it requires workarounds, some of them core ones, which impact the whole app.
- I left only one Entity type, removed all separations and made it straightforward/all-on-one/dumb.
- The whole 'position' thing for saving in DB is wierd, but there is a predefined sort for sure and no param for that as well.

### Todo:

- Play music as a service.
- Test everything.
- Add proguard rules in case of release build demonstration.

### Tech stack:

- Kotlin
- Gradle KTS
- Jetpack Compose + Navigation
- Hilt
- Room
- Retrofit
- Moshi
- Coil
- JUnit4
- MockK