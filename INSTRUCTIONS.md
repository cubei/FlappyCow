Quick starting guide:
https://youtu.be/5Yymo-ZMrRQ

--

0. You need: Java SDK, Android SDK installed and download important stuff via SDK Manager (like newest Android API). I suggest you use an IDE like Eclipse with ADT Plugin (Android Studio might work too, but I didn't try)

1. Clone this repository to your local harddrive: `git clone https://github.com/cubei/FlappyCow.git`

2. Open your IDE (with a new workspace)

3. `Import Android Code` -> choose the cloned folder "FlappyCow"

4. This app contains ads via [Samsung Admob](http://www.samsungadhub.com/) which should be functional with the included jar file.
The adds via [Google Admob](https://www.google.de/ads/admob/) require the [Google Play Services](http://developer.android.com/google/play-services/index.html). Since version 1.95 the Google Play Services are also needed for leaderboard and achievements.
To get this running in Eclipse you'll need 2 additional library projects: *Google-Play-Services* and *BaseGameUtil*
(Android Studio should have the Google Play Services already built in)
  * a) Go in the cloned repository folder `./FlappyCow/lib_projects` and unpack both zip archive files
  * b) Import  both projects in your IDE (Eclipse) like done before.
  * c) Make sure in both projects `is library` under `properties/Android`  is checked
  * d) In the `BaseGameUtil` project add `google-play-services_lib` as a library in `properties/android`
  * e) In the `FlappyCow` project add `BaseGameUtil` as a library in `properties/android`

5. Enjoy the code.

---

This game features the Google Play Services with a leaderboard and achievements. Those will not work out of the box for you. Because the ids for that features (res/values/ids.xml) come from my google developer account and will only work with the apk signed by me. (You will get an error when trying to sign in)

So you need to add your own game to the play services in the [Google Play developer
console](https://play.google.com/apps/publish/) or remove this feature from the game. The easiest way to do so, would be to remove the sign in button.

---

Suggestions:

* Not familiar with Java programming:
  * Change images to create "Flappy Fish" or other themes. If you keep the image sizes the same, you won't need to change any code.

* Java programmer
  * Follow [Justin Bieber on Twitter](https://twitter.com/justinbieber) or do whatever you want.
