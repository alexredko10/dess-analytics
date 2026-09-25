package com.telematika.dessanalytics.analytics.api.errors;

public class NoTelemetryDataException extends RuntimeException {
  public NoTelemetryDataException(String message) {
    super(message);
  }
}
