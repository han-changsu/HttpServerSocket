import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class HttpServerSocket {
    public static void main(String[] args) {
        System.out.println("Server has started on 127.0.0.1:80 \nWaiting for a connection...");
        try {
            ServerSocket serverSocket = new ServerSocket(80);
            for (int i = 0; i < 10; i++) {
                Socket socket = serverSocket.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket clientSocket = socket;
                            System.out.println(clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " Client connected!");
                            InputStream inputStream = clientSocket.getInputStream();
                            OutputStream outputStream = clientSocket.getOutputStream();

                            byte[] bytes = new byte[1024];
                            inputStream.read(bytes);
                            String requestData = new String(bytes, StandardCharsets.UTF_8).replace("\0", "");

                            String method = requestData.split(" ")[0];
                            ArrayList<String> path = new ArrayList<>(Arrays.asList(requestData.split(" ")[1].split("[?]")[0].split("/")));
                            HashMap<String, String> query = new HashMap<String, String>();
                            if (requestData.split(" ")[1].split("[?]").length > 1) {
                                for (String str : requestData.split(" ")[1].split("[?]")[1].split("&")) {
                                    if (str.split("=").length > 1) {
                                        query.put(str.split("=")[0], str.split("=")[1]);
                                    }
                                }
                            }
                            HashMap<String, String> post = new HashMap<String, String>();
                            if (requestData.split("\r\n\r\n").length > 1) {
                                for (String str : requestData.split("\r\n\r\n")[1].split("[&]")) {
                                    if (str.split("=").length > 1) {
                                        post.put(str.split("=")[0], str.split("=")[1]);
                                    }
                                }
                            }
                            System.out.println(requestData + "\n" + method + "\n" + path + "\n" + query + "\n" + post + "\n" + "\n");

                            String responseMessage = null;

                            ArrayList<String> equal = new ArrayList<>(path.size());
                            if (path.size() < 2) {
                                responseMessage = """
                                        <!DOCTYPE html>
                                        <html lang="ko">                    
                                        <head>
                                        	<meta charset="UTF-8">
                                        	<title>HTML Forms</title>
                                        </head>                 
                                        <body>                   
                                        	<h1>GET 로그인 방식</h1>
                                        	<form action="/login" method="get">
                                        		사용자 : <br>
                                        		<input type="text" name="username"><br>
                                        		비밀번호 : <br>
                                        		<input type="password" name="password">
                                        		<input type="submit" value="로그인">
                                        	</form>
                                        	<h1>POST 로그인 방식</h1>
                                        	<form action="/login" method="post">
                                        		사용자 : <br>
                                        		<input type="text" name="username"><br>
                                        		비밀번호 : <br>
                                        		<input type="password" name="password">
                                        		<input type="submit" value="로그인">
                                        	</form>                  
                                        </body>         
                                        </html>""";
                            } else if (path.get(1).equals("login")) {
                                String username = "";
                                String password = "";
                                switch (method) {
                                    case "GET":
                                        username = query.get("username");
                                        password = query.get("password");
                                        break;
                                    case "POST":
                                        username = post.get("username");
                                        password = post.get("password");
                                        break;
                                    default:
                                        break;
                                }
                                responseMessage = """
                                        <!DOCTYPE html>
                                        <html lang="ko">                    
                                        <head>
                                        	<meta charset="UTF-8">
                                        	<title>HTML Forms</title>
                                        </head>                 
                                        <body>                   
                                        	<h1>${method} 로그인 방식</h1> 
                                        	사용자 : ${username}<br>
                                        	비밀번호 : ${password}<br> 
                                        	<button type="button" onclick = "location.href = '/' ">메인으로 이동</button>             
                                        </body>         
                                        </html>""".replaceAll("\\$\\{method}", method).replaceAll("\\$\\{username}", username).replaceAll("\\$\\{password}", password);
                            }


                            String responseData = "HTTP/1.1 200 OK\r\n" +
                                    "Server: Asal\r\n" +
                                    "Content-type: text/html\r\n\r\n" +
                                    responseMessage;
                            outputStream.write(responseData.getBytes());


                            System.out.println(clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " Client disconnected!");

                            inputStream.close();
                            outputStream.close();
                            clientSocket.close();
                        } catch (Exception e) {

                        }
                    }
                }).start();


            }
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
