# Teleprompter

Simple teleprompter for Android based on the sample code from the Android SDK following Android fundamentals and project specifications.


this project in Udacity course in the Android Developer Nanodegree.

# Documentation
Check out _Capstone_Stage1.pdf_ for Project Requirement Document / specification document that outlines a technical design and implementation plan for the app.

# Pre-requisites
- Android SDK v28
- Android Build Tools v28.0.1
- Android Support Repository 
   *appcompat-v7:28.0.0-alpha3
   *support-media-compat:28.0.0-alpha3
   *support-v4:28.0.0-alpha3
   *espresso-core:3.0.2
- Other Support Repository 
   *butterknife:8.8.1
   *lottie:2.5.5

# Getting started
This sample uses the Gradle build system. To build this project, use the "gradlew build" command or use "Import Project" in Android Studio.
You can simply 
- "Import Project" in Android Studio 
- add this project to your firbase acount and download  google-services.json and add this to TeleprompterApp file inside teleprompter project files
# Features
- App theme extends AppCompat.
- Use an app bar and associated toolbars, standard and simple transitions between activities.
- Leverage third-party libraries such as ButterKnife, CircleImageView, Gson, etc
- Google service integrations such as Admob, Interstital Ads, Analytics.
- Home screen widget listing file details.
- Build and deploy using the installRelease Gradle task.
- Signing configuration, and the keystore and passwords are included in the repository. Keystore is referred to by a relative path.
- Implement a ContentProvider to access locally stored data.
- Async Task implementation to perform on-demand requests (download file).
- Use a Loader to move data to its views.
- Support for accessibility - Navigation using DPad, necessary content descriptions, etc.
- Support  RTL layout switching on all layouts.


# License

Copyright 2016 The Android Open Source Project, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
