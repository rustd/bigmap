package com.bigdatatag;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.bigdatatag.streamingEntity.Measurement;

public class JsonParser {
    public static Measurement parseJson(String line) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        Measurement measurement = objectMapper.readValue(line, Measurement.class);
        return measurement;
    }
}
