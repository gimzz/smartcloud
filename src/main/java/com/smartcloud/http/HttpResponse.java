package com.smartcloud.http;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HttpResponse {

    public static ResponseEntity<Map<String, Object>> ok(Object data) {
        return build(HttpStatus.OK, data, "success");
    }

    public static ResponseEntity<Map<String, Object>> created(Object data) {
        return build(HttpStatus.CREATED, data, "success");
    }

    private static ResponseEntity<Map<String, Object>> build(
            HttpStatus status,
            Object data,
            String type
    ) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("type", type);
        body.put("data", data);
        return ResponseEntity.status(status).body(body);
    }

    public static ResponseEntity<Map<String, Object>> noContent() {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", Instant.now().toString());
    body.put("status", HttpStatus.NO_CONTENT.value());
    body.put("type", "success");
    body.put("data", null);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(body);
}

}
