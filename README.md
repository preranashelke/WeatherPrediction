# Weather Prediction

A Spring Boot microservice that returns three days of city forecast highs/lows, forecast windows, and weather advice.

## Run locally

```bash
./mvnw spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080). Set `OPENWEATHER_API_KEY` in the environment to enable live data.

## API

`GET /api/weather?city={city}&offline={true|false}` returns a JSON forecast. Every day includes a date, Celsius high/low, prediction messages, and time window.

Swagger UI is available at `/swagger-ui/index.html`; machine-readable schema is at `/v3/api-docs`. The checked-in contract is [openapi.yaml](openapi.yaml).

| Status | Meaning |
| --- | --- |
| 200 | Forecast returned from live provider or offline fallback |
| 400 | Missing, blank, or overlong city parameter |
| 503 | Forecast and fallback are both unavailable |


## Design and quality

- **Ports and Strategy:** `WeatherProvider` selects online and local data sources; `FallbackWeatherProvider` applies graceful fallback.
- **Dependency inversion:** `WeatherService` consumes provider and rule abstractions.
- **SOLID:** single-purpose providers/rules and extension through new rule implementations.
- **12 Factor:** configuration via environment, stateless request handling, logs to standard output, and packaged executable artifact.
- **Performance:** one bounded upstream request for all forecast windows, a reusable HTTP client, and aggregation in memory.
- **Offline resilience:** bundled JSON works without network access or an API key.

## Build, test, deploy

```bash
./mvnw clean verify
docker build -t weather-prediction:local .
docker run --rm -p 8080:8080 -e OPENWEATHER_API_KEY="$OPENWEATHER_API_KEY" weather-prediction:local
```

`Jenkinsfile` contains build/test, image build, and local run stages. Configure Jenkins tools named `jdk17` and `maven`; store the API key as a masked Jenkins credential named `OPENWEATHER_API_KEY`. The Jenkins deployment stage maps the container to host port 8081 so it can run alongside Jenkins' default port 8080.

`mvn verify` runs the Spring context smoke test and rule tests for exact threshold boundaries and each advice message.

## Request flow

The draw.io diagram is in [docs/weather-flow.drawio](docs/weather-flow.drawio).
