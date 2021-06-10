# OpenTelemetry Annotation Demo

### Pre-requisites

- Docker
- JAVA 8
- Maven

To run the demo, switch to the docker directory and run(Windows machine, Haven't tried in MAC or Linux):

```shell
docker compose up -d
```

The demo exposes the following backends:

- Jaeger at http://localhost:16686
- Zipkin at http://localhost:9411
- Prometheus at http://localhost:9090
- Kibana at http://localhost:5601

### Run JAVA app to export the traces using Annotations

To run java application, navigate to root of the project and open command prompt to run command:

```shell
mvn package
```

The target folder will be generated upon succesfull build. Run the jar file:

```shell
java -jar opentelemetry-annotations-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

Traces should be exported to Zipkin, Jaeger and Elastic (Kibana)..

To clean up any docker container from the demo run `docker compose down` from 
this directory.