package top.flagshen.myqq.util;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class MgcUtil {

    public MgcUtil() {
    }

    private static Set<String> dictionary = new HashSet<String>();

    public static Set<String> loadFile() {
        if (dictionary.size() > 0) {
            return dictionary;
        }
        try (
                FileInputStream fileInputStream = new FileInputStream("C:\\mgc.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
        ) {
            String tmp;
            while ((tmp = br.readLine()) != null) {
                dictionary.add(tmp);
            }
        } catch (IOException e) {
        }
        return dictionary;
    }

}