# Video Conversion Facade

Assignment #4 – Facade Pattern. Topic: Option A – Media/Video Conversion Facade. Java 17+.

## Idea
Converting a video needs several classes called in a strict order
(read file → pick source codec → decode → fix audio → pick target codec → encode → write file).
`VideoConversionFacade` hides that sequence behind one call:

```java
Path ogg = new VideoConversionFacade().convert("samples/lecture.mp4", "ogg");
```

## Structure
```
src/main/java/kz/astanait/videofacade/
├── client/      Main                  – demo client, uses only the facades
├── facade/      VideoConversionFacade – main facade
│                VideoInfoFacade       – second facade (describe a file)
│                ConversionException   – the one exception clients see
└── subsystem/   CodecFactory, Codec, Mpeg4Codec, OggCodec, WebmCodec,
                 BitrateReader, AudioMixer, VideoFileStorage
src/test/java/…  JUnit 4 tests
docs/uml.png     UML class diagram
samples/         demo input files
```

## Run
- IntelliJ: open as Maven project, run `Main`; run tests from `src/test/java`.
- Maven: `mvn test`

Supported formats: mp4, ogg, webm.
