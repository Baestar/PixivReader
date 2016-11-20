import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import com.jaunt.JauntException;

public class GUI extends JFrame implements ActionListener{

	//fields
	PixivReader pR;
	int page;
	Vector<String> v = new Vector<String>(30);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public static void main(String[] args) 
	{
		GUI frame = new GUI();
		frame.page1();
		
		

	}
	private GUI()
	{
		super("Pixiv Reader");
		setSize(400,200);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
		case "Next":			next();
								break;
		case "Back":			back();
								break;
		case "Add Instrution":	add();
								break;
		case "Get Pictues":		finish();
								break;
		case "Add":				addData();
								break;
								
		}
	}
	private void page1()
	{
		page = 1;
		setRootPane(new JRootPane());
		setLayout(new GridLayout(0,2));
		JLabel lblUN = new JLabel("Pixiv Username");
		JLabel lblPW = new JLabel("Pixiv Password");
		JLabel lblFP = new JLabel("Output Filepath (Optional)");
		JTextField userName = new JTextField(20);
		JTextField password = new JTextField(20);
		JTextField filePath = new JTextField(20);
		JButton next = new JButton("Next");
		JButton back = new JButton("Back");
		back.setEnabled(false);
		add(lblUN);
		add(userName);
		add(lblPW);
		add(password);
		add(lblFP);
		add(filePath);
		add(back);
		add(next);
		next.addActionListener(this);
		setVisible(true);
	}
	private void page2()
	{
		page = 2;
		setRootPane(new JRootPane());
		setLayout(new GridLayout(0,2));
		JRadioButton btn1 = new JRadioButton("Single");
		btn1.setSelected(true);
		JRadioButton btn2 = new JRadioButton("Manga");
		JRadioButton btn3 = new JRadioButton("Page");
		JRadioButton btn4 = new JRadioButton("Artist");
		JRadioButton btn5 = new JRadioButton("Change Directory");
		ButtonGroup radiobtns = new ButtonGroup();
		radiobtns.add(btn1);
		radiobtns.add(btn2);
		radiobtns.add(btn3);
		radiobtns.add(btn4);
		radiobtns.add(btn5);
		add(btn1);
		add(btn2);
		add(btn3);
		add(btn4);
		add(btn5);
		JButton add = new JButton("Add Instrution");
		JButton back = new JButton("Back");
		JButton finish = new JButton("Get Pictues");
		add(add);
		add(back);
		add(finish);
		add.addActionListener(this);
		back.addActionListener(this);
		finish.addActionListener(this);
		setVisible(true);
		
		
	}
	private void next()
	{
		if(page == 1)
		{
			Container content = getRootPane().getContentPane();
			String uN = ((JTextComponent)(content.getComponent(1))).getText();
			String pW = ((JTextComponent)(content.getComponent(3))).getText();
			String fP = ((JTextComponent)(content.getComponent(5))).getText();

			pR = new PixivReader(uN, pW, fP);
			page2();
		}
	}
	private void back()
	{
		if(page == 2)
		{
			pR = null;
			page1();
		}
		if(page == 3)
		{
			v.remove(v.size()-1);
			page2();
		}
	}
	private void finish()
	{
		System.out.println(v.toString());
		try
		{
		for(int i = 0; i < v.size(); i++)
		{
			pR.parseArgs(v.get(i));
		}
		v.removeAllElements();
		}
		catch(JauntException e)
		{
			System.err.println(e);
		}
	}
	private void add()
	{
		Container content = getRootPane().getContentPane();
		if(((AbstractButton)(content.getComponent(0))).isSelected())//single
		{
			v.add("s");
			path("URL");
			return;
		}
		if(((AbstractButton)(content.getComponent(1))).isSelected())//manga
		{
			v.add("m");
			path("URL");
			return;
		}
		if(((AbstractButton)(content.getComponent(2))).isSelected())//page
		{
			v.add("p");
			path("URL");
			return;
		}
		if(((AbstractButton)(content.getComponent(3))).isSelected())//artist
		{
			v.add("a");
			path("URL");
			return;
		}
		//change dir
		v.add("f");
		path("Filepath");
		
	}
	private void path(String s)
	{
		page = 3;
		setRootPane(new JRootPane());
		setLayout(new GridLayout(0,2));
		JLabel lbl = new JLabel(s);
		JTextField data = new JTextField(20);
		add(lbl);
		add(data);
		JButton add = new JButton("Add");
		JButton back = new JButton("Back");
		add(back);
		add(add);
		back.addActionListener(this);
		add.addActionListener(this);
		setVisible(true);
	}
	private void addData()
	{
		Container content = getRootPane().getContentPane();
		String data = ((JTextComponent)(content.getComponent(1))).getText();
		int index = v.size()-1;
		v.set(index, v.get(index) + data);
		page2();
	}

}
