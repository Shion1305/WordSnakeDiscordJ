package com.shion1305.discord.wordsnake.data;

import com.shion1305.discord.wordsnake.WordData;
import com.shion1305.discord.wordsnake.WordSnakeChecker;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class DataManager {
    public static void main(String[] args) {
        ArrayList<WordData> data = new ArrayList<>();
        String[] sFiles = {"一般名詞.csv", "固有名詞・数詞.csv", "普通名詞.csv"};


        try (Scanner s = new Scanner(new FileInputStream("goi_norm.csv"), StandardCharsets.UTF_8)) {
            backto:
            while (s.hasNextLine()) {
                String[] inputs = s.nextLine().split(",");
                for (WordData d : data) {
                    if (d.word.equals(inputs[1]) && d.kana.equals(inputs[2])) {
                        System.out.println("Duplicated: " + inputs[1]);
                        continue backto;
                    }
                }
                data.add(new WordData(0, inputs[1], inputs[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String file : sFiles) {
            try (Scanner s = new Scanner(new FileInputStream("Datasets/" + file), StandardCharsets.UTF_8)) {
                backto:
                while (s.hasNextLine()) {
                    String[] inputs = s.nextLine().split(",");
                    for (WordData d : data) {
                        if (WordSnakeChecker.convertKata(d.word).equals(WordSnakeChecker.convertKata(inputs[0])) && d.kana.equals(inputs[1])) {
                            System.out.println("Duplicated: " + inputs[0]);
                            continue backto;
                        }
                    }
                    data.add(new WordData(0, inputs[0], inputs[1]));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (PrintWriter wr = new PrintWriter(new FileOutputStream("Datasets/all_out.csv", false))) {
            for (int i = 0; i < data.size(); i++) {
                WordData wd = data.get(i);
                wr.write(String.valueOf(i));
                wr.write(",");
                wr.write(wd.word);
                wr.write(",");
                wr.write(wd.kana);
                wr.write("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
