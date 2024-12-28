package com.profid.profid.controller;

import com.profid.profid.dto.GeneralResponse;
import com.profid.profid.service.StudentSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Student Synchronization", description = "Endpoints for synchronizing and posting student data")
public class StudentSyncController {

    private final StudentSyncService studentSyncService;

    public StudentSyncController(StudentSyncService studentSyncService) {
        this.studentSyncService = studentSyncService;
    }

    @Operation(
            summary = "Fetch and save students using WebClient",
            description = "Fetches student data from an external API using WebClient and saves it into the database."
    )
    @GetMapping("/fetch/webclient")
    public ResponseEntity<GeneralResponse> fetchAndSaveUsingWebClient() {
        GeneralResponse response = studentSyncService.fetchAndSaveUsingWebClient();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Fetch and save students using HttpClient",
            description = "Fetches student data from an external API using HttpClient and saves it into the database."
    )
    @GetMapping("/fetch/httpclient")
    public ResponseEntity<GeneralResponse> fetchAndSaveUsingHttpClient() {
        GeneralResponse response = studentSyncService.fetchAndSaveUsingHttpClient();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Post student data using WebClient",
            description = "Posts student data from the database to a specified URL using WebClient."
    )
    @PostMapping("/post/webclient")
    public ResponseEntity<GeneralResponse> postStudentsUsingWebClient(
            @Parameter(description = "The URL where student data will be posted", required = true)
            @RequestParam String postUrl) {
        GeneralResponse response = studentSyncService.postStudentsUsingWebClient(postUrl);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Post student data using HttpClient",
            description = "Posts student data from the database to a specified URL using HttpClient."
    )
    @PostMapping("/post/httpclient")
    public ResponseEntity<GeneralResponse> postStudentsUsingHttpClient(
            @Parameter(description = "The URL where student data will be posted", required = true)
            @RequestParam String postUrl) {
        GeneralResponse response = studentSyncService.postStudentsUsingHttpClient(postUrl);
        return ResponseEntity.ok(response);
    }
}