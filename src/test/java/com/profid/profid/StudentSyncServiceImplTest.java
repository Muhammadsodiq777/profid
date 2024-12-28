package com.profid.profid;

import com.fasterxml.jackson.core.type.TypeReference;
import com.profid.profid.dto.GeneralResponse;
import com.profid.profid.dto.response.StudentResponseDTO;
import com.profid.profid.dto.response.SubjectResponseDTO;
import com.profid.profid.entity.Student;
import com.profid.profid.entity.Subject;
import com.profid.profid.repository.StudentRepository;
import com.profid.profid.service.StudentSyncServiceImpl;
import com.profid.profid.utils.v1.GenericWebClient;
import com.profid.profid.utils.v2.GenericHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentSyncServiceImplTest {

    @InjectMocks
    private StudentSyncServiceImpl studentSyncService;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private GenericWebClient genericWebClient;

    @Mock
    private GenericHttpClient genericHttpClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fetchAndSaveUsingWebClient_Success() {
        String url = "https://example.org/students";
        List<StudentResponseDTO> mockStudents = List.of(
                new StudentResponseDTO("John", "john@example.com", 20, "Male", "1234567890", "Address 1", List.of(
                        new SubjectResponseDTO("Math", 3, "Mathematics")
                ))
        );

        when(genericWebClient.get(eq(url), anyMap(), any(ParameterizedTypeReference.class), eq(10)))
                .thenReturn(Mono.just(mockStudents));

        GeneralResponse response = studentSyncService.fetchAndSaveUsingWebClient();

        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("Data fetched and saved using WebClient", response.getMessage());
        verify(studentRepository, times(1)).save(any(Student.class));
    }


    @Test
    void fetchAndSaveUsingWebClient_Error() {
        String url = "https://example.org/students";

        when(genericWebClient.get(eq(url), anyMap(), any(ParameterizedTypeReference.class), eq(10)))
                .thenThrow(new RuntimeException("External API error"));

        GeneralResponse response = studentSyncService.fetchAndSaveUsingWebClient();

        assertNotNull(response);
        assertEquals("FAILURE", response.getStatus());
        assertTrue(response.getMessage().contains("Error fetching data using WebClient"));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void fetchAndSaveUsingHttpClient_Success() throws IOException {
        String url = "https://example.org/students";
        List<StudentResponseDTO> mockStudents = List.of(
                new StudentResponseDTO("Jane", "jane@example.com", 22, "Female", "9876543210", "Address 2", List.of(
                        new SubjectResponseDTO("Science", 4, "Physics")
                ))
        );

        when(genericHttpClient.get(eq(url), anyMap(), any(TypeReference.class), eq(10)))
                .thenReturn(mockStudents);

        GeneralResponse response = studentSyncService.fetchAndSaveUsingHttpClient();

        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("Data fetched and saved using HttpClient", response.getMessage());
        verify(studentRepository, times(1)).save(any(Student.class));
    }


    @Test
    void fetchAndSaveUsingHttpClient_Error() throws IOException {
        String url = "https://example.org/students";

        when(genericHttpClient.get(eq(url), anyMap(), any(TypeReference.class), eq(10)))
                .thenThrow(new IOException("External API error"));

        GeneralResponse response = studentSyncService.fetchAndSaveUsingHttpClient();

        assertNotNull(response);
        assertEquals("FAILURE", response.getStatus());
        assertTrue(response.getMessage().contains("Error fetching data using HttpClient"));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void postStudentsUsingWebClient_Success() {
        String postUrl = "https://example.org/students";
        List<Student> mockStudents = List.of(
                new Student("John", "john@example.com", 20, "Male", "1234567890", "Address 1", new ArrayList<>())
        );

        when(studentRepository.findAll()).thenReturn(mockStudents);
        when(genericWebClient.post(eq(postUrl), anyMap(), anyList(), eq(GeneralResponse.class), eq(10)))
                .thenReturn(Mono.just(new GeneralResponse("Data successfully posted using WebClient", "SUCCESS")));

        GeneralResponse response = studentSyncService.postStudentsUsingWebClient(postUrl);

        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("Data successfully posted using WebClient", response.getMessage());
        verify(studentRepository, times(1)).findAll();
    }


    @Test
    void postStudentsUsingHttpClient_Success() throws IOException {
        String postUrl = "https://example.org/students";
        List<Student> mockStudents = List.of(
                new Student("Jane", "jane@example.com", 22, "Female", "9876543210", "Address 2", new ArrayList<>())
        );

        when(studentRepository.findAll()).thenReturn(mockStudents);
        when(genericHttpClient.post(eq(postUrl), anyMap(), anyList(), eq(GeneralResponse.class), eq(10)))
                .thenReturn(new GeneralResponse("Data successfully posted using HttpClient", "SUCCESS"));

        GeneralResponse response = studentSyncService.postStudentsUsingHttpClient(postUrl);

        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("Data successfully posted using HttpClient", response.getMessage());
        verify(studentRepository, times(1)).findAll();
    }


    @Test
    void postStudentsUsingHttpClient_NoStudents() throws IOException {
        String postUrl = "https://example.org/students";
        when(studentRepository.findAll()).thenReturn(Collections.emptyList());

        GeneralResponse response = studentSyncService.postStudentsUsingHttpClient(postUrl);

        assertNotNull(response);
        assertEquals("FAILURE", response.getStatus());
        assertEquals("No students found in the database", response.getMessage());
        verify(studentRepository, times(1)).findAll();
        verify(genericHttpClient, never()).post(anyString(), anyMap(), anyList(), eq(GeneralResponse.class), eq(10));
    }
}
