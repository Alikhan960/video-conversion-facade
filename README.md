# Video Conversion Facade

Assignment #4 – Facade Pattern.
Topic: **Option A – Media/Video Conversion Facade**. Java 17+, no external dependencies.

## The idea

Converting a video needs several collaborating classes called in a strict order
(read file → pick source codec → decode → fix audio → pick target codec → encode → write file).
`VideoConversionFacade` hides that sequence behind one call:

