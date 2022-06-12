package com.vulcanth.nyel.misc;

import java.io.Closeable;
import java.io.IOException;

public class KotlinFeatures {
    public interface Let<T> {
        void run(T it) throws Exception;
    }

    public static <T> void let(T obj, Let<T> le) {
        if (le == null) return;
        try {
            le.run(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T extends Closeable> void use(T obj, Let<T> le) {
        if (le == null) return;
        try {
            le.run(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            obj.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T extends AutoCloseable> void use(T obj, Let<T> le) {
        if (le == null) return;
        try {
            le.run(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            obj.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

}
