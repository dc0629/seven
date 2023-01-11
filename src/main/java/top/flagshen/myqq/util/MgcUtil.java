package top.flagshen.myqq.util;
import org.apache.commons.lang3.StringUtils;

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
    private static MgcTrie mgcTrie = null;

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

    public static boolean haveMgc(String word) {
        if (StringUtils.isBlank(word)) {
            return false;
        }
        if (mgcTrie == null) {
            mgcTrie = new MgcTrie();
            Set<String> strings = MgcUtil.loadFile();
            for (String s : strings) {
                mgcTrie.insert(s);
            }
        }
        return mgcTrie.search(word);
    }

}