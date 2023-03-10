package com.heima.utils.common;

import com.sun.corba.se.spi.ior.ObjectKey;

import java.util.*;

/**
 * DFA算法
 *
 * @Author peelsannaw
 * @create 13/11/2022 下午12:35
 */

@SuppressWarnings("all")
public class SensitiveWordsFilter {
    private static Map<String, Object> treeMap;
    private static final String MAX_MATCH_TYPE = "1";
    private static final String MIN_MATCH_TYPE = "0";

    public static void init(Set<String> words) {
        treeMap = new HashMap<>(words.size());
        Map nowWordMap = null;
        Map nextWordMap = null;
        for (String s : words) {
            //引用
            nowWordMap = treeMap;
            for (int i = 0; i < s.length(); i++) {
                Character item = s.charAt(i);
                if (nowWordMap.get(item) != null) {
                    nowWordMap = (Map) nowWordMap.get(item);
                } else {
                    nextWordMap = new HashMap();
                    nextWordMap.put("isEnd", false);
                    nowWordMap.put(item, nextWordMap);
                    nowWordMap = nextWordMap;
                }
                if (i == s.length() - 1) {
                    nowWordMap.put("isEnd", "true");
                }
            }
        }
    }

    private static Integer getSensitiveWordLength(String txt, int offset, String type) {
        boolean flag = false;
        Integer length = 0;
        Map nowMap = treeMap;
        if (offset >= txt.length()) return 0;
        for (int i = offset; i < txt.length(); i++) {
            Character word = txt.charAt(i);
            nowMap = (Map) nowMap.get(word);
            if (nowMap != null) {
                length++;
                if (nowMap.get("isEnd").equals("true")) {
                    flag = true;
                    if (type.equals(MIN_MATCH_TYPE)) {
                        break;
                    }
                }
            } else break;
        }

        if (flag && length >= 2) {
            return length;
        }
        return 0;
    }


    private static Set<String> getAllSensitiveWords(String txt) {
        Set<String> words = new HashSet<>();
        for(int i = 0;i<txt.length();i++){
            Integer length = getSensitiveWordLength(txt, i, MAX_MATCH_TYPE);
            if(length>0){
                String newWord = txt.substring(i,i+length);
                words.add(newWord);
                i = i+length - 1;
            }

        }
        return words;
    }

    public static String replaceAll(String txt, Character rep) {
        if (rep == null) rep = 'x';
        Set<String> allSensitiveWords = getAllSensitiveWords(txt);
        for (String allSensitiveWord : allSensitiveWords) {
            String s = "";
            for (int i = 0; i < allSensitiveWord.length(); i++){
                s += rep;
            }
            txt = txt.replaceAll(allSensitiveWord, s);
        }
        return txt;
    }


    public static void main(String[] args) {
        String content="我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛" +
                "我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛我操作了这场比赛"
                +"";
        String[] s = {""};

        Set<String> set = new HashSet<>(Arrays.asList(s));
        init(set);
        long l = System.currentTimeMillis();

        replaceAll(content, '?');
//        for(int i = 0;i<s.length;i++){
//            content.replaceAll(s[i],"??");
//        }
        long l2 = System.currentTimeMillis();
        System.out.println(l2-l);
    }
}


