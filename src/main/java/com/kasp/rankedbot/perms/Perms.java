package com.kasp.rankedbot.perms;

import com.kasp.rankedbot.RankedBot;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Perms {

    public static HashMap<String, String> permsData = new HashMap<>();

    public static void loadPerms() {
        String filename = "permissions.yml";
        ClassLoader classLoader = RankedBot.class.getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(filename)) {
            String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            File file = new File("RankedBot/" + filename);
            if (!file.exists()) {
                file.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter("RankedBot/" + filename));
                bw.write(result);
                bw.close();
            }

            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(new FileInputStream("RankedBot/permissions.yml"));
            for (String s : data.keySet()) {
                if (data.get(s) != null) {
                    permsData.put(s, data.get(s).toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Successfully loaded the permissions file into memory");
    }

    public static void reload() {
        permsData.clear();

        loadPerms();
    }

    public static String getPerm(String permission) {
        return permsData.get(permission);
    }
}
