package com.shion1305.discord.wordsnake;

import java.util.ArrayList;
import java.util.List;

public class WordCheckResult {
    public static final int WORD_INVALID = 1, WORD_OK = 0, WORD_CONVENTION_REQUIRED = 2, WORD_NOT_FOUND = 3, WORD_NOT_SATISFIED = 4, WORD_OUT = 5;
    int code;
    WordData data;
    List<WordData> banList;

    public WordCheckResult(int i) {
        code = i;
    }

    public WordCheckResult(int i, WordData data) {
        code = i;
        this.data = data;
        banList = new ArrayList<>();
        banList.add(data);
    }

    public WordCheckResult(int i, WordData data, List<WordData> datas) {
        code = i;
        this.data = data;
        banList = datas;
    }
}
