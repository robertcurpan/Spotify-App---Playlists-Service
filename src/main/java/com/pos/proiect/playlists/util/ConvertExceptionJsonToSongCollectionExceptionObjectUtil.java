package com.pos.proiect.playlists.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pos.proiect.playlists.exception.ErrorObject;
import org.springframework.web.client.HttpClientErrorException;

public class ConvertExceptionJsonToSongCollectionExceptionObjectUtil {

    public static ErrorObject getExceptionObjectFromHttpClientExceptionJson(HttpClientErrorException ex) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String exceptionBody = ex.getResponseBodyAsString();
        ErrorObject exceptionObject = objectMapper.readValue(exceptionBody, ErrorObject.class);
        return exceptionObject;
    }
}
