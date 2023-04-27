package com.github.shy526.devenvarrange.help;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * io处理工具
 */
public class IoHelp {
    public static void copy(InputStream in, OutputStream out, boolean flag) {
        byte[] bytes = new byte[1024 * 10];
        int len = -1;
        try {
            while ((len = in.read(bytes, 0, bytes.length)) != 0) {
                out.write(bytes, 0, len);
            }
            out.flush();
        } catch (Exception ignored) {
        } finally {
            if (flag) {
                in = close(in, InputStream.class);
                out = close(out, OutputStream.class);
            }
        }
    }

    public static boolean copy(Path source, Path target) {
        boolean result = false;
        byte[] bytes = new byte[1024 * 10];
        int len = -1;
        try (
                BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(source));
                BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(target));
        ) {
            while ((len = bis.read(bytes, 0, bytes.length)) != 0) {
                bos.write(bytes, 0, len);
            }
            bos.flush();
            result = true;
        } catch (Exception ignored) {
            result = false;
        }
        return result;
    }

    public static Path unZip(Path zipPath, Path target) {
        if (!zipPath.toFile().exists()) {
            return null;
        }
        String root = null;
        boolean flag = true;
        try (ZipFile zipFile = new ZipFile(zipPath.toFile());) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry item = entries.nextElement();
                Path itemPath = target.resolve(item.getName());
                File file = itemPath.toFile();
                if (item.isDirectory()) {
                    boolean temp = file.exists() || file.mkdirs();
                    root = flag ? item.getName() : root;
                    flag = false;
                    continue;
                }

                File parentFile = file.getParentFile();
                boolean temp = parentFile.exists() || parentFile.mkdirs();
                try (InputStream in = new BufferedInputStream(zipFile.getInputStream(item)); OutputStream out = new BufferedOutputStream(Files.newOutputStream(itemPath));
                ) {
                    IoHelp.copy(in, out, false);
                } catch (Exception ignored) {
                }
                flag = false;
            }
        } catch (Exception ignored) {
        }
        if (root != null) {
            target = target.resolve(root);
        }
        return target;
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
