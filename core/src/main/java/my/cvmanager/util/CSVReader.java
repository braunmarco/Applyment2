package my.cvmanager.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CSVReader<T> {
    private final Logger logger = Logger.getLogger(CSVReader.class.getCanonicalName());

    public List<T> readCSV(InputStream inputStream, Class<T> clazz) {
        List<T> objects = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            String[] headers = br.readLine().split(";");
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                T object = createObject(clazz, headers, values);
                objects.add(object);
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der CSV-Datei: " + e.getMessage());
        }

        return objects;
    }

    /**
     * Creates an object of type T from CSV headers and values.
     *
     * @param clazz   The entity class type.
     * @param headers The CSV header names.
     * @param values  The CSV row values.
     * @return A populated object of type T or null if creation fails.
     */
    public T createObject(Class<T> clazz, String[] headers, String[] values) {
        try {
            Constructor<T> constructor = clazz.getConstructor();
            T object = constructor.newInstance();

            for (int i = 0; i < headers.length; i++) {
                String header = normalize(headers[i]);

                boolean fieldFound = false;

                for (Field field : clazz.getDeclaredFields()) {
                    if (normalize(field.getName()).equals(header)) {
                        field.setAccessible(true);

                        String rawValue = values[i].trim().replace("\"", "");

                        if (field.getType() == String.class) {
                            field.set(object, rawValue);
                        } else if (field.getType() == LocalDate.class && !rawValue.isEmpty()) {
                            field.set(object, LocalDate.parse(rawValue));
                        }
                        // Add more types if necessary

                        fieldFound = true;
                        break;
                    }
                }

                if (!fieldFound) {
                    logger.warning("No matching field found for CSV column: " + headers[i]);
                }
            }
            return object;

        } catch (Exception e) {
            logger.severe("Error creating object: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Normalizes header/field names by converting to lowercase and removing underscores.
     */
    private String normalize(String name) {
        return name.trim().toLowerCase().replace("_", "").replace("\"", "");
    }
}