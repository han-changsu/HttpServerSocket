import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class HttpServerSocket {
    public static void main(String[] args)
    {
        System.out.println("Server has started on 127.0.0.1:80 \nWaiting for a connection...");
        try {
            ServerSocket socket = new ServerSocket(80);
            for (int i = 0; i < 10; i++) {
                Socket client = socket.accept();
                System.out.println("Client connected!");

                InputStream in = client.getInputStream();
                OutputStream out = client.getOutputStream();

                Scanner s = new Scanner(in,"UTF-8");

                String requestData = "";
                System.out.println(s);
                if(s.hasNextLine()){
                    requestData = s.nextLine();
                }

                ArrayList<String> requestList = new ArrayList<>(Arrays.asList(requestData.split(" ")));
                String method = requestList.get(0);
                ArrayList<String> requestUrl = new ArrayList<>(Arrays.asList(requestList.get(1).split("\\?")));
                String requestRoute = requestUrl.get(0);

                String responseMessage = null;
                if(requestRoute.replaceAll("/","").equals("")){
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
                }
                else if (requestRoute.equals("/login")) {
                    String username="";
                    String password="";
                    switch (method){
                        case "GET":
                            ArrayList<String> requestGet = new ArrayList<>(Arrays.asList((requestUrl.size()>1?requestUrl.get(1):"").split("&")));
                            for (int j = 0; j < requestGet.size(); j++) {
                                ArrayList<String> labalList = new ArrayList<>(Arrays.asList(requestGet.get(j).split("=")));
                                if(labalList.get(0).equals("username"))username = labalList.get(1);
                                else if(labalList.get(0).equals("password"))password = labalList.get(1);
                            }
                            break;
                        case "POST":
                            while(s.hasNextLine()&(requestData=s.nextLine()).equals("")){System.out.println(requestData);}
                            requestData = s.nextLine();
                            System.out.println(requestData);
                            ArrayList<String> requestPost = new ArrayList<>(Arrays.asList(requestData.split("&")));
                            for (int j = 0; j < requestPost.size(); j++) {
                                System.out.println(requestPost.get(j));
                                ArrayList<String> labalList = new ArrayList<>(Arrays.asList(requestPost.get(j).split("=")));
                                if(labalList.get(0).equals("username"))username = labalList.get(1);
                                else if(labalList.get(0).equals("password"))password = labalList.get(1);
                            }
                            break;
                        default:
                            break;
                    }
                    responseMessage="""
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
                            </html>""".replaceAll("\\$\\{method}",method).replaceAll("\\$\\{username}",username).replaceAll("\\$\\{password}",password);
                }else if (requestRoute=="/logout") {

                }else if (requestRoute=="/login") {

                }
                String responseData = "HTTP/1.1 200 OK\r\n" +
                        "Server: Asal\r\n" +
                        "Content-type: text/html\r\n\r\n" +
                        responseMessage;
                out.write(responseData.getBytes());

                System.out.println("end");

                s.close();
                in.close();
                out.close();
                client.close();
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
