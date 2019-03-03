## Selfie App

Simple selfie application which capture images automatically when face
detected. For face detection firebase ml-kit used. During the project
development Kotlin used

### Idea:
The app is a simple selfie taking app, where users must be able to take
a selfie and show the result. And take photo after face detected

#### CameraView Lib
Fotoapprat camera dependency wrapped in camera_view module. And with
this approach we can able to change camera dependency without modify app
layer.
[OnfidoCameraView](https://github.com/gokhanaliccii/SmartSefieApp/blob/develop/camera_view/src/main/java/com/onfido/camera/OnfidoCameraView.kt)

#### FaceDetection
FirebaseMl kit used to detect face from frames.
[FaceDetector](https://github.com/gokhanaliccii/SmartSefieApp/blob/develop/camera_view/src/main/java/com/onfido/camera/facedetection/FaceDetector.kt)
created in camera_view module. In app layer I implemented face detection
functionality with
[FirebaseFaceDetector](https://github.com/gokhanaliccii/SmartSefieApp/blob/develop/app/src/main/java/com/onfido/android/techtask/facedetector/FirebaseFaceDetector.kt)
If you would rotate your device face detection would work better
(**experience**)


### Need To Improve
* UX & better app design
* FaceDetection performance