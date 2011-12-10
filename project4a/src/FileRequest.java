import java.io.*;
import java.net.*;
import java.util.*;

class FileRequest implements Runnable {

    FileRequest(Socket s, Serve app) {
    this.app=app;
    client = s;
    }

    public void run() {
        if(requestRead()) {
            if(fileOpened()) {
                constructHeader();
                if(fileSent()) {
                    app.display("*File: " + fileName + "File Transfer Complete * Bytes Sent: " + bytesSent + "\n");
                }
            }
        }
        try {
            dis.close();
            client.close();
        } catch (Exception e) { app.display ("Error closing Socket \n" + e); }
    }

    private boolean fileSent()
        {
            try {
                DataOutputStream clientStream = new DataOutputStream (new BufferedOutputStream (client.getOutputStream()));
                clientStream.writeBytes(header);
                app.display("******** File Request ******** \n" + "******** " + fileName + "******** \n" +header);
                int i;
                bytesSent = 0;
                    while ((i=requestedFile.read()) != -1) {
                        clientStream.writeByte(i);
                        bytesSent++;
                    }
                clientStream.flush();
                clientStream.close();

           } catch (IOException e) { return false; }
           return true;
       }

       private boolean fileOpened() {
           try {
               requestedFile = new DataInputStream(new BufferedInputStream(new FileInputStream("wwwroot/" + fileName)));
               fileLength = requestedFile.available();
               app.display(fileName + "is: " + fileLength + "bytes long... \n");
           } catch (FileNotFoundException e) {

               if(fileName.equals("filenfound.html")) { return false; }
               fileName="filenfound.html";
               if(!fileOpened()) { return false; }
           } catch (Exception e) { return false; }

      return true;
      }

      private boolean requestRead() {
          try {
              //Open InputStream and read(parse) the request
              dis = new DataInputStream(client.getInputStream());
              String line;
              while ((line=dis.readLine()) != null) {
                  StringTokenizer tokenizer = new StringTokenizer(line, " ");
                  if (!tokenizer.hasMoreTokens()) { break; }
                  if (tokenizer.nextToken().equals("GET")) {
                      fileName = tokenizer.nextToken();
                      if(fileName.equals("/")) {
                          fileName = "index.html";
                          } else {
                          fileName = fileName.substring(1);
                      }
                  }
              }

          }catch (Exception e) {
              return false;
          }
          app.display("Finished file request...");
          return true;
      }

      private void constructHeader() {
          String contentType;

          if((fileName.toLowerCase().endsWith(".jpg"))||(fileName.toLowerCase().endsWith(".jpeg"))||(fileName.toLowerCase().endsWith(".jpe")))
              { contentType = "image/jpg"; }
          else if((fileName.toLowerCase().endsWith(".gif")))
              { contentType = "image/gif"; }
          else if((fileName.toLowerCase().endsWith(".htm"))||(fileName.toLowerCase().endsWith(".html")))
              { contentType = "text/html"; }
          else if((fileName.toLowerCase().endsWith(".qt"))||(fileName.toLowerCase().endsWith(".mov")))
              { contentType = "video/quicktime"; }
          else if((fileName.toLowerCase().endsWith(".class")))
              { contentType = "application/octet-stream"; }
          else if((fileName.toLowerCase().endsWith(".mpg"))||(fileName.toLowerCase().endsWith(".mpeg"))||(fileName.toLowerCase().endsWith(".mpe")))
              { contentType = "video/mpeg"; }
          else if((fileName.toLowerCase().endsWith(".au"))||(fileName.toLowerCase().endsWith(".snd")))
              { contentType = "audio/basic"; }
          else if ((fileName.toLowerCase().endsWith(".wav")))
              { contentType = "audio/x-wave"; }
          else
              { contentType = "text/plain"; } //default

          header = "HTTP/1.0 200 OK\n" + "Allow: GET\n" +
          "MIME-Version: 1.0\n" + "Server: HMJ Basic HTTP Server\n" +
          "Content-Type: " + contentType + "\n" + "Content-Length: " +
          fileLength + "\n\n";
    }

    private Serve app;
    private Socket client;
    private String fileName, header;
    private DataInputStream requestedFile, dis;
    private int fileLength, bytesSent;

}
                









