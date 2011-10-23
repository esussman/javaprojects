import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JComboBox;


public class IMAP extends JFrame {

	private JPanel contentPane;
	private JTextField txtHostAddress;
	private JTextField txtPortNumber;
	private JTextField txtUserName;
	private JTextField txtPassword;
	private JLabel lblWrite;
	private JButton btnClose;
	private ArrayList<Credentials> userLogs;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					IMAP frame = new IMAP();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public IMAP() {
		userLogs = new ArrayList<Credentials>();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblHostAddress = new JLabel("Host Address");
		
		txtHostAddress = new JTextField();
		txtHostAddress.setColumns(10);
		
		JLabel lblPortNumber = new JLabel("Port Number");
		
		JLabel lblUsername = new JLabel("UserName");
		
		JLabel lblPassword = new JLabel("Password");
		
		txtPortNumber = new JTextField();
		txtPortNumber.setColumns(10);
		
		txtUserName = new JTextField();
		txtUserName.setColumns(10);
		
		txtPassword = new JTextField();
		txtPassword.setColumns(10);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				int port = -1;
				Credentials c = new Credentials();
				
				String username = txtUserName.getText();
				String password = txtPassword.getText();
				String hostname = txtHostAddress.getText();
			
				
				try
				{
					port = Integer.parseInt(txtPortNumber.getText());
				}
				catch(NumberFormatException e)
				{
					JOptionPane.showMessageDialog(null, "Port must be a number.");
				}
				if(username.isEmpty() || password.isEmpty() || hostname.isEmpty())
					JOptionPane.showMessageDialog(null, "A field was left blank. Please enter in all fields.");
				else if(port < 0)
					JOptionPane.showMessageDialog(null, "Please enter a port.");
				else
				{
					c.setHostname(hostname);
					c.setPassword(password);
					c.setUsername(username);
					c.setPort(port);
					userLogs.add(c);
					SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
					SSLSocket sslsocket;
			        try {
						sslsocket = (SSLSocket) sslsocketfactory.createSocket(hostname, port);
						
				        InputStream inputstream = sslsocket.getInputStream();
			            InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
			            BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
						
			            
			            String login = ". login " + username + " " + password + "\n";
						OutputStream outputstream = sslsocket.getOutputStream();
						outputstream.write(login.getBytes());
			            OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream);
			            BufferedWriter bufferedwriter = new BufferedWriter(outputstreamwriter);
			            bufferedwriter.write(login);
			     
			            char[] f = new char[1024];
			            bufferedreader.read(f);
			            String message;
			            message = String.valueOf(f);
			        	JOptionPane.showMessageDialog(null,message);
			            sslsocket.close();
			            
					} catch (UnknownHostException e) 
					{
						e.printStackTrace();
					} catch (IOException e) {
					
						e.printStackTrace();
					}

				}
			}
		});
		
		lblWrite = new JLabel("");
		
		btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				 BufferedWriter out;
				try {
					out = new BufferedWriter(new FileWriter("login.txt"));
					for(int i = 0; i < userLogs.size(); i++)
					{
						out.write(userLogs.get(i).getHostname()+",");
						out.write(userLogs.get(i).getPort()+",");
						out.write(userLogs.get(i).getUsername()+",");
						out.write(userLogs.get(i).getPassword());
						if(i != userLogs.size() - 1)
							out.write("\n");
					}
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
				
				
				
				System.exit(0);
				}
		});
		
		final JComboBox cbCredentials = new JComboBox();
		cbCredentials.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				JComboBox cb = (JComboBox)arg0.getSource();
				if(cb.getSelectedIndex() != 0)
				{
					txtHostAddress.setText(userLogs.get(cb.getSelectedIndex()-1).getHostname());
					txtPortNumber.setText(""+userLogs.get(cb.getSelectedIndex()-1).getPort());
					txtUserName.setText(userLogs.get(cb.getSelectedIndex()-1).getUsername());
					txtPassword.setText(userLogs.get(cb.getSelectedIndex()-1).getPassword());
				}
				else
				{
					txtHostAddress.setText("");
					txtPortNumber.setText("");
					txtUserName.setText("");
					txtPassword.setText("");
				}
			}
		});
		cbCredentials.insertItemAt("NONE", 0);
		cbCredentials.setSelectedIndex(0);
		File loginfile = new File("login.txt");
		if(loginfile.exists())
		{
			try{
				  // Open the file that is the first 
				  // command line parameter
				  FileInputStream fstream = new FileInputStream("login.txt");
				  // Get the object of DataInputStream
				  DataInputStream in = new DataInputStream(fstream);
				  BufferedReader br = new BufferedReader(new InputStreamReader(in));
				  String strLine;
				  //Read File Line By Line
				  while ((strLine = br.readLine()) != null)   {
					  //Hostname,port,username,password
					 Credentials c = new Credentials();
					 String data[] = new String[4];
					 data = strLine.split(",");
					 c.setHostname(data[0]);
					 c.setPort(Integer.parseInt(data[1]));
					 c.setUsername(data[2]);
					 c.setPassword(data[3]);
					 cbCredentials.insertItemAt(data[0] + " , " + data[2], userLogs.size()+1);
					 userLogs.add(c);
				  }
				  //Close the input stream
				  in.close();
				    }catch (Exception e){//Catch exception if any
				  System.err.println("Error: " + e.getMessage());
			}
			
		} else
		{
			try {
				loginfile.createNewFile();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(19)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblWrite, GroupLayout.PREFERRED_SIZE, 357, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addComponent(lblPortNumber)
										.addComponent(lblHostAddress)
										.addComponent(lblUsername)
										.addComponent(lblPassword, GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE))
									.addPreferredGap(ComponentPlacement.RELATED))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(btnConnect)
									.addGap(1)))
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(btnClose)
								.addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtUserName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtPortNumber, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(txtHostAddress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(cbCredentials, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE)))
							.addContainerGap(70, Short.MAX_VALUE))))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblHostAddress)
						.addComponent(txtHostAddress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(cbCredentials, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPortNumber)
						.addComponent(txtPortNumber, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUsername)
						.addComponent(txtUserName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPassword)
						.addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnConnect)
						.addComponent(btnClose))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblWrite, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(17, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}
}
