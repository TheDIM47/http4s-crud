#### sbt-native-image

Change following settings in build.sbt
```
nativeImageInstalled := true
nativeImageGraalHome := file("/opt/graalvm-ce-java11-20.3.0").toPath
```
or comment these lines and export GraalVM home directory as GRAALVM_HOME
```
$ export GRAALVM_HOME=/opt/graalvm-ce-java11-20.3.0
```
and then, simple
```shell
sbt nativeImage
```
run:
```shell
target/native-image/http4s-crud
```
or, with config overrides (see application.conf)
```shell
target/native-image/http4s-crud -DSERVER_PORT=9000
```

#### Http4s

You can build a native-image binary as mentioned in the http4s deployment [section] 
(https://github.com/drocsid/http4s/blob/docs/deployment/docs/src/main/tut/deployment.md). 
You will need to follow the directions there to provide GraalVM / native-image plugin 
and provide a muslC bundle. Then populate the UseMuslC path with it's location.

```shell
native-image --static -H:UseMuslC="/path.to/muslC" -H:+ReportExceptionStackTraces -H:+AddAllCharsets \
  --allow-incomplete-classpath --no-fallback --initialize-at-build-time --enable-http --enable-https \
  --enable-all-security-services --verbose -jar "./target/scala-2.13/http4s-crud-assembly-0.0.1-SNAPSHOT.jar" http4s-crudBinaryImage
```
