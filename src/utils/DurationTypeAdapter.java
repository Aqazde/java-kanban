package utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter out, Duration duration) throws IOException {
        if (duration == null) {
            out.nullValue();
        } else {
            out.value(duration.toMinutes()); // Сериализация Duration в минуты
        }
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        // Обработка чисел
        if (in.peek() == com.google.gson.stream.JsonToken.NUMBER) {
            long minutes = in.nextLong();
            return Duration.ofMinutes(minutes);
        }

        // Обработка строк (формат ISO-8601, например, "PT30M")
        if (in.peek() == com.google.gson.stream.JsonToken.STRING) {
            String durationString = in.nextString();
            return Duration.parse(durationString);
        }

        throw new IllegalArgumentException("Неверный формат для Duration: ожидается число (минуты) " +
                "или строка (ISO-8601)");
    }
}