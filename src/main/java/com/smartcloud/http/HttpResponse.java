package com.smartcloud.http;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HttpResponse {

    public static ResponseEntity<Map<String,Object>> ok(Object data) {
        return buildResponse(HttpStatus.OK, data, "success");
    }

    public static ResponseEntity<Map<String,Object>> created(Object data) {
        return buildResponse(HttpStatus.CREATED, data, "success");
    }

    private static ResponseEntity<Map<String,Object>> buildResponse(HttpStatus status, Object data, String type) {
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("type", type);
        body.put("data", data);
        return ResponseEntity.status(status).body(body);
    }
}
