package edu.gcc.webserver;

import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;

/**
 * A basic http handler responsible for returning a single file upon request
 */
public class FileRequest implements RequestHandler {

    private final File SEND_FILE;

    public FileRequest(File f){
        SEND_FILE = f;
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
        System.out.println("Hello World");
        System.out.println(SEND_FILE.getAbsolutePath());
        respond(exchange,RESPONSE_CODE.OK,SEND_FILE);
    }
}
