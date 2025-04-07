package edu.gcc.webserver;

import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class WebServer {

    public static final int THREAD_POOL_SIZE = 20;
    HttpServer server;

    public WebServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(80), 0);
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        server.setExecutor(threadPool);
        server.start();

        server.createContext("/",new FileRequest(new File("server/index.html")));
        server.createContext("/static/",new DirectoryRequest(new File("server/static"),"/static/"));
    }
}
