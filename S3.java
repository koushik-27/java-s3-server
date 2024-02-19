/*
 *  CS 656 Fall 2023 Project: AWS S3 buckets V3.30
 *  Copyright (C) New Jersey Institute of Technology
 *  All rights reserved
 *
 *  Group name: A34
 *  Group members: Janvi Rakeshkumar Patel (jp2343),Naga Kousick Reddy Voggu (nv344)
 *
 */
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.PrintStream;
/* -- end of imports -- */

class S3 {

    private byte[] HOST;
    private int PORT;
    private InetAddress Addr;
    private Socket s1;
    // HTTP "200 OK" header
    private final byte[] HTTP_OK_HEADER = new byte[] { 'H', 'T', 'T', 'P', '/', '1', '.', '1', ' ', '2', '0', '0', ' ',
            'O', 'K', '\r', '\n' };
    // HTTP "404 Not Found" header
    private final byte[] HTTP_NOT_FOUND_HEADER = new byte[] { 'H', 'T', 'T', 'P', '/', '1', '.', '1', ' ', '4', '0',
            '4', ' ', 'N', 'o', 't', ' ', 'F', 'o', 'u', 'n', 'd', '\r', '\n', 'C', 'o', 'n', 't', 'e', 'n', 't', '-',
            'T', 'y', 'p', 'e', ':', ' ', 't', 'e', 'x', 't', '/', 'p', 'l', 'a', 'i', 'n', '\r', '\n', '\r', '\n', 'F',
            'i', 'l', 'e', ' ', 'D', 'o', 'e', 's', 'n', '\'', 't', ' ', 'E', 'x', 'i', 's', 't' };
    // HTTP "416 Requested Range Not Satisfiable" header
    private final byte[] HTTP_RANGE_NOT_SATISFIABLE_HEADER = new byte[] {
                'H', 'T', 'T', 'P', '/', '1', '.', '1', ' ', '4', '1', '6', ' ', 
                'R', 'a', 'n', 'g', 'e', ' ', 'N', 'o', 't', ' ', 'S', 'a', 't', 'i', 's', 'f', 'i', 'a', 'b', 'l', 'e', '\r', '\n',
                'C', 'o', 'n', 't', 'e', 'n', 't', '-', 'T', 'y', 'p', 'e', ':', ' ', 't', 'e', 'x', 't', '/', 'p', 'l', 'a', 'i', 'n', '\r', '\n',
                'C', 'o', 'n', 't', 'e', 'n', 't', '-', 'L', 'e', 'n', 'g', 't', 'h', ':', ' ', '3', '4', '\r', '\n',
                '\r', '\n',
                'R', 'e', 'q', 'u', 'e', 's', 't', 'e', 'd', ' ', 'D', 'a', 't', 'a', ' ', 'o', 'u', 't', ' ', 'o', 'f', ' ', 'F', 'i', 'l', 'e', ' ', 'L', 'i', 'm', 'i', 't', 's'
            };
    
