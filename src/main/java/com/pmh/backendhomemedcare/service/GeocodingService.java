package com.pmh.backendhomemedcare.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pmh.backendhomemedcare.exception.ExternalServiceException;
import com.pmh.backendhomemedcare.exception.GeocodingNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GeocodingService {

    @Value("${app.nominatim.user-agent}")
    private String userAgent;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public double[] getCoordinates(String address) throws ExternalServiceException {
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://nominatim.openstreetmap.org/search?q=" +
                                address.replace(" ", "+") + "&format=json&limit=1"))
                        .header("User-Agent", userAgent)
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                JsonNode array = mapper.readTree(response.body());

                if (array.isEmpty()) throw new ExternalServiceException("Endereço não encontrado: " + address);

                double lat = array.get(0).get("lat").asDouble();
                double lon = array.get(0).get("lon").asDouble();
                return new double[]{lat, lon};

            } catch (IOException | InterruptedException e) {
                if (attempt == maxAttempts)
                    throw new ExternalServiceException("Erro ao processar geocoding: " + e.getMessage(), e);
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
        }
        throw new ExternalServiceException("Erro inesperado ao processar geocoding.");
    }
}

