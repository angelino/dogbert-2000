FROM clojure:temurin-21-lein AS builder
RUN mkdir -p /usr/src/dogbert-2000
WORKDIR /usr/src/dogbert-2000
COPY project.clj /usr/src/dogbert-2000
RUN --mount=type=cache,target=/root/.m2 lein deps
COPY . /usr/src/dogbert-2000
RUN --mount=type=cache,target=/root/.m2 mv "$(lein ring uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" dogbert-2000-standalone.jar

FROM eclipse-temurin:21-alpine
RUN mkdir -p /usr/lib/dogbert-2000
WORKDIR /usr/lib/dogbert-2000
COPY --from=builder /usr/src/dogbert-2000/dogbert-2000-standalone.jar /usr/lib/dogbert-2000/dogbert-2000-standalone.jar
CMD ["java", "-jar", "dogbert-2000-standalone.jar"]
