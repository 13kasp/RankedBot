package com.kasp.rbw3.messages;

import com.kasp.rbw3.Main;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Msg {

    public static HashMap<String, String> msgData = new HashMap<>();

    public static void loadMsg() throws FileNotFoundException {
        String filename = "messages.yml";
        ClassLoader classLoader = Main.class.getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(filename)) {
            String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            File file = new File("RBW/" + filename);
            if (!file.exists()) {
                file.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter("RBW/" + filename));
                bw.write(result);
                bw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(new FileInputStream("RBW/messages.yml"));
        for (String s : data.keySet()) {
            msgData.put(s, data.get(s).toString());
        }

        System.out.println("Successfully loaded the messages file into memory");
    }

    public static String getMsg(String msg) {
        return msgData.get(msg);
    }
}
