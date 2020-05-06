/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.github.ness.utility;

public class MathUtils {
    public static final float PI = (float)Math.PI;
    private static final float[] SIN_TABLE_FAST = new float[4096];

    public static int floor(double var0) {
        int var2 = (int) var0;
        return var0 < (double) var2 ? var2 - 1 : var2;
    }

    public static float sin(float par0) {
        return SIN_TABLE_FAST[(int) (par0 * 651.8986F) & 4095];
    }

    public static float cos(float par0) {
        return SIN_TABLE_FAST[(int) ((par0 + ((float) Math.PI / 2F)) * 651.8986F) & 4095];
    }

    public static float toRadians(float angdeg) {
        return angdeg / 180.0F * PI;
    }

    static {
        int i;

        for (i = 0; i < 4096; i++) {
            SIN_TABLE_FAST[i] = (float) Math.sin((double) (((float) i + 0.5F) / 4096.0F * ((float) Math.PI * 2F)));
        }

        for (i = 0; i < 360; i += 90) {
            SIN_TABLE_FAST[(int) ((float) i * 11.377778F) & 4095] = (float) Math.sin((double) ((float) i * 0.017453292F));
        }
    }
}