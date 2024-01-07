# Lookup - A fully AI powered Landmarks Recognition App
![Banner Image](images/app_banner_image.png)
<p align = "center">
<a href="https://www.repostatus.org/#wip"><img src="https://www.repostatus.org/badges/latest/wip.svg" alt="Project Status: WIP – Initial development is in progress, but there has not yet been a stable, usable release    suitable for the public." /></a>
</p>

Lookup is a fully AI powered Android app that lets you explore the world's wonders in a whole new way! Simply take a photo of a famous monument, and the app will
instantly recognize it and generate fascinating descriptions, answer your questions, and even write unique articles with your preferred writing tone about the place. 

## Table of Contents
1. [Demo](#demo)
2. [Screenshots](#screenshots)
3. [Tech Stack](#tech-stack)
4. [Remote API's / Client SDK's](#remote-apis--client-sdks)
5. [Source code, Architecture, & Testing](#source-code-architecture--testing)
6. [Building and running the app](#building-and-running-the-app)

## Demo
https://github.com/technophilist/Lookup/assets/54663474/25ff77dd-4110-4165-b470-c2613e7112eb

## Screenshots
<img src = "images/screenshots/home_screen.png" width = "270" height = "600" /> <img src = "images/screenshots/bottom_sheet.png" width = "270" height = "600" /> <img src = "images/screenshots/bookmarked_locations_screen.png" width = "270" height = "600" /> 

<img src = "images/screenshots/delete_dialog.png" width = "270" height = "600" /> <img src = "images/screenshots/article_variations_drop_down.png" width = "270" height = "600" /> <img src = "images/screenshots/landmark_detail_screen.png" width = "270" height = "600" />

## Tech Stack
- Entirely written in [Kotlin](https://kotlinlang.org/).
- [CameraX](https://developer.android.com/training/camerax) for accessing and processing the images captured from the camera.
- [TensorFlow Lite](https://www.tensorflow.org/lite/inference_with_metadata/task_library/overview) for image recognition.
- [Hilt](https://www.google.com/url?client=internal-element-cse&cx=000521750095050289010:zpcpi1ea4s8&q=https://developer.android.com/training/dependency-injection/hilt-android&sa=U&ved=2ahUKEwiW5omeu6z4AhWRR2wGHVUsCo0QFnoECAMQAQ&usg=AOvVaw3dCbP79C6od3KVCnJub3v0) for dependency injection.
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for UI and navigation.
- [Coil compose](https://coil-kt.github.io/coil/compose/) for image loading and caching.
- [Coil-gif](https://coil-kt.github.io/coil/gifs/) for loading and displaying gif's.
- [Lottie compose](https://github.com/airbnb/lottie/blob/master/android-compose.md) for displaying animations.
- [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines/coroutines-guide.html) for threading.
- [Kotlin Flows](https://developer.android.com/kotlin/flow) for creating reactive streams.
- [Work Manager](https://developer.android.com/topic/libraries/architecture/workmanager?gclid=EAIaIQobChMIwJy33ufG8QIVGcEWBR31Mwa-EAAYASAAEgIF3vD_BwE&gclsrc=aw.ds) for persistent long-running background tasks.
- [Retrofit](https://square.github.io/retrofit/) for communicating with the OpenAI API.
- [Room](https://developer.android.com/training/data-storage/room) for database.
- Moshi + Moshi Kotlin CodeGen for deserializing responses from the API.
- Uses [Kotlin Gradle DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html).


## Remote API's / Client SDK's
- [Gemini Client SDK](https://ai.google.dev/tutorials/android_quickstart) 
- [OpenAI API](https://openai.com/blog/openai-api)
- [Unsplash API](https://unsplash.com/developers)

## Source code, Architecture, & Testing
- Uses multi-repository pattern.
- Commit messages follow the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) specification.
- Consists of unit tests that predominantly test the data layer.

## Building and running the app
1. Create an unsplash developer account and get the api token from https://unsplash.com/documentation.
2. In the `local.properties` file of your project add the api key as shown below.
```properties
UNSPLASH_API_ACCESS_KEY = PASTE-YOUR-TOKEN-HERE
```
3. The app can be made to either use [Gemini](https://ai.google.dev) or [Chat-GPT](https://openai.com/blog/introducing-chatgpt-and-whisper-apis)
for it's contextual text generation features. To use either of the two, make sure to get the access / api key for the chosen LLM service,
and paste it in the `local.properties` file as follows. For the service that you've chosen not to use,
make sure to enter a random string as the access key since the existence of both the keys are required
for the app to build.
```properties
UNSPLASH_API_ACCESS_KEY = PASTE-YOUR-TOKEN-HERE
GOOGLE_GEMINI_API_KEY = PASTE-YOUR-TOKEN-HERE
```
5. By default, the app uses Gemini for text generation. If you want to use Chat-GPT, then replace the '@GeminiClient'
di qualifier with the `@OpenAiClient` qualifier in all places where an instance of `TextGeneratorClient` is
injected.
6. Build the app and run it.
