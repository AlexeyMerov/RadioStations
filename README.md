# RadioStations

# Test task

Originally this challenge was designed to take a few hours to complete.  
I spent a few days :) I hope 'they' expected a much simpler solution.  
~ 4-6 hours for all initial preparations.  
~ 10 for everything else  
Maybe more, but i didn't count so precisely.

**Task:**  
-- Take a link and go for every nested links which represents categories.  
-- Display different types of items.  
-- If you have time - play audio. But again, time...  
-- Be good with performance, architecture and use best practices.
  
---

As a result i believe i make a "friendly-to-expand" app, and it can get new features much faster, with less pain and more reliability.

**Bottlenecks:**  
-- Only one, tbh.  
Server. Response type, the whole 'logic' and everything is just... not the best.  
The problem is that it requires many workarounds, some of them core ones, which impact the whole app.

Edit: And the whole 'position' thing for saving in DB is wierd, but there is a predefined sort for sure and no param for that as well.

Edit 2: And server making mess again, so i left only one Entity type and remove all separations and made it dumb/all-on-one and straightforward.

**Todo:**  
-- Play music.  
-- Test everything.  
-- Add proguard rules in case of release build demonstration.

**Tech stack:**  
-- Kotlin  
-- Jetpack Navigation  
-- Hilt  
-- Room  
-- Retrofit  
-- Moshi  
-- Glide