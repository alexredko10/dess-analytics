package com.telematika.dessanalytics.analytics.api.errors;

public class ApiExceptionHandler extends RuntimeException {
  public ApiExceptionHandler(String message) {
    super(message);
  }
}
