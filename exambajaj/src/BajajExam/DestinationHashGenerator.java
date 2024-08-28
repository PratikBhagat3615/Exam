package BajajExam;

import org.json.JSONObject;
import org.json.JSONArray;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.nio.charset.StandardCharsets;

public class DestinationHashGenerator {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar 240343020075 C:\\Users\\ACER\\Desktop\\Jars\\example.json");
            return;
        }

        String prnNumber = args[0].toLowerCase().replaceAll("\\s", "");
        String filePath = args[1];

        try {
            // Read JSON content from file
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(jsonContent);

            // Find the destination value
            String destinationValue = findDestinationValue(jsonObject);
            if (destinationValue == null) {
                System.out.println("Destination key not found");
                return;
            }

            // Generate random string
            String randomString = generateRandomString(8);

            // Concatenate strings
            String concatenatedString = prnNumber + destinationValue + randomString;

            // Generate MD5 hash
            String md5Hash = generateMD5Hash(concatenatedString);

            // Output the hash and random string
            System.out.println(md5Hash + ";" + randomString);

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String findDestinationValue(JSONObject jsonObject) {
        return findDestinationValueRecursive(jsonObject);
    }

    private static String findDestinationValueRecursive(Object obj) {
        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            for (String key : jsonObject.keySet()) {
                Object value = jsonObject.get(key);
                if (key.equals("destination")) {
                    return value.toString();
                }
                String result = findDestinationValueRecursive(value);
                if (result != null) return result;
            }
        } else if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;
            for (Object value : jsonArray) {
                String result = findDestinationValueRecursive(value);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
