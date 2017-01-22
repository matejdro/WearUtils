package com.matejdro.wearutils.miscutils;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class TextUtils {
    public static boolean containsRegexes(String text, Collection<String> regexes) {
        for (String regex : regexes) {
            try {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(text);
                if (matcher.find())
                    return true;
            } catch (PatternSyntaxException ignored) {
            }
        }

        return false;
    }

}