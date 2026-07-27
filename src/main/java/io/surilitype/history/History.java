package io.surilitype.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class History {

    private final List<Result> result;

    public History() {
        this.result = new ArrayList<>();
    }

    public void append(Result r) {
        this.result.add(r);
    }

    List<Result> recent() {
        var out = new ArrayList<>(List.copyOf(result));
        Collections.reverse(out);
        return out;
    }
}
