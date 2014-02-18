0. You should have the Android SDK installed and download important stuff via SDK Manager. I suggest you use an IDE like Eclipse (Android Studio will work too, but I didn't try)

1. Clone this repository to your local harddrive: `git clone https://github.com/cubei/FlappyCow.git`

2. Open your IDE (with a new workspace)

3. `Import Android Code` -> choose the cloned folder "FlappyCow"

4. Get rid of the the ad errors. Choose option a or b.

* a) The ads need the Google Play Services. You can set up a library project [like this](https://developers.google.com/mobile-ads-sdk/docs/). Android Studio should have this already built in.
* b) Remove the ads: Go into the `AndroidManifest.xml` and delete the ad related stuff. They should be marked as an error. Now you will see only remaining errors in the `Game.java` file. Delete the faulty lines in *setLayouts()*, *onResume()* and the ad imports.

Enjoy the code.

Suggestions:
* Not familiar with java programming:
** Change images to create "Flappy Fish" or other themes. If you keep the image sizes the same, you won't need to change any code.
* Java programmer
** Follow Justin Bieber on Twitter or do what ever you want.