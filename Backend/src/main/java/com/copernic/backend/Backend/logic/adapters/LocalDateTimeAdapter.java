package com.copernic.backend.Backend.logic.adapters;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Adaptador personalitzat per serialitzar i deserialitzar objectes {@link LocalDateTime} amb Gson.
 * Converteix instàncies de {@code LocalDateTime} a cadenes en format ISO-8601 i viceversa.
 * Utilitza el format {@code ISO_LOCAL_DATE_TIME} per garantir compatibilitat estàndard.
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    /**
     * Formatador per convertir entre {@link LocalDateTime} i la seva representació textual ISO-8601.
     */
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Serialitza un objecte {@link LocalDateTime} a una representació JSON en format ISO-8601.
     *
     * @param src L'objecte {@code LocalDateTime} a serialitzar.
     * @param typeOfSrc Tipus de la font (normalment {@code LocalDateTime.class}).
     * @param context Context de serialització de Gson.
     * @return Un {@link JsonPrimitive} amb la data i hora en format ISO-8601.
     */
    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.format(formatter));
    }

    /**
     * Deserialitza una cadena JSON en format ISO-8601 a un objecte {@link LocalDateTime}.
     *
     * @param json Element JSON que conté la data i hora com a cadena.
     * @param typeOfT Tipus de destí (normalment {@code LocalDateTime.class}).
     * @param context Context de deserialització de Gson.
     * @return Un objecte {@link LocalDateTime} equivalent a la cadena proporcionada.
     * @throws JsonParseException Si la cadena no es pot parsejar com a {@code LocalDateTime}.
     */
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return LocalDateTime.parse(json.getAsString(), formatter);
    }
}



