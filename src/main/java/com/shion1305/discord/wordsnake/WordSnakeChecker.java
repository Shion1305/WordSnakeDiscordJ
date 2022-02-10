package com.shion1305.discord.wordsnake;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


public class WordSnakeChecker {
    private final static HashMap<Integer, WordData> data;

    //Initialization in static method.
    static {
        data = new HashMap<>();
        try (FileInputStream stream = new FileInputStream(System.getProperty("user.home") + "/ShionServerConfig/WordSnakeDiscordJ/goi_norm.csv")) {
            Scanner reader = new Scanner(stream, StandardCharsets.UTF_8);
            while (reader.hasNextLine()) {
                String[] line = reader.nextLine().split(",");
                data.put(Integer.parseInt(line[0]), new WordData(Integer.parseInt(line[0]), line[1], line[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(data.size());
    }

    public static void main(String[] args) {
        System.out.println("hello");
        System.out.println(getWordsFromWord("明日"));
        System.out.println(data.size());
    }

    public static WordData getWordFromID(int id) {
        return data.get(id);
    }

    public static List<WordData> getWordsFromWord(String query) {
        List<WordData> result = new ArrayList<>();
        for (WordData s : data.values()) {
            if (s.word.equals(query)) {
                result.add(s);
            }
        }
        return result;
    }

    public static List<WordData> getWordsFromKana(String query) {
        List<WordData> result = new ArrayList<>();
        for (WordData s : data.values()) {
            if (s.kana.equals(query)) {
                result.add(s);
            }
        }
        return result;
    }

    public static WordCheckResult checkWord(String content, char ending) {
        System.out.println(content);
        if (!isJCharacter(content)) {
            return new WordCheckResult(WordCheckResult.WORD_INVALID);
        }
        List<WordData> r = WordSnakeChecker.getWordsFromWord(content);
        if (r.isEmpty()) {
            if (isHiraKata(content)) {
                r = WordSnakeChecker.getWordsFromKana(convertKata(content));
                if (r.size() == 1) {
                    if (checkTaboo(r.get(0))) return new WordCheckResult(WordCheckResult.WORD_OK, r.get(0));
                    return new WordCheckResult(WordCheckResult.WORD_OUT, r.get(0));
                }
                if (!r.isEmpty()) return new WordCheckResult(WordCheckResult.WORD_CONVENTION_REQUIRED);
            }
            return new WordCheckResult(WordCheckResult.WORD_NOT_FOUND);
        }
        for (WordData wd : r) {
            if (wd.kana.charAt(0) == ending) {
                if (checkTaboo(wd)) return new WordCheckResult(WordCheckResult.WORD_OK, wd, r);
                return new WordCheckResult(WordCheckResult.WORD_OUT, wd, r);
            }
        }
        return new WordCheckResult(WordCheckResult.WORD_NOT_SATISFIED);
    }

    private static boolean checkTaboo(WordData wd) {
        switch (wd.kana.charAt(wd.kana.length() - 1)) {
            case 'ん':
            case 'ン':
                return false;
        }
        return true;
    }

    public static boolean isJCharacter(String t) {
        return t.matches("^[\\u4E00-\\u9FFF\\p{InHiragana}\\p{InKatakana}]+$");
    }

    public static boolean isHiraKata(String t) {
        return t.matches("^[\\p{InHiragana}\\p{InKatakana}]+$");
    }

    /**
     * @param t target string
     * @return t converted to Katakana
     */
    public static String convertKata(String t) {
        StringBuffer sb = new StringBuffer(t);
        for (int i = 0; i < t.length(); i++) {
            if ('ぁ' <= t.charAt(i) && 'ん' >= t.charAt(i)) {
                sb.setCharAt(i, (char) (t.charAt(i) - 'ぁ' + 'ァ'));
            }
        }
        return sb.toString();
    }
}
