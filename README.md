# Video Conversion Facade

Assignment #4 – Facade Pattern (Software Design Patterns, Astana IT University).
Topic: **Option A – Media/Video Conversion Facade**. Java 17+, no external dependencies.

## The idea

Converting a video needs several collaborating classes called in a strict order
(read file → pick source codec → decode → fix audio → pick target codec → encode → write file).
`VideoConversionFacade` hides that sequence behind one call:

```java
Path ogg = new VideoConversionFacade().convert("samples/lecture.mp4", "ogg");
```

## Structure

```
src/main/java/kz/astanait/videofacade/
├── client/      Main                      – demo client, uses ONLY the facades
├── facade/      VideoConversionFacade     – main facade: convert(...)
│                VideoInfoFacade           – second facade: describe(...)
│                ConversionException       – the single exception clients see
└── subsystem/   CodecFactory, Codec, AbstractHeaderCodec,
                 Mpeg4Codec, OggCodec, WebmCodec,
                 BitrateReader, AudioMixer, VideoFileStorage (+ 2 subsystem exceptions)
src/test/java/…/FacadeTests.java           – plain-Java tests
docs/uml.dot, docs/uml.png                 – UML class diagram
samples/                                   – input files for the demo
```

Subsystem classes never reference the facades, and can be used directly (see the last test).

## Build & run

```bash
mkdir -p out
javac -d out $(find src -name '*.java')
java -cp out kz.astanait.videofacade.client.Main      # demo
java -cp out kz.astanait.videofacade.FacadeTests      # tests
```

In IntelliJ IDEA: open the folder, mark `src/main/java` and `src/test/java` as source roots, run `Main`.

## Supported formats

`mp4`, `ogg`, `webm` (the codecs are simplified stand-ins: a format header plus a reversible payload transform).
