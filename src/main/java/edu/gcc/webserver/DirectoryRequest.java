package edu.gcc.webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;

/**
 * A basic handler responsible for finding and returning a requested file from a directory
 */
public class DirectoryRequest implements RequestHandler {

    private final String REQUEST_PATH;
    private final File DIRECTORY;

    public DirectoryRequest(File dir, String path){
        if (!dir.isDirectory()){
            throw new IllegalArgumentException("dir must be a directory!");
        }
        DIRECTORY = dir;
        REQUEST_PATH = path;
    }


    /**
     * Handle the given request and generate an appropriate response.
     * See {@link HttpExchange} for a description of the steps
     * involved in handling an exchange.
     *
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws NullPointerException if exchange is {@code null}
     * @throws IOException          if an I/O error occurs
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath().substring(REQUEST_PATH.length());

        if (path.isEmpty()){ path = "index.html"; }

        File toSend = new File(DIRECTORY,path);

        if (!toSend.exists()){

            toSend = new File(DIRECTORY,"404.html");
            exchange.sendResponseHeaders( 404, toSend.length() );

            OutputStream out = exchange.getResponseBody();

            Files.copy( toSend.toPath(), out );
            out.close();
            return;
        }

        respond(exchange, RESPONSE_CODE.OK, toSend);
    }
}
