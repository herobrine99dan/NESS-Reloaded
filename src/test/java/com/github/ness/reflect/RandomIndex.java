package com.github.ness.reflect;

import java.util.concurrent.ThreadLocalRandom;

final class RandomIndex {

    private RandomIndex() {}

    static int randomPositiveIndex() {
        return 1 + ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
    }
}