    // HTTP "400 Bad Request" header
    private final byte[] HTTP_BAD_REQUEST_HEADER = new byte[] {
        'H', 'T', 'T', 'P', '/', '1', '.', '1', ' ', '4', '0', '0', ' ', 
        'B', 'a', 'd', ' ', 'R', 'e', 'q', 'u', 'e', 's', 't', '\r', '\n',
        'C', 'o', 'n', 't', 'e', 'n', 't', '-', 'T', 'y', 'p', 'e', ':', ' ', 
        't', 'e', 'x', 't', '/', 'p', 'l', 'a', 'i', 'n', '\r', '\n',
        'C', 'o', 'n', 't', 'e', 'n', 't', '-', 'L', 'e', 'n', 'g', 't', 'h', ':', ' ', 
        '2', '3', '\r', '\n',
        '\r', '\n',
        'I', 'n', 'v', 'a', 'l', 'i', 'd', ' ', 'R', 'e', 'q', 'u', 'e', 's', 't', ' ', 'F', 'o', 'r', 'm', 'a', 't'
    };
    
            
    // Constant byte arrays for different content types
    private final byte[] CONTENT_TYPE_PDF = new byte[] { 'C', 'o', 'n', 't', 'e', 'n', 't', '-', 'T', 'y', 'p', 'e',
            ':', ' ', 'a', 'p', 'p', 'l', 'i', 'c', 'a', 't', 'i', 'o', 'n', '/', 'p', 'd', 'f', '\r', '\n', '\r',
            '\n' };
    private final byte[] CONTENT_TYPE_JPG = new byte[] { 'C', 'o', 'n', 't', 'e', 'n', 't', '-', 'T', 'y', 'p', 'e',
            ':', ' ', 'i', 'm', 'a', 'g', 'e', '/', 'j', 'p', 'e', 'g', '\r', '\n', '\r', '\n' };
    private final byte[] CONTENT_TYPE_PNG = new byte[] { 'C', 'o', 'n', 't', 'e', 'n', 't', '-', 'T', 'y', 'p', 'e',
            ':', ' ', 'i', 'm', 'a', 'g', 'e', '/', 'p', 'n', 'g', '\r', '\n', '\r', '\n' };
    private final byte[] CONTENT_TYPE_HTML = new byte[] { 'C', 'o', 'n', 't', 'e', 'n', 't', '-', 'T', 'y', 'p', 'e',
            ':', ' ', 't', 'e', 'x', 't', '/', 'h', 't', 'm', 'l', '\r', '\n', '\r', '\n' };
    private final byte[] CONTENT_TYPE_TEXT = new byte[] { 'C', 'o', 'n', 't', 'e', 'n', 't', '-', 'T', 'y', 'p', 'e',
            ':', ' ', 't', 'e', 'x', 't', '/', 'p', 'l', 'a', 'i', 'n', '\r', '\n', '\r', '\n' };

    public static void main(String[] a) throws Exception { /* do not change this method */

        S3 bucket = new S3(Integer.parseInt(a[0]));

        bucket.run(0);

    }

    public S3(int port) {
        PORT = port;
    } /* do not change this method */

    int parse(byte[] buf) {
        int start = -1, end = -1, spaces = 0;
        for (int i = 0; i < buf.length; i++) {
            if (buf[i] == ' ') {
                spaces++;
                if (spaces == 1) {
                    start = i + 1;
                } else if (spaces == 2) {
                    end = i;
                    break;
                }
            }
        }

        if (start != -1 && end != -1) {
            int[] oxLx = extractOxLx(buf, start, end);
            try {
                if (check_fx(buf, start, end, oxLx[0], oxLx[1])) {
                    return 0;
                }
                else if (check_hx(buf, start, end, oxLx[0], oxLx[1])) {
                    return 0;
                }
                else {
                    s1.getOutputStream().write(HTTP_BAD_REQUEST_HEADER);
                    s1.close();
                    return -1;
                }
            } catch (Exception e) {
                // Handle Exception here
                e.printStackTrace();
                return -1;
            }
        }

        return -1;
    }

    int dns(int X) { /* do not change the signature */
        /* X can be whatever you like */
        /* this method may be used to set Addr */
        return 0;
    }

    int run(int X) throws Exception /* do not change the signature */
    {
        ServerSocket s0 = new ServerSocket(PORT);
        // Socket s1 = null;
        byte[] b0 = null;
        InputStream input = null; // input stream
        int req_data = 0; // number of bytes read

        while (true) {

            s1 = s0.accept( /* -- */ );

            // s1.getInputStream().read( /* aaa, bbb, ccc */ ) ;
            b0 = new byte[1024]; // 1K buffer
            input = s1.getInputStream();
            req_data = input.read(b0, 0, b0.length); // read upto 1K bytes
            if (req_data > 0) {
                parse(/* b0 ?? or some other buffer ?? */ b0);
            }

            dns(0);
            input.close();

        } /* while loop */
    } /* run */

    /* ------------- your own methods below this line ONLY ----- */

    // convert byte array to string
    String byte2str(byte[] b, int index, int length) {
        byte[] bs = new byte[length];
        for (int i = 0; i < length; i++) {
            bs[i] = b[index + i];
        }
        return new String(bs);
    }

    // extracting integer values of ox and lx
    private int[] extractOxLx(byte[] buf, int start, int end) {
        int ox = -1, lx = -1;
        byte[] oxBytes = new byte[] { 'o', 'x', '=' };
        byte[] lxBytes = new byte[] { 'l', 'x', '=' };

        for (int i = start; i < end; i++) {
            if (buf[i] == '&') {
                if (isMatch(buf, i + 1, oxBytes)) {
                    ox = extractNumber(buf, i + 4, end);
                } else if (isMatch(buf, i + 1, lxBytes)) {
                    lx = extractNumber(buf, i + 4, end);
                }
            }
        }
        return new int[] { ox, lx };
    }

    // helps to check existence of lx and ox
    private boolean isMatch(byte[] buf, int start, byte[] match) {
        for (int i = 0; i < match.length; i++) {
            if (start + i >= buf.length || buf[start + i] != match[i]) {
                return false;
            }
        }
        return true;
    }

    // extracts integer values
    private int extractNumber(byte[] buf, int start, int end) {
        int number = 0;
        for (int i = start; i < end && buf[i] >= '0' && buf[i] <= '9'; i++) {
            number = number * 10 + (buf[i] - '0');
        }
        return number;
    }

    boolean check_fx(byte[] buf, int start, int end, int ox, int lx) throws Exception {
        byte[] fx = new byte[] { '/', 'G', 'E', 'T', '/', 'f', 'x', ':', '/', '/' };
        int fxIndex = 0;
        for (int i = start; i < end && fxIndex < fx.length; i++) {
            if (buf[i] == fx[fxIndex]) {
                fxIndex++;
            } else {
                return false;
            }
        }

        if (fxIndex == fx.length) {
            int filenameEnd = start + fx.length;
            while (filenameEnd < end && buf[filenameEnd] != '&' && buf[filenameEnd] != ' ') {
                filenameEnd++;
            }

            byte[] filename = new byte[filenameEnd - (start + fx.length)];
            for (int i = 0; i < filename.length; i++) {
                filename[i] = buf[start + fx.length + i];
            }
            send_file(filename, s1.getOutputStream(), ox, lx);
            return true;
        } else {
            return false;
        }
    }

    // checking hx
    boolean check_hx(byte[] buf, int start, int end, int ox, int lx) throws Exception {
        byte[] hx = new byte[] { '/', 'G', 'E', 'T', '/', 'h', 'x', ':', '/', '/' };
        int hxIndex = 0;
        for (int i = start; i < end && hxIndex < hx.length; i++) {
            if (buf[i] == hx[hxIndex]) {
                hxIndex++;
            } else {
                return false;
            }
        }

        if (hxIndex == hx.length) {
            int URLend = start + hx.length;
            while (URLend < end && buf[URLend] != '&' && buf[URLend] != ' ') {
                URLend++;
            }
            byte[] URL = new byte[URLend - (start + hx.length)];
            for (int i = 0; i < URL.length; i++) {
                URL[i] = buf[start + hx.length + i];
            }
            // Extract hostname and path
            int urlStart = 7; // 'http://' is 7 bytes long
            int hostnameEnd = urlStart;
            while (hostnameEnd < URL.length && URL[hostnameEnd] != '/') {
                hostnameEnd++;
            }

            // Extract hostname
            HOST = new byte[hostnameEnd - urlStart];
            for (int i = 0; i < HOST.length; i++) {
                HOST[i] = URL[urlStart + i];
            }
            Addr = InetAddress.getByName(byte2str(HOST, 0, HOST.length));
            // Extract path if present
            byte[] path;
            if (hostnameEnd < URL.length) {
                path = new byte[URL.length - hostnameEnd];
                for (int i = 0; i < path.length; i++) {
                    path[i] = URL[hostnameEnd + i];
                }
            } else {
                path = new byte[] { '/' }; // Default path
            }
            connectClient(Addr, path, s1.getOutputStream(), ox, lx);
            return true;
        } else {
            return false;
        }
    }

    // connecting client and external server
    void connectClient(InetAddress addr, byte[] path, OutputStream clientOutput, int ox, int lx) throws Exception {
        Socket serverSocket = null;
        byte[] buffer = new byte[1024];
        int bytesRead;
        try {
            serverSocket = new Socket(addr, 80);
            
            if(ox ==-1 && lx ==-1){
                serverSocket.getOutputStream().write(prepareGetRequestType1(path));
                //System.out.println("ox and lx are not present");
                while((bytesRead = serverSocket.getInputStream().read(buffer)) != -1){
                    clientOutput.write(buffer, 0, bytesRead);
                }
            }
            else{
                serverSocket.getOutputStream().write(prepareGetRequestType2(path, ox, lx));
                //System.out.println("ox and lx are present");
                while((bytesRead = serverSocket.getInputStream().read(buffer)) != -1){
                    clientOutput.write(buffer, 0, bytesRead);
                }
            }
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }
    
    // preparing get request

    private byte[] prepareGetRequestType1(byte[] path) {
        // Standard components of the GET request
        byte[] getPrefix = { 'G', 'E', 'T', ' ' };
        byte[] httpSuffix = { ' ', 'H', 'T', 'T', 'P', '/', '1', '.', '1', '\r', '\n' };
        byte[] hostPrefix = { 'H', 'o', 's', 't', ':', ' ' };
        byte[] endOfHeaders = { '\r', '\n', '\r', '\n' };
    
        // Calculate total length
        int totalLength = getPrefix.length + path.length + httpSuffix.length
                          + hostPrefix.length + HOST.length + endOfHeaders.length;
    
        // Create request byte array
        byte[] request = new byte[totalLength];
        int index = 0;
    
        // Copy components into request
        for (byte b : getPrefix) { request[index++] = b; }
        for (byte b : path) { request[index++] = b; }
        for (byte b : httpSuffix) { request[index++] = b; }
        for (byte b : hostPrefix) { request[index++] = b; }
        for (byte b : HOST) { request[index++] = b; }
        for (byte b : endOfHeaders) { request[index++] = b; }
    
        return request;
    }

    private byte[] prepareGetRequestType2(byte[] path, int ox, int lx) {
        // Standard components of the GET request
        byte[] getPrefix = { 'G', 'E', 'T', ' ' };
        byte[] httpSuffix = { ' ', 'H', 'T', 'T', 'P', '/', '1', '.', '1', '\r', '\n' };
        byte[] hostPrefix = { 'H', 'o', 's', 't', ':', ' ' };
        byte[] newline = { '\r', '\n' };
        byte[] rangePrefix = { 'R', 'a', 'n', 'g', 'e', ':', ' ', 'b', 'y', 't', 'e', 's', '=', };
        byte[] dash = { '-' };
        byte[] endOfHeaders = { '\r', '\n', '\r', '\n' };
    
        // Convert ox and lx to byte arrays
        lx = ox + lx - 1;  // Adjust lx to be the last byte to be requested
        byte[] oxBytes = intToBytes(ox);
        byte[] lxBytes = intToBytes(lx);
    
        // Calculate total length
        int totalLength = getPrefix.length + path.length + httpSuffix.length
                          + hostPrefix.length + HOST.length + newline.length
                          + rangePrefix.length + oxBytes.length + dash.length + lxBytes.length
                          + endOfHeaders.length;
    
        // Create request byte array
        byte[] request = new byte[totalLength];
        int index = 0;
    
        // Copy components into request
        for (byte b : getPrefix) { request[index++] = b; }
        for (byte b : path) { request[index++] = b; }
        for (byte b : httpSuffix) { request[index++] = b; }
        for (byte b : hostPrefix) { request[index++] = b; }
        for (byte b : HOST) { request[index++] = b; }
        for (byte b : newline) { request[index++] = b; }
        for (byte b : rangePrefix) { request[index++] = b; }
        for (byte b : oxBytes) { request[index++] = b; }
        for (byte b : dash) { request[index++] = b; }
        for (byte b : lxBytes) { request[index++] = b; }
        for (byte b : endOfHeaders) { request[index++] = b; }
    
        return request;
    }
    
    private byte[] intToBytes(int value) {
        // Handle the special case of 0 immediately
        if (value == 0) {
            return new byte[]{ '0' };
        }
    
        boolean isNegative = value < 0;
        if (isNegative) {
            value = -value; // Make the value positive for conversion
        }
    
        // Calculate the number of digits
        int length = 0;
        int tempValue = value;
        while (tempValue > 0) {
            tempValue /= 10;
            length++;
        }
    
        if (isNegative) {
            length++; // For negative sign
        }
    
        byte[] result = new byte[length];
    
        int index = length - 1;
        while (value > 0) {
            int digit = value % 10;
            result[index--] = (byte) ('0' + digit);
            value /= 10;
        }
    
        if (isNegative) {
            result[0] = '-';
        }
    
        return result;
    }
    
    // figuring out content-type
    private byte[] getContentType(byte[] filename) {
        if (endsWithExtension(filename, new byte[] { '.', 'p', 'd', 'f' })) {
            return CONTENT_TYPE_PDF;
        } else if (endsWithExtension(filename, new byte[] { '.', 'j', 'p', 'e', 'g' })
                || endsWithExtension(filename, new byte[] { '.', 'j', 'p', 'g' })) {
            return CONTENT_TYPE_JPG;
        } else if (endsWithExtension(filename, new byte[] { '.', 'p', 'n', 'g' })) {
            return CONTENT_TYPE_PNG;
        } else if (endsWithExtension(filename, new byte[] { '.', 'h', 't', 'm', 'l' })
                || endsWithExtension(filename, new byte[] { '.', 'h', 't', 'm' })) {
            return CONTENT_TYPE_HTML;
        } else {
            return CONTENT_TYPE_TEXT; // Default to text
        }
    }

    // helps to check the file extension
    private boolean endsWithExtension(byte[] filename, byte[] extension) {
        if (filename.length < extension.length) {
            return false;
        }
        for (int i = 1; i <= extension.length; i++) {
            if (filename[filename.length - i] != extension[extension.length - i]) {
                return false;
            }
        }
        return true;
    }

    // sending file
    /*void send_file(byte[] filename, OutputStream output, int offset, int length) throws Exception {
        File file = new File(byte2str(filename, 0, filename.length));
        if (file.exists() && !file.isDirectory()) {

            long fileSize = file.length();
            if (offset < 0 || length < 0 || offset >= fileSize || (offset + length) > fileSize) {
            // Send error message for out-of-bounds request
            output.write(HTTP_RANGE_NOT_SATISFIABLE_HEADER);
            output.close();
            s1.close();
            return;
            }

            output.write(HTTP_OK_HEADER);
            byte[] contentType = getContentType(filename);
            output.write(contentType);

            FileInputStream fileInput = new FileInputStream(file);

            if (offset > 0) {
                long skipped = fileInput.skip(offset);
                if (skipped != offset) {
                    // Handle error or throw exception
                }
            }

            byte[] fileData = new byte[1024];
            int bytesRead = 0;
            int bytesToRead = (length > 0) ? length : Integer.MAX_VALUE;

            while (bytesRead < bytesToRead && (bytesRead = fileInput.read(fileData, 0,
                    Math.min(fileData.length, bytesToRead - bytesRead))) != -1) {
                output.write(fileData, 0, bytesRead);
                bytesRead += bytesRead;
            }

            fileInput.close();
            output.close();
            s1.close();
        } else {
            output.write(HTTP_NOT_FOUND_HEADER);
            output.close();
            s1.close();
        }
    }*/

    void send_file(byte[] filename, OutputStream output, int offset, int length) throws Exception {
        File file = new File(byte2str(filename, 0, filename.length));
        if (!file.exists() || file.isDirectory()) {
            output.write(HTTP_NOT_FOUND_HEADER);
            output.close();
            s1.close();
            return;
        }
    
        long fileSize = file.length();
        if (offset == -1 && length == -1) {
            // Set offset and length to read the entire file
            offset = 0;
            length = (int) fileSize;
        } else if (offset < 0 || length < 0 || offset > fileSize || (offset + length) > fileSize) {
            // Send error message for out-of-bounds request
            output.write(HTTP_RANGE_NOT_SATISFIABLE_HEADER);
            output.close();
            s1.close();
            return;
        }
    
        output.write(HTTP_OK_HEADER);
        byte[] contentType = getContentType(filename);
        output.write(contentType);
    
        FileInputStream fileInput = new FileInputStream(file);
    
        if (offset > 0) {
            fileInput.skip(offset);
        }
    
        byte[] fileData = new byte[1024];
        int bytesRead;
        int bytesToRead = length;
    
        while (bytesToRead > 0 && (bytesRead = fileInput.read(fileData, 0, Math.min(fileData.length, bytesToRead))) != -1) {
            output.write(fileData, 0, bytesRead);
            bytesToRead -= bytesRead;
        }
    
        fileInput.close();
        output.close();
        s1.close();
    }
    
    
    /* --- end of all methods --- */
} /* class S3 */