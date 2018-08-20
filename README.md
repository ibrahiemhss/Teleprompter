# Teleprompter

Teleprompter, as in classic teleprompter, allows the users to easily read any text file by displaying it as a scrolling script, in front of a camera or a group of people...etc.
In order to help the user achieve a good presentation, this app ensures the flexibility of saving any file the user wants from and to the device or the cloud. 
There are many options the user can freely choose from like resizing fonts ,changing colors, using the app in landscape mode and adjusting the script scrolling speed the way it suits them.



# Documentation
Check out _Capstone_Stage1.pdf_ for Project Requirement Document / specification document that outlines a technical design and implementation plan for the app.

# Pre-requisites
- Java programming language
- IDE Android Studio version 3.1
- Android SDK v28
- Android Build Tools v28.0.1
- Android Support Repository 
   * appcompat-v7:28.0.0-rc01
   * support-media-compat:28.0.0-rc01
   * support-v4:28.0.0-rc01
   * espresso-core:3.0.2
   * google play-services:15.0.1
- Other libraries 
   * Butterknife v 8.8.1
   * Roundedimageview v 2.3.0
   * Materialedittext v 2.1.4
- Gradle version 3.1.4


# Features
- App theme extends AppCompat.
- Use an CoordinatorLayout with AppBarLayout , CollapsingToolbarLayout and Toolbar standard and simple transitions between activities.
- Leverages third-party libraries such as ButterKnife, CircleImageView and Materialedittext.
- Google service integrations such as Admob, Interstital Ads, Analytics.
- Google Drive API Key to connect the app with Google Drive APIs.
- Build and deploy using the installRelease Gradle task.
- Implements a ContentProvider to access locally stored data.
- Implements Async Loader to get Data from ContentProvider to Display


# Getting started
This sample uses the Gradle build system. To build this project, use the "gradlew build" command or use "Import Project" in Android Studio.
You can simply 
- "Import Project" in Android Studio 
- add this project to your firebase account and download  google-services.json and add it to TeleprompterApp file inside teleprompter project files
-  Get the Google Drive API key from https://console.developers.google.com/home/dashboard?pli=1 



