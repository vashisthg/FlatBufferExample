package in.co.ophio.flatbufferexample;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author Gaurav Vashisth.
 */
public class DataReader {


    public static String loadJsonString(InputStream inputStream) throws IOException {
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        int count;
        while ((count = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, count);
        }
        return writer.toString();
    }

    public static byte[] loadBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[10240];
        int i;
        while ((i = inputStream.read(buff, 0, buff.length)) > 0) {
            baos.write(buff, 0, i);
        }
        return baos.toByteArray();
    }

}
