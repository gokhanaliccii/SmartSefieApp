## Selfie App

The goal of this exercise is for us to see how you build a simple app from scratch, how you architecture your code and how you prioritise your work in such a limited period of time.

The app is a simple selfie taking app, where users must be able to take a selfie and show the result.

### Required points:

The app should have 2 different views/screens

On the 1st view/screen you should have the following components:

1. Front camera preview
2. Button to take a picture

On the 2nd view/screen you should have the following components:

1. Result of the taken selfie
2. Button (X) to dismiss the result and get back to the camera

### Bonus points:

1. Back navigation handling (Result -> Camera Preview; Camera preview -> Exit)
2. Face detection: Can we get our selfie automatically taken once a clear face is detected on the screen?
For this, we recommend either [MLKit](https://firebase.google.com/docs/ml-kit/android/detect-faces) or [FaceDetector](https://github.com/RedApparat/FaceDetector)


We recommend you to use [Fotoapparat](https://github.com/RedApparat/Fotoapparat) as a 3rd party camera wrapper, to avoid all the unnecessary boilerplate code of setting up the camera.
This dependency is already part of your `app/build.gradle`
Feel free to use any 3rd party libraries (or replace our suggestions) to help you do the job - a good engineer always has a good set of tools. However, keep in mind that we will challenge the need of all the included dependencies.
Apart from having our code working in a structured and efficient way, we also care about UX. Don't be afraid of adding animations or transitions wherever they improve the UX of your app

For the delivery of your solution, you can either e-mail us a zip of your solution or the link to a repository containing it.

Good luck and we are looking forward to see your selfie app!