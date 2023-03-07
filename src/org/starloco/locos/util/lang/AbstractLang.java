package org.starloco.locos.util.lang;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Locos on 09/12/2015.
 */
public class AbstractLang implements Lang {

    protected final List<String> sentences = new ArrayList<>();

    public AbstractLang() {
        this.initialize();
    }

    @Override
    public String get(int index) {
        return sentences.get(index);
    }

    @Override
    public void initialize() {}
}
