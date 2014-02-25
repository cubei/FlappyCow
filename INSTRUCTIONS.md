0. You should have the Android SDK installed and download important stuff via SDK Manager. I suggest you use an IDE like Eclipse (Android Studio will work too, but I didn't try)

1. Clone this repository to your local harddrive: `git clone https://github.com/cubei/FlappyCow.git`

2. Open your IDE (with a new workspace)

3. `Import Android Code` -> choose the cloned folder "FlappyCow"

4. This app contains ads via Samsung Admob with should be functional with the included jar file.
The adds via Google Admob require the Google Play Services. Since Version 1.95 the Google Play Services are also needed for leaderboard and achievement.
To get this running in eclipse you'll need 2 additional library projects: Google-Play-Services and BaseGameUtil
(Android Studio should have the Google Play Services already built in)
  * a) Go in the cloned repository folder `./FlappyCow/lib_projects` and unpack both zip archive files
  * b) In your IDE (eclipse) import both projects like done before.
  * c) Make sure in both project `is library` under `properties/Android`  is checked
  * d) In the `BaseGameUtil` project add `google-play-services_lib` as a library in `properties/android`
  * e) In the `FlappyCow` project add `BaseGameUtil` as a library in `properties/android`

5. Enjoy the code.

---

Suggestions:

* Not familiar with java programming:
  * Change images to create "Flappy Fish" or other themes. If you keep the image sizes the same, you won't need to change any code.

* Java programmer
  * Follow Justin Bieber on Twitter or do what ever you want.
