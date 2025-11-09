package com.pmh.backendhomemedcare.util;

import com.pmh.backendhomemedcare.exception.ExternalServiceException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;

/**
 * Utilitários para tratamento de dados de endereço.
 */
public final class AddressUtils {

    private AddressUtils() { /* util */ }

    /**
     * Sanitiza um endereço para uso em queries externas:
     * - remove acentuação (diacríticos)
     * - remove caracteres de pontuação, substituindo por espaços
     * - colapsa múltiplos espaços e aplica trim
     * - lança ExternalServiceException se o resultado ficar vazio
     * - retorna a string já URL-encoded (UTF-8)
     */
    public static String sanitizeAddress(String address) throws ExternalServiceException {
        if (address == null) return "";

        // Remove acentos (normaliza para forma NFD e remove marcas de diacríticos)
        String normalized = Normalizer.normalize(address, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        // Remove qualquer caractere que não seja letra, número ou espaço — substitui por espaço para evitar junção de palavras
        String cleaned = normalized.replaceAll("[^\\p{Alnum}\\s]", " ");
        // Colapsa múltiplos espaços e trim
        cleaned = cleaned.replaceAll("\\s+", " ").trim();

        if (cleaned.isEmpty()) {
            throw new ExternalServiceException("Endereço inválido após sanitização: " + address);
        }

        return URLEncoder.encode(cleaned, StandardCharsets.UTF_8);
    }
}

