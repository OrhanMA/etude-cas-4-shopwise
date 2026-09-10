package com.shopwise.app.exception;

import java.time.Instant;
import java.util.Map;

public class ApiError {
  private final int status;
  private final String message;
  private final Instant timestamp;
  private final Map<String, String> details;

  public ApiError(int status, String message) {
    this(status, message, null);
  }

  public ApiError(int status, String message, Map<String, String> details) {
    this.status = status;
    this.message = message;
    this.details = details;
    this.timestamp = Instant.now();
  }

  public int getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public Map<String, String> getDetails() {
    return details;
  }
}
