package edu.gcc.webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public interface RequestHandler extends HttpHandler {

    class RESPONSE_CODE {
        public static final int OK = 200;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
    }

    default void respond(HttpExchange exchange, int rcode, String s) throws IOException {
        exchange.sendResponseHeaders(rcode, s.length());
        OutputStream out = exchange.getResponseBody();

        out.write( s.getBytes() );
        out.close();
    }

    default void respond(HttpExchange exchange, int rcode, File f) throws IOException {
        exchange.sendResponseHeaders(rcode, f.length());
        OutputStream out = exchange.getResponseBody();

        Files.copy( f.toPath(), out );
        out.close();
    }
}
