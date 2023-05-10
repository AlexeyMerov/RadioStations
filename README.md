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

**Todo:**  
-- Play music.  
-- Remove some hardcode or move it.  
-- Improve DI if possible.
-- Test everything.  
-- In case it will have paging, redo to paging3.  
-- Add proguard rules in case of release build demonstration.

**Tech stack:**  
-- Kotlin  
-- Jetpack Navigation  
-- Hilt  
-- Room  
-- Retrofit  
-- Moshi  
-- Glide