package com.github.shy526.devenvarrange.help;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * io处理工具
 */
public class IoHelp {
    public static void copy(InputStream in, OutputStream out) {
        byte[] bytes = new byte[1024 * 10];
        int len = -1;
        try {
            while ((len = in.read(bytes, 0, bytes.length)) != 0) {
                out.write(bytes, 0, len);
            }
            out.flush();
        } catch (Exception ignored) {
        } finally {
            in = close(in, InputStream.class);
            out = close(out, OutputStream.class);
        }
    }

    public static <T> T close(Closeable closeable, Class<T> tClass) {
        if (closeable == null) {
            return null;
        }
        try {
            closeable.close();
        } catch (Exception ignored) {
        } finally {
            closeable = null;
        }
        return null;
    }

}
