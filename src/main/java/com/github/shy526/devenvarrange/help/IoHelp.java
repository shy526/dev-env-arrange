package com.github.shy526.devenvarrange.help;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * io处理工具
 */
public class IoHelp {
    public static void copy(InputStream in, OutputStream out, boolean flag, Consumer<Long> consumer) {
        byte[] bytes = new byte[1024 * 1024];
        int len = -1;
        long speed = 0;
        try {
            while ((len = in.read(bytes)) != -1) {
                out.write(bytes, 0, len);
                speed+=len;
                if (consumer!=null){
                    consumer.accept(speed);
                }
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

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(Files.newInputStream(source));
            bos = new BufferedOutputStream(Files.newOutputStream(target));
            byte[] bytes = new byte[1024 * 10];
            int len = -1;
            while ((len = bis.read(bytes, 0, bytes.length)) != 0) {
                bos.write(bytes, 0, len);
            }
            bos.flush();
            result = true;
        } catch (Exception ignored) {
        } finally {
            close(bis, InputStream.class);
            close(bos, InputStream.class);
        }
        return result;
    }

    public static Path unZip(Path zipPath, Path target) {
        if (!zipPath.toFile().exists()) {
            return null;
        }
        Path root = null;
        boolean flag = true;
        try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry item = entries.nextElement();
                Path itemPath = target.resolve(item.getName());
                File file = itemPath.toFile();
                if (item.isDirectory()) {
                    boolean temp = file.exists() || file.mkdirs();
                    root = flag ? itemPath : root;
                    flag = false;
                    continue;
                }

                File parentFile = file.getParentFile();
                boolean temp = parentFile.exists() || parentFile.mkdirs();
                try (InputStream in = new BufferedInputStream(zipFile.getInputStream(item)); OutputStream out = new BufferedOutputStream(Files.newOutputStream(itemPath));
                ) {
                    IoHelp.copy(in, out, false,null);
                } catch (Exception ignored) {
                }
                flag = false;
            }
        } catch (Exception ignored) {
        }
        if (root != null) {
            while (true){
                Path parent = root.getParent();
                if (parent.compareTo(target)==0){
                    return root;
                }
                root=parent;
            }

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
