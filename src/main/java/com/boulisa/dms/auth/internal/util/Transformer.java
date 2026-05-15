package com.boulisa.dms.auth.internal.util;

import java.text.Normalizer;

public class Transformer {

    public static String transform(String value) {
        if (value == null) return null;
        return Normalizer.normalize(value.trim(), Normalizer.Form.NFKC).toLowerCase();
    }

}
