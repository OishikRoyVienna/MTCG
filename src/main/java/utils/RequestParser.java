package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestParser
{
    public static Map<String, String> parseHttpRequest(BufferedReader reader) throws IOException
    {
        Map<String, String> requestData = new HashMap<>();
        String line = reader.readLine();

        if (line != null && !line.isEmpty())
        {
            String[] requestLine = line.split(" ");
            requestData.put("method", requestLine[0]); // GET, POST, etc.
            requestData.put("path", requestLine[1]);   // Requested path

            // Parse headers
            while (!(line = reader.readLine()).isEmpty())
            {
                String[] header = line.split(": ");
                requestData.put(header[0], header[1]);
            }

            // Read the body based on Content-Length
            if ("POST".equals(requestData.get("method")) || "PUT".equals(requestData.get("method")))
            {
                String contentLengthValue = requestData.get("Content-Length");
                if (contentLengthValue != null)
                {
                    int contentLength = Integer.parseInt(contentLengthValue);
                    char[] bodyChars = new char[contentLength];
                    int read = reader.read(bodyChars, 0, contentLength);
                    String body = new String(bodyChars, 0, read);
                    requestData.put("body", body);
                }
            }
        }
        return requestData;
    }
}