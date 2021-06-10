# OpenTelemetry Annotation Demo

To run the demo, switch to this directory and run(Windows machine, Haven't tried in MAC or Linux):

```shell
docker compose up -d
```

The demo exposes the following backends:

- Jaeger at http://localhost:16686
- Zipkin at http://localhost:9411
- Prometheus at http://localhost:9090
- Kibana at http://localhost:5601

To clean up any docker container from the demo run `docker compose down` from 
this directory.
