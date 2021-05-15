# Andriod-Gesture-Recognizer
 An andriod app for recognizing gestures, user can draw a gesture and get the top three matches from the gestures in library. The gestures in the library can be added, edited, deleted or renamed.
 

# Andriod-Gesture-Recognizer
 An andriod app for recognizing gestures, user can draw a gesture and get the top three matches from the gestures in library. The gestures in the library can be added, edited, deleted or renamed.
 
 
# Install
```sh
You will need to install the following:  
* [OpenJDK 11.0.10] or later version.  (https://www.oracle.com/java/technologies/javase-downloads.html)
* [Gradle 6.8.1] or later version. (https://gradle.org/install/)
* [Android SDK] (https://developer.android.com/studio/releases/platform-tools)
* [Intellij IDEA or Android Studio] Install AVD of Pixel 3
```
# Usage
Open this project with Intellij IDEA or Android Studio and deploy on the Pixel 3 AVD.

# How it works
*	The gesture drawn by user are sampled uniformly into points and stored. 
*	The recognition is realized by calculating the mean squared error of the sequence of points.
*	No gesture related libraries are used.

# Demo
## Adding Gesture
Instruction: 1. Go to the addition screen by choosing addition in the nevigation bar at the bottom.
2. Draw the gesture you want to add.
3. Click clear to reset the screen; click done and enter the name, then confirm to add the new gesture to the library.
<p>
<img src="https://github.com/DaveHJT/Andriod-Gesture-Recognizer/blob/main/demo/addition.gif?raw=true" width="300">
</p>

## Gesture Library
Instruction: 
Gesture library contained all the gestures that you added before. You can click on the gesture to edit, rename or delete the gesture. 
Here is a demo of edit, rename and delete operation.
<p>
<img src="https://github.com/DaveHJT/Andriod-Gesture-Recognizer/blob/main/demo/library.gif?raw=true" width="300">
</p>

## Recognition
Instruction: In the recognition screen, draw a gesture you previously added to the library, and the top three matches will appear at the bottom of the screen. 
PS: You can change the scale and direction of the gesture, or even draw the gesture in a reversed way (end point becomes the start point), and it will still be recognized. But remember the mirrored gesture is not the same gesture as the original one.
<p>
<img src="https://github.com/DaveHJT/Andriod-Gesture-Recognizer/blob/main/demo/recognition.gif?raw=true" width="300">
</p>

## License
None







 
