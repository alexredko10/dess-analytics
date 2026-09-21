# DESS Analytics

Backend service for energy analytics, historical aggregation, telemetry processing, and operational insights for the DESS Monitor ecosystem.

The project is built with Java 21 and Spring Boot and is intended to evolve from a small analytics service into a production component that can serve dashboards, external clients, and AI-assisted analysis.

## Purpose

`dess-analytics` is responsible for transforming raw telemetry into useful analytical data.

The service is expected to consume historical and live data produced by the existing DESS Monitor system and expose stable REST APIs for clients such as:

* Angular dashboard;
* internal monitoring tools;
* external integrations;
* future AI / virtual assistant integrations;
* reporting and anomaly-detection services.

The service does not control hardware and does not execute inverter or device commands.

Operational control and safety remain the responsibility of the existing DESS Monitor runtime.

## High-Level Architecture

```text
                         ┌────────────────────┐
                         │    Angular UI      │
                         └─────────┬──────────┘
                                   │
                                   │ REST
                                   ▼
                         ┌────────────────────┐
                         │   DESS Analytics   │
                         │   Java / Spring    │
                         └──────┬───────┬─────┘
                                │       │
                     historical │       │ live state
                                │       │
                                ▼       ▼
                         TimescaleDB   DESS Monitor
                                         Python
```

The main architectural rule is simple:

```text
DESS Monitor
    owns collection, hardware communication, automation and safety.

DESS Analytics
    owns aggregation, statistics, historical analysis and analytical APIs.
```

## Current Development Goal

The first production-oriented milestone is an energy summary API.

Example:

```http
GET /api/v1/analytics/summary
```

Expected response:

```json
{
  "pvGeneratedKwh": 7.84,
  "loadConsumedKwh": 9.21,
  "minBatterySoc": 48.0,
  "maxBatterySoc": 96.0,
  "averageBatterySoc": 71.4
}
```

This summary will eventually be used by the web dashboard to populate energy statistics and historical views.

The first implementation is intentionally based on in-memory telemetry so that domain logic can be developed and tested independently from PostgreSQL, TimescaleDB and external services.

## Domain Model

The analytics domain is based on time-series telemetry.

Example telemetry sample:

```java
public record InverterSample(
        Instant timestamp,
        Double pvPowerW,
        Double totalLoadW,
        Double batterySoc
) {
}
```

A sequence of samples can then be used to calculate:

* generated PV energy;
* consumed load energy;
* peak power;
* battery SOC statistics;
* energy usage by time period;
* telemetry gaps;
* hourly and daily profiles;
* device-level statistics;
* anomalies.

An important requirement is that energy calculations must use actual time intervals between samples instead of assuming a fixed sampling period.

## Project Structure

```text
src/main/java/com/example/

├── DessAnalyticsApplication.java
│
├── analytics/
│   ├── api/
│   ├── domain/
│   ├── repository/
│   └── service/
│
└── training/
    └── orders/
        ├── api/
        ├── domain/
        └── service/
```

Tests follow the same package structure:

```text
src/test/java/com/example/

├── analytics/
│
└── training/
    └── orders/
```

## Analytics Module

The `analytics` package contains production-oriented functionality.

Responsibilities will include:

```text
domain
    Core analytics models and calculation results.

service
    Business logic and time-series calculations.

repository
    Access to telemetry and historical data.

api
    REST endpoints and API contracts.
```

The domain and service layers should remain as independent from Spring and persistence details as reasonably possible.

## Training Module

The project also contains:

```text
com.telematika.dessanalytics.training.orders
```

This package is intentionally kept as a separate interview-training area.

It contains exercises around:

* `List`;
* `Map` and `HashMap`;
* `Set`;
* arrays;
* `Comparator`;
* aggregation;
* sliding-window algorithms;
* algorithmic complexity;
* Spring controller tests.

The training package is not part of the DESS analytics domain and should not be used by production analytics code.

## Planned Data Sources

The service is expected to use two primary sources.

### TimescaleDB

Historical telemetry and time-series analytics.

Typical operations:

```text
SELECT
aggregation
time_bucket
GROUP BY
window functions
historical range queries
```

