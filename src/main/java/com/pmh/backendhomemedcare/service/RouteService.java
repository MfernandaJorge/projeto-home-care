package com.pmh.backendhomemedcare.service;

import com.pmh.backendhomemedcare.exception.ExternalServiceException;
import com.pmh.backendhomemedcare.exception.RouteNotFoundException;
import com.pmh.backendhomemedcare.model.dto.RouteResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RouteService {

    @Value("${ors.api.key}")
    private String orsApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Consulta o ORS para calcular distância e duração entre dois pontos.
     *
     * @param startLat latitude de origem
     * @param startLon longitude de origem
     * @param endLat   latitude de destino
     * @param endLon   longitude de destino
     * @return objeto RouteResult com distância (m) e duração (s)
     * @throws RouteNotFoundException    se a rota não for encontrada
     * @throws ExternalServiceException  em caso de falha HTTP ou parsing JSON
     */
    public RouteResult calcularRota(double startLat, double startLon, double endLat, double endLon) {
        String url = "https://api.openrouteservice.org/v2/directions/driving-car";

        Map<String, Object> body = Map.of(
                "coordinates", new double[][]{
                        {startLon, startLat}, // ORS exige ordem: lon, lat
                        {endLon, endLat}
                }
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", orsApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<RouteResult> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    RouteResult.class
            );

            RouteResult result = response.getBody();

            if (result == null || result.routes() == null || result.routes().isEmpty()) {
                throw new RouteNotFoundException("Não foi possível calcular a rota entre os pontos fornecidos.");
            }

            return result;

        } catch (HttpStatusCodeException e) {
            throw new ExternalServiceException(
                    "Erro HTTP ao chamar ORS: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(),
                    e
            );
        } catch (Exception e) {
            throw new ExternalServiceException("Erro ao processar rota: " + e.getMessage(), e);
        }
    }
}
