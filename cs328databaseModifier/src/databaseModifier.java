import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.sql.*;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.ListModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import java.awt.Label;
import java.awt.TextField;

public class databaseModifier extends JFrame {

	private JPanel contentPane;
	private JTextField txtClassNum;
	private JTextField txtClassTitle;
	private JTextField txtClassInstructor;
	private JTextField txtClassLocation;
	private DefaultListModel dlmClasses;
	private JTextField txtClassTime;
	private int[] classNumbers;
	private JTextField txtClassDepartment;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					databaseModifier frame = new databaseModifier();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void getClasses()
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:a.db");
			Statement stat = conn.createStatement();
			
			ResultSet rs = stat.executeQuery("select * from classes;");
			dlmClasses.clear();
			int count = 0;
			    while (rs.next()) 
			    {
			      count++;
			      String classDisplay = rs.getString("department") + " " + rs.getInt("course") + ", " + rs.getString("title");
			      dlmClasses.addElement(classDisplay);
			    }
			    conn.close();
			}
			catch(Exception ex)
			{
				System.out.println(ex.getMessage());
			}
	}
	/**
	 * Create the frame.
	 */
	public databaseModifier() 
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 540, 365);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblClassNumber = new JLabel("Class Number");
		lblClassNumber.setBounds(21, 48, 88, 16);
		
		txtClassNum = new JTextField();
		txtClassNum.setBounds(115, 42, 134, 28);
		txtClassNum.setColumns(10);
		
		JLabel lblInsertNewClass = new JLabel("Insert New Class");
		lblInsertNewClass.setBounds(21, 14, 105, 16);
		
		txtClassTitle = new JTextField();
		txtClassTitle.setBounds(115, 82, 134, 28);
		txtClassTitle.setColumns(10);
		
		txtClassInstructor = new JTextField();
		txtClassInstructor.setBounds(115, 236, 134, 28);
		txtClassInstructor.setColumns(10);
		
		txtClassLocation = new JTextField();
		txtClassLocation.setBounds(115, 162, 134, 28);
		txtClassLocation.setColumns(10);
		
		txtClassTime = new JTextField();
		txtClassTime.setBounds(115, 202, 134, 28);
		txtClassTime.setColumns(10);
		
		JLabel lblTitle = new JLabel("Title");
		lblTitle.setBounds(21, 88, 28, 16);
		
		JLabel lblInstructor = new JLabel("Instructor");
		lblInstructor.setBounds(21, 242, 62, 16);
		
		JLabel lblLocation = new JLabel("Location");
		lblLocation.setBounds(21, 168, 54, 16);
		
		JLabel lblTime = new JLabel("Time");
		lblTime.setBounds(21, 208, 31, 16);
		
		JButton btnInsert = new JButton("Insert");
		btnInsert.setBounds(6, 282, 80, 29);
		btnInsert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(txtClassTitle.getText().isEmpty() || txtClassInstructor.getText().isEmpty() || txtClassLocation.getText().isEmpty() ||
						txtClassNum.getText().isEmpty() || txtClassTime.getText().isEmpty() || txtClassDepartment.getText().isEmpty())
				{
					System.out.println("A field was left blank");
				}
				else
				{
					try
					{
						Class.forName("org.sqlite.JDBC");
						Connection conn = DriverManager.getConnection("jdbc:sqlite:a.db");
						Statement stat = conn.createStatement();
						//stat.execute("drop table classes");
						//stat.executeUpdate("create table classes(course number, department text, title text, instructor text, location text, time text)");
						PreparedStatement prep = conn.prepareStatement("insert into classes values (?, ?, ?, ?, ?, ?);");
					
						prep.setInt(1, Integer.parseInt(txtClassNum.getText()));
						prep.setString(2, txtClassDepartment.getText());
					    prep.setString(3, txtClassTitle.getText());
					    prep.setString(4, txtClassInstructor.getText());
					    prep.setString(5, txtClassLocation.getText());
					    prep.setString(6, txtClassTime.getText());
					    prep.addBatch();
					    
					    prep.executeBatch();
					    conn.setAutoCommit(true);
					    /*
					    ResultSet rs = stat.executeQuery("select * from classes;");
					    while (rs.next()) 
					    {
					      System.out.println("course number = " + rs.getInt("course"));
					      System.out.println("course department = " + rs.getString("department"));
					      System.out.println("Title = " + rs.getString("title"));
					      System.out.println("Instructor = " + rs.getString("instructor"));
					      System.out.println("Location = " + rs.getString("location"));
					      System.out.println("Time = " + rs.getString("time"));
					      System.out.println();
					    }
					    rs.close();
					    */
					    conn.close();
					    getClasses();
					    
					}
					catch(Exception ex)
					{
						System.out.println(ex.getMessage());
					}
				
				}
				
			}
		});
		
		JButton btnClose = new JButton("Close");
		btnClose.setBounds(456, 309, 79, 29);
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		dlmClasses = new DefaultListModel();
		JList listClasses = new JList(dlmClasses);
		listClasses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(listClasses);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(287, 47, 220, 183);
		contentPane.add(scrollPane);
		listClasses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		  MouseListener mouseListener = new MouseAdapter() {
		      public void mouseClicked(MouseEvent mouseEvent) {
		        JList theList = (JList) mouseEvent.getSource();
		        if (mouseEvent.getClickCount() == 2) {
		          int index = theList.locationToIndex(mouseEvent.getPoint());
		          ListModel dlm = theList.getModel();
		          String itemText = (String)dlm.getElementAt(index);
		          String department =   itemText.substring(0, itemText.indexOf(' '));
		          int number = Integer.parseInt(itemText.substring(itemText.indexOf(' ')+1, itemText.indexOf(',')));
		         
		        
		          if (index >= 0) 
		          {
		            int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this class from the database?");
		            if(answer == 0)
		            {
		            	try{
		            	Class.forName("org.sqlite.JDBC");
						Connection conn = DriverManager.getConnection("jdbc:sqlite:a.db");
						Statement stat = conn.createStatement();
						//stat.execute("drop table classes");
						//stat.executeUpdate("create table classes(course number, department text, title text, instructor text, location text, time text)");
						PreparedStatement prep = conn.prepareStatement("delete from classes where course = ? and department = ?;");
						prep.setInt(1, number);
						prep.setString(2, department);
						prep.addBatch();
						prep.executeBatch();
						
						
					    conn.setAutoCommit(true);
					    conn.close();
					    getClasses();
						
						
		            	}
		            	catch(Exception ex)
		            	{
		            		System.out.println(ex.getMessage());
		            	}
						
						getClasses();
		            }
		            else
		            {
		            	 System.out.println("NO");
		            }
		          }
		        }
		      }
		    };
		    listClasses.addMouseListener(mouseListener);
		
		contentPane.add(lblInsertNewClass);
		contentPane.add(lblClassNumber);
		contentPane.add(lblTitle);
		contentPane.add(lblInstructor);
		contentPane.add(lblLocation);
		contentPane.add(lblTime);
		contentPane.add(txtClassTitle);
		contentPane.add(txtClassNum);
		contentPane.add(txtClassInstructor);
		contentPane.add(txtClassLocation);
		contentPane.add(txtClassTime);
		contentPane.add(btnInsert);
		contentPane.add(btnClose);
		
		txtClassDepartment = new JTextField();
		txtClassDepartment.setBounds(115, 122, 134, 28);
		contentPane.add(txtClassDepartment);
		txtClassDepartment.setColumns(10);
		
		JLabel lblDepartment = new JLabel("Department");
		lblDepartment.setBounds(21, 128, 88, 16);
		contentPane.add(lblDepartment);
		
		JButton btnClearDatabase = new JButton("Clear Database");
		btnClearDatabase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to clear the database?");
	            if(answer == 0)
	            {
				try{
	            	Class.forName("org.sqlite.JDBC");
					Connection conn = DriverManager.getConnection("jdbc:sqlite:a.db");
					Statement stat = conn.createStatement();
		
					
					stat.executeUpdate("drop table if exists classes;");
					stat.executeUpdate("create table classes(course number, department text, title text, instructor text, location text, time text)");
					
				  
				    getClasses();
					
					
	            	}
	            	catch(Exception ex)
	            	{
	            		System.out.println(ex.getMessage());
	            	}
	            }
	
			}
		});
		btnClearDatabase.setBounds(356, 236, 154, 29);
		contentPane.add(btnClearDatabase);
		getClasses();
	}
}
