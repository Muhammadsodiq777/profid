package com.profid.profid.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.profid.profid.dto.GeneralResponse;
import com.profid.profid.dto.response.StudentResponseDTO;
import com.profid.profid.entity.Student;
import com.profid.profid.entity.Subject;
import com.profid.profid.repository.StudentRepository;
import com.profid.profid.utils.v1.GenericWebClient;
import com.profid.profid.utils.v2.GenericHttpClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.*;

@Service
public class StudentSyncServiceImpl implements StudentSyncService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentSyncServiceImpl.class);

    private final StudentRepository studentRepository;
    private final GenericWebClient genericWebClient;
    private final GenericHttpClient genericHttpClient;

    public StudentSyncServiceImpl(StudentRepository studentRepository, GenericWebClient genericWebClient, GenericHttpClient genericHttpClient) {
        this.studentRepository = studentRepository;
        this.genericWebClient = genericWebClient;
        this.genericHttpClient = genericHttpClient;
    }

    // Fetch using GenericWebClient
    @Override
    public GeneralResponse fetchAndSaveUsingWebClient() {
        String url = "https://example.org/students";
        try {
            List<StudentResponseDTO> students = genericWebClient
                    .get(url, getAuthHeadersAsMap(), new ParameterizedTypeReference<List<StudentResponseDTO>>() {
                    }, 10)
                    .block();

            if (students != null) {
                saveStudentsToDatabase(students);
            }

            return new GeneralResponse("Data fetched and saved using WebClient", "SUCCESS");
        } catch (Exception e) {
            return new GeneralResponse("Error fetching data using WebClient: " + e.getMessage(), "FAILURE");
        }
    }


    // Fetch using GenericHttpClient
    @Override
    public GeneralResponse fetchAndSaveUsingHttpClient() {
        String url = "https://example.org/students";
        try {
            List<StudentResponseDTO> students = genericHttpClient
                    .get(url, getAuthHeadersAsMap(), new TypeReference<>() {
                    }, 10);

            if (students != null) {
                saveStudentsToDatabase(students);
            }

            return new GeneralResponse("Data fetched and saved using HttpClient", "SUCCESS");
        } catch (IOException e) {
            return new GeneralResponse("Error fetching data using HttpClient: " + e.getMessage(), "FAILURE");
        }
    }


    // Post using GenericWebClient
    @Override
    public GeneralResponse postStudentsUsingWebClient(String postUrl) {
        if (postUrl == null || postUrl.isEmpty()) {
            return new GeneralResponse("Post URL is null or empty", "FAILURE");
        }

        try {
            List<Student> students = studentRepository.findAll();

            if (students.isEmpty()) {
                return new GeneralResponse("No students found in the database", "FAILURE");
            }

            Flux.fromIterable(students)
                    .buffer(10)
                    .flatMap(batch -> genericWebClient.post(postUrl, getAuthHeadersAsMap(), batch, GeneralResponse.class, 10))
                    .blockLast();

            return new GeneralResponse("Data successfully posted using WebClient", "SUCCESS");
        } catch (Exception e) {
            LOGGER.error("Error posting data using WebClient: {}", e.getMessage(), e);
            return new GeneralResponse("Error posting data using WebClient: " + e.getMessage(), "FAILURE");
        }
    }

    // Post using GenericHttpClient
    @Override
    public GeneralResponse postStudentsUsingHttpClient(String postUrl) {
        if (postUrl == null || postUrl.isEmpty()) {
            return new GeneralResponse("Post URL is null or empty", "FAILURE");
        }

        try {
            List<Student> students = studentRepository.findAll();

            if (students.isEmpty()) {
                return new GeneralResponse("No students found in the database", "FAILURE");
            }

            List<List<Student>> batches = partitionList(students, 10);
            for (List<Student> batch : batches) {
                genericHttpClient.post(postUrl, getAuthHeadersAsMap(), batch, GeneralResponse.class, 10);
            }

            return new GeneralResponse("Data successfully posted using HttpClient", "SUCCESS");
        } catch (IOException e) {
            LOGGER.error("Error posting data using HttpClient: {}", e.getMessage(), e);
            return new GeneralResponse("Error posting data using HttpClient: " + e.getMessage(), "FAILURE");
        }
    }


    private void saveStudentsToDatabase(List<StudentResponseDTO> students) {
        for (StudentResponseDTO studentDTO : students) {
            Student student = new Student();
            student.setName(studentDTO.getName());
            student.setEmail(studentDTO.getEmail());
            student.setAge(studentDTO.getAge());
            student.setGender(studentDTO.getGender());
            student.setPhoneNumber(studentDTO.getPhoneNumber());
            student.setAddress(studentDTO.getAddress());

            List<Subject> subjects = studentDTO.getSubjects().stream().map(subjectDTO -> {
                Subject subject = new Subject();
                subject.setName(subjectDTO.getName());
                subject.setCredits(subjectDTO.getCredits());
                subject.setDescription(subjectDTO.getDescription());
                subject.setStudent(student);
                return subject;
            }).toList();

            student.setSubjects(subjects);
            studentRepository.save(student);
        }
    }

    private Map<String, String> getAuthHeadersAsMap() {
        String auth = "admin:pass1234";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + encodedAuth);
        return headers;
    }

    private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            partitions.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return partitions;
    }

}
