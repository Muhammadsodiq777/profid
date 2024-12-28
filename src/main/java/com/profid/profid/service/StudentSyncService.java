package com.profid.profid.service;

import com.profid.profid.dto.GeneralResponse;

public interface StudentSyncService {
    // Fetch using GenericWebClient
    GeneralResponse fetchAndSaveUsingWebClient();

    // Fetch using GenericHttpClient
    GeneralResponse fetchAndSaveUsingHttpClient();

    // Post using GenericWebClient
    GeneralResponse postStudentsUsingWebClient(String postUrl);

    // Post using GenericHttpClient
    GeneralResponse postStudentsUsingHttpClient(String postUrl);
}
