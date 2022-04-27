package com.mf.starter.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CollectionUtil {
    public static List<?> convertObjectToList(Object object) {
        List<?> list = new ArrayList<>();
        if (object.getClass().isArray()) {
            list = Arrays.asList((Object[])object);
        } else if (object instanceof Collection) {
            list = new ArrayList<>((Collection<?>) object);
        }
        return list;
    }
}
