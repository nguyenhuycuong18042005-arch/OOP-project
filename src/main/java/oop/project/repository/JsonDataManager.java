package oop.project.repository;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JsonDataManager<T> {
    private final Gson gson;

    public JsonDataManager(){
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    public void saveToFile(List<T> data, String filePath){
        try(Writer writer = new FileWriter(filePath)){
            gson.toJson(data, writer);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public List<T> loadFromFile(String filePath, Type typeOfT) {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(filePath)) {
            List<T> data = gson.fromJson(reader, typeOfT);//chuyển đổi json thành Object
            return data != null ? data : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    private static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate>{
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context){
            return new JsonPrimitive(formatter.format(src));
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException{
            return LocalDate.parse(json.getAsString(), formatter);
        }
    }
}
