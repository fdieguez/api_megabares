package com.megabares.api.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 *
 * @author Francisco
 */
public enum Compresor {
    ;
    public static byte[] comprimir(String text) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            OutputStream out = new DeflaterOutputStream(baos);
            out.write(text.getBytes("UTF-8"));
            out.close();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return baos.toByteArray();
    }

    public static String descomprimir(byte[] bytes) {
        InputStream in = new InflaterInputStream(new ByteArrayInputStream(bytes));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            return new String(baos.toByteArray(), "UTF-8");
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
