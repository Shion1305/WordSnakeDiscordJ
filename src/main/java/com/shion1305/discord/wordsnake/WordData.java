package com.shion1305.discord.wordsnake;

public class WordData {
    int id;
    String word;
    String kana;

    public WordData(int id, String word, String kana) {
        this.id = id;
        this.word = word;
        this.kana = kana;
    }

    @Override
    public String toString() {
        return "WordData{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", kana='" + kana + '\'' +
                '}';
    }
}