Spring JDBC / `JdbcTemplate` will generally be preferred for these workloads because the data is naturally query-oriented rather than entity-oriented.

The analytics service should use read-only database credentials when accessing telemetry owned by DESS Monitor.

### DESS Monitor REST API

The existing Python service can provide current runtime state that does not need to be reconstructed from historical telemetry.

This may include:

```text
current inverter state
current device state
runtime status
live measurements
```

## REST API Direction

The public analytics API will evolve around resources such as:

```text
GET /api/v1/analytics/summary

GET /api/v1/analytics/pv

GET /api/v1/analytics/battery

GET /api/v1/analytics/devices

GET /api/v1/analytics/load

GET /api/v1/analytics/data-quality
```

Business calculations should be performed on the backend.

Clients should generally receive calculated values rather than reimplement domain calculations independently.

For example, the frontend should receive:

```json
{
  "selfSufficiencyPercent": 76.9
}
```

instead of reproducing the self-sufficiency formula in JavaScript.

## Development Roadmap

Development is intentionally incremental.

### Phase 1 — Core Java Analytics

Implement the domain model and calculations using in-memory telemetry.

Focus areas:

* collections;
* time-series processing;
* aggregation;
* sorting;
* sliding windows;
* null and boundary handling;
* unit testing.

### Phase 2 — Spring Boot API

Expose analytics through REST endpoints.

Introduce:

* controllers;
* DTOs;
* validation;
* exception handling;
* controller tests.

### Phase 3 — TimescaleDB

Replace the in-memory repository with a real telemetry repository.

Introduce:

* PostgreSQL;
* TimescaleDB;
* Spring JDBC;
* SQL aggregation;
* integration tests;
* Testcontainers.

### Phase 4 — Production Deployment

Package and deploy the service.

Introduce:

* Docker;
* Kubernetes;
* configuration and secrets;
* health checks;
* readiness checks;
* observability.

### Phase 5 — Angular Dashboard

Build a dedicated analytics frontend consuming the Java REST API.

Possible areas:

* overview dashboard;
* PV production;
* battery history;
* load profiles;
* device statistics;
* historical comparisons;
* anomalies.

### Phase 6 — Advanced Analytics

Extend the service with:

* anomaly detection;
* missing telemetry detection;
* consumption spikes;
* device runtime statistics;
* day-over-day comparisons;
* forecast versus actual production;
* long-term trends.

### Phase 7 — AI Assistant

A future virtual assistant may use the analytics service as a deterministic data source.

Instead of giving a language model direct unrestricted database access, the service can expose explicit analytical tools such as:

```text
getEnergySummary(...)
getBatteryHistory(...)
getTopConsumers(...)
comparePeriods(...)
findAnomalies(...)
getDataQuality(...)
```

A model hosted through a platform such as Amazon Bedrock could then use those APIs to answer higher-level questions while calculations remain inside the analytics service.

Example:

```text
"Why was grid consumption higher yesterday?"
```

The model should request structured analytical data from the Java service and explain the result rather than calculate directly from raw telemetry.

## Running Tests

Run all tests:

```bash
mvn test
```

or:

```bash
./mvnw test
```

Tests can also be executed directly from the IDE.

## Engineering Principles

The project follows several principles:

1. Keep hardware control separate from analytics.
2. Prefer explicit domain models over unstructured data.
3. Keep analytical calculations on the backend.
4. Keep domain logic testable without Spring.
5. Treat telemetry as time-series data rather than ordinary CRUD entities.
6. Make database access read-only where the analytics service does not own the data.
7. Add infrastructure only when the domain requires it.
8. Prefer small, independently testable changes.
9. Define analytical metrics precisely before implementing them.
10. Preserve deterministic calculations outside of future AI components.

## Technology Stack

Current and planned technologies:

```text
Java 21
Spring Boot
JUnit 5
AssertJ
Spring MVC
Spring JDBC
PostgreSQL
TimescaleDB
Testcontainers
Docker
Kubernetes
Angular
```

Future integrations may include AWS services and model platforms such as Amazon Bedrock.

## Status

The project is currently in the early domain-development stage.

The existing order analytics package is retained as an isolated training module.

Active development is focused on the real DESS analytics domain, beginning with inverter telemetry and energy summary calculations.
