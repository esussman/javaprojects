import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.*;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JList;


public class IMAP extends JFrame {

	private JPanel contentPane;
	private JTextField txtHostAddress;
	private JTextField txtPortNumber;
	private JTextField txtUserName;
	private JTextField txtPassword;
	private JLabel lblWrite;
	private JButton btnClose;
	private ArrayList<Credentials> userLogs;
	private JList listShowEmails;
	private DefaultListModel  emailContents;
	public PrintWriter out = null;
	public BufferedReader in = null;
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
		setBounds(100, 100, 626, 437);
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
				Thread split = new Thread()
				{
					public void run()
					{
						int port = -1;
						
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
						
							String response = "";
							Credentials c = new Credentials();
							c.setHostname(hostname);
							c.setPassword(password);
							c.setUsername(username);
							c.setPort(port);
							userLogs.add(c);
							
							Properties props = System.getProperties();
							props.setProperty("mail.store.protocol", "imaps");
								try {
									Session session = Session.getDefaultInstance(props, null);
									Store store = session.getStore("imaps");
									store.connect(hostname, username, password);

									Folder inbox = store.getFolder("Inbox");
									inbox.open(Folder.READ_ONLY);
									Message messages[] = inbox.getMessages();
									for(Message message:messages)
									{
										System.out.println(message.getMessageNumber());
										System.out.println(message.getSubject());
									}
									inbox.close(false);
									store.close();
							} catch (NoSuchProviderException e) {
								e.printStackTrace();
								System.exit(1);
							} catch (MessagingException e) {
								e.printStackTrace();
								System.exit(2);
							}
							
							
						}
					}
				};
				split.start();
					
				
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
		
		JScrollPane scrollPane = new JScrollPane();
		emailContents = new DefaultListModel();
		listShowEmails = new JList(emailContents);
		scrollPane.setViewportView(listShowEmails);
		listShowEmails.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(6)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(cbCredentials, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnConnect)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnClose, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 588, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblPortNumber)
									.addGap(12)
									.addComponent(txtPortNumber, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGap(6)
									.addComponent(lblPassword, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
									.addGap(6)
									.addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, 225, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblHostAddress)
									.addGap(6)
									.addComponent(txtHostAddress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGap(6)
									.addComponent(lblUsername)
									.addGap(6)
									.addComponent(txtUserName, GroupLayout.PREFERRED_SIZE, 225, GroupLayout.PREFERRED_SIZE)))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblWrite, GroupLayout.PREFERRED_SIZE, 357, GroupLayout.PREFERRED_SIZE))))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(6)
							.addComponent(lblHostAddress))
						.addComponent(txtHostAddress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(6)
							.addComponent(lblUsername))
						.addComponent(txtUserName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(13)
							.addComponent(lblPortNumber))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(7)
							.addComponent(txtPortNumber, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(13)
							.addComponent(lblPassword))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(7)
							.addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGap(6)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(cbCredentials, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnConnect)
						.addComponent(btnClose))
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(115)
							.addComponent(lblWrite, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(59)
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 218, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
		
	}
}
