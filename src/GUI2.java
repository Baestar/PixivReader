import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class GUI2 {

	//fields
	JFrame jFrame;
	PixivReader pR;
	Thread process;
	
	//constructors
	GUI2()
	{
		jFrame = new JFrame("Pixiv Reader");
		jFrame.setSize(200, 300);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	//methods
	
	public static void main(String args[])
	{
		GUI2 gui = new GUI2();
		gui.loginPage();
		
	}
	private void loginPage()
	{
		jFrame.setLayout(new GridBagLayout());
		jFrame.setMinimumSize(new Dimension(100,200));
		JLabel title = new JLabel("Pixiv Reader");
		JLabel lblUser = new JLabel("Pixiv Username");
		JLabel lblPass = new JLabel("Pixiv Password");
		JLabel lblPath = new JLabel("Filepath (Optional)");
		JTextField boxUser = new JTextField(20);
		JPasswordField boxPass = new JPasswordField(20); //TODO Make password work
		JTextField boxPath = new JTextField(20);
		
		JButton submit = new JButton("Submit Login");
			submit.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					
					jFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					pR = new PixivReader(boxUser.getText(), String.valueOf(boxPass.getPassword()), boxPath.getText());
					process = new Thread(pR);
					process.start();
					jFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					dataPage();
					
					
				}
			});
		
		
		GridBagConstraints cons0 = new GridBagConstraints(); 
			cons0.gridx = 0;
			cons0.gridy = 0;
			cons0.gridwidth = GridBagConstraints.REMAINDER; 
		GridBagConstraints cons1 = new GridBagConstraints(); 
			cons1.gridx = 0;
			cons1.gridy = 3;
			cons1.gridwidth = GridBagConstraints.REMAINDER;
		GridBagConstraints cons2 = new GridBagConstraints(); 
			cons2.gridx = 0;
			cons2.gridy = 4;
			cons2.fill = GridBagConstraints.HORIZONTAL;
			cons2.gridwidth = GridBagConstraints.REMAINDER;
		GridBagConstraints cons3 = new GridBagConstraints();
			cons3.gridx = 0;
			cons3.gridy = 5;
			cons3.gridwidth = GridBagConstraints.REMAINDER;
		GridBagConstraints cons4 = new GridBagConstraints(); 
			cons4.gridx = 0;
			cons4.gridy = 6;
			cons4.fill = GridBagConstraints.HORIZONTAL;
			cons4.gridwidth = GridBagConstraints.REMAINDER;
		GridBagConstraints cons5 = new GridBagConstraints();
			cons5.gridx = 0;
			cons5.gridy = 8;
			cons5.gridwidth = GridBagConstraints.REMAINDER;
		GridBagConstraints cons6 = new GridBagConstraints(); 
			cons6.gridx = 0;
			cons6.gridy = 9;
			cons6.fill = GridBagConstraints.HORIZONTAL;
			cons6.gridwidth = GridBagConstraints.REMAINDER;
		GridBagConstraints cons7 = new GridBagConstraints();
			cons7.gridx = 0;
			cons7.gridy = 11;
			cons7.gridwidth = GridBagConstraints.REMAINDER;
		
		jFrame.add(title, cons0);
		jFrame.add(lblUser,cons1);
		jFrame.add(boxUser,cons2);
		jFrame.add(lblPass,cons3);
		jFrame.add(boxPass,cons4);
		jFrame.add(lblPath,cons5);
		jFrame.add(boxPath,cons6);
		jFrame.add(submit,cons7);
		
		
		jFrame.setVisible(true);
	}
	private void dataPage()
	{
		jFrame.getContentPane().removeAll();
		jFrame.getContentPane().repaint();
		jFrame.setLayout(new BorderLayout());
		jFrame.setSize(500, 200);
		jFrame.setMinimumSize(new Dimension(450,200));
		
		Container leftPane = new Container();
			leftPane.setLayout(new GridLayout(0,1));
		
		Container rightPane = new Container();
			rightPane.setLayout(new GridLayout(0,2));
		Container buttonPane = new Container();
			buttonPane.setLayout(new GridLayout(0,1));
		
		
		JRadioButton single = new JRadioButton("Single");
		JRadioButton manga = new JRadioButton("Manga");
		JRadioButton page = new JRadioButton("Page");
		JRadioButton artist = new JRadioButton("Artist");
		JRadioButton path = new JRadioButton("Change Path");
		ButtonGroup group = new ButtonGroup();
		group.add(single);
		group.add(manga);
		group.add(page);
		group.add(artist);
		group.add(path);
		
		JLabel title = new JLabel("Fetch Pictures");
			title.setHorizontalAlignment(SwingConstants.CENTER);
			
			
			
		JLabel urlLbl = new JLabel("URL");
		JLabel useridLbl = new JLabel("User ID");
		JLabel artidLbl = new JLabel("Illustration ID");
		JLabel pathLbl = new JLabel("Destination Filepath");
			
		JTextField urlText = new JTextField(15);
			urlText.setEnabled(false);
		JTextField useridText = new JTextField(15);
			useridText.setEnabled(false);
		JTextField artidText = new JTextField(15);
			artidText.setEnabled(false);
		JTextField pathText = new JTextField(15);
			pathText.setEnabled(false);
			
		JButton submit = new JButton("Add To Queue");
			submit.setEnabled(false);
		JButton end = new JButton("Close When Done");
		
		single.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				urlText.setEnabled(true);
				useridText.setEnabled(false);
				artidText.setEnabled(true);
				pathText.setEnabled(false);
				submit.setEnabled(true);
			}
		});
		manga.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				urlText.setEnabled(true);
				useridText.setEnabled(false);
				artidText.setEnabled(true);
				pathText.setEnabled(false);
				submit.setEnabled(true);
			}
		});
		page.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				urlText.setEnabled(true);
				useridText.setEnabled(false);
				artidText.setEnabled(false);
				pathText.setEnabled(false);
				submit.setEnabled(true);
			}
		});
		artist.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				urlText.setEnabled(true);
				useridText.setEnabled(true);
				artidText.setEnabled(false);
				pathText.setEnabled(false);
				submit.setEnabled(true);
			}
		});
		path.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				urlText.setEnabled(false);
				useridText.setEnabled(false);
				artidText.setEnabled(false);
				pathText.setEnabled(true);
				submit.setEnabled(true);
			}
		});
		
		
		submit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(single.isSelected())
				{
					String artid = artidText.getText();
					String url = urlText.getText();
					if(artid.length() != 0)
					{
						pR.addToFetch("s" + artid);
						dataPage();
					}
					else if(url.length() != 0)
					{
						pR.addToFetch("s" + url);
						dataPage();
					}
					else
					{
						//no data entered
					}
				}
				else if(manga.isSelected())
				{
					String artid = artidText.getText();
					String url = urlText.getText();
					if(artid.length() != 0)
					{
						pR.addToFetch("m" + artid);
						dataPage();
					}
					else if(url.length() != 0)
					{
						pR.addToFetch("m" + url);
						dataPage();
					}
					else
					{
						//no data entered
					}
				}
				else if(page.isSelected())
				{
					String url = urlText.getText();
					if(url.length() !=0)
					{
						pR.addToFetch("p" + url);
						dataPage();
					}
					else
					{
						//no data entered
					}
				}
				else if(artist.isSelected())
				{
					String userid = useridText.getText();
					String url = urlText.getText();
					if(userid.length() != 0)
					{
						pR.addToFetch("a" + userid);
						dataPage();
					}
					else if(url.length() != 0)
					{
						pR.addToFetch("a" + url);
						dataPage();
					}
					else
					{
						//no data entered
					}

				}
				else if(path.isSelected())
				{
					String newPath = pathText.getText();
					if(newPath.length() != 0)
					{
						pR.addToFetch("f" + newPath);
						dataPage();
					}
					else
					{
						//no data entered
					}
				}
				else
				{
					//no button selected
				}
			}
		});
		end.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				process.interrupt();
				lastPage();
			}
		});
		
			
		

		
		
		leftPane.add(single);
		leftPane.add(manga);
		leftPane.add(page);
		leftPane.add(artist);
		leftPane.add(path);
		
		rightPane.add(urlText);
		rightPane.add(urlLbl);
		rightPane.add(useridText);
		rightPane.add(useridLbl);
		rightPane.add(artidText);
		rightPane.add(artidLbl);
		rightPane.add(pathText);
		rightPane.add(pathLbl);
		
		
		buttonPane.add(submit);
		buttonPane.add(end);
		
		jFrame.add(buttonPane, BorderLayout.EAST);
		jFrame.add(rightPane, BorderLayout.CENTER);
		jFrame.add(leftPane, BorderLayout.WEST);
		jFrame.add(title, BorderLayout.NORTH);
		jFrame.getContentPane().validate();
	}
	synchronized private void lastPage()
	{
		System.out.println("endTest");
		jFrame.getContentPane().removeAll();
		jFrame.getContentPane().repaint();
		jFrame.setLayout(new BorderLayout());
		jFrame.setSize(400, 200);
		JProgressBar bar = new JProgressBar();
			bar.setIndeterminate(true);
		jFrame.add(bar, BorderLayout.CENTER);
		jFrame.getContentPane().validate();//TODO Not displaying last page.
		
		while(process.isAlive())
		{
			try {
				Thread.sleep(1000); //TODO can't exit program during this join
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		System.exit(0);
		
		
	}
}
