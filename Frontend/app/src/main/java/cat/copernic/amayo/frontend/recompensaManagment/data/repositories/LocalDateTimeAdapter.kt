package cat.copernic.amayo.frontend.recompensaManagment.data.repositories

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Adaptador personalitzat per a serialitzar i deserialitzar objectes [LocalDateTime] amb Gson.
 *
 * Utilitza el format ISO-8601 per defecte (`DateTimeFormatter.ISO_LOCAL_DATE_TIME`) per convertir
 * entre [LocalDateTime] i la seva representació en cadena dins de JSON.
 *
 * Ús:
 *      Quan es vol persistir un [LocalDateTime] com a string en JSON.
 *      Per convertir automàticament les dates a la seva representació estàndard ISO-8601.
 * @property formatter Formatador per donar format a les dates i parsejar-les.
 */
class LocalDateTimeAdapter(
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
) : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    /**
     * Serialitza un [LocalDateTime] a un element JSON com a cadena.
     *
     * @param src Valor de [LocalDateTime] a serialitzar.
     * @param typeOfSrc Tipus de la font (no utilitzat directament).
     * @param context Context de serialització Gson.
     * @return [JsonElement] amb la representació en text del LocalDateTime.
     */
    override fun serialize(
        src: LocalDateTime?,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement =
        JsonPrimitive(src?.format(formatter))

    /**
     * Deserialitza un [JsonElement] en un objecte [LocalDateTime] usant el formatador especificat.
     *
     * @param json Element JSON a deserialitzar.
     * @param typeOfT Tipus objectiu (no utilitzat directament).
     * @param context Context de deserialització Gson.
     * @return Objecte [LocalDateTime] resultant del parseig.
     */
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): LocalDateTime =
        LocalDateTime.parse(json.asString, formatter)
}