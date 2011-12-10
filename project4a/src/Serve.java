import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
/*
 * Update by Alisa Neeman 7/16/2002
 * Added individual action listeners to
 * buttons and window event listener to frame
 */

public class Serve extends Frame implements Runnable {
    Serve()
    {
        setTitle("Server Program");
        Panel p = new Panel();
        portInput = new TextField(String.valueOf(port),4);
        startButton = new Button("Start Server");
        stopButton = new Button("Stop-Resume Server");
        p.add(new Label("Port: "));
        p.add(portInput);
        p.add(startButton);
        p.add(stopButton);

        stopButton.addActionListener (new ActionListener()
            {
                public void actionPerformed(ActionEvent e){
                    stopServer();
                }
            });

        startButton.addActionListener (new ActionListener()
            {
                public void actionPerformed(ActionEvent e){
                    startServer();
                }
            });

        add("North",p);

        addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                    dispose();
                    System.exit(0);
                }
            });

        a = new TextArea(50,30);
        add("Center",a);
        setSize(300,300);
        //resize(300,300);
        //show();
        setVisible(true);
    }

    public void run() {
        port=Integer.parseInt(portInput.getText());
        display("Server Started on Port: " + port + "\n");

        try {
            ss = new ServerSocket(port);
            while(!threadSuspended) 
            {
                display("Listening again ** port: " + port + "\n");
                Socket s = ss.accept();
                    new Thread(new FileRequest(s,this)).start();
                    display("Got a file request... \n");
            }

        }  catch (Exception e) { display ("$$$$ Exception " + e + "\n");}
    }

    public void startServer(){

        if(runner==null) {
            runner = new Thread(this);
            threadSuspended = false;
            runner.start();
            portInput.setEditable(false);
        } 
    }

    public synchronized void stopServer()
    {
        
    	threadSuspended = !threadSuspended;
    	if(threadSuspended)
    		display("Server Stopped by User... \n");
    	else
    		display("Server resumed by User... \n");
        portInput.setEditable(true);
    }
    
    public synchronized void display(String text) {
        //a.appendText(text);
    	a.append(text);
    	System.out.println(text);
    }

    public static void main(String[] args) {
        new Serve();
    }
    
    
    private boolean threadSuspended;
    private ServerSocket ss;
    private TextArea a;
    private int port = 8080;
    private TextField portInput;
    private Button startButton, stopButton;
    private volatile Thread runner=null;
}
