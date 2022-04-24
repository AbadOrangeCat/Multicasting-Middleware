import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;

public class Middleware {
	public static int count = 0;

	public static void main(String[] args) throws IOException {
		System.out.println("Middleware 2");
		JFrame frame = new JFrame();
		frame.setTitle("Middleware 2");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 400);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		JButton jb = new JButton("Send");
		JList alist1 = new JList();
		JList alist2 = new JList();
		JList alist3 = new JList();

		JPanel ptotal = new JPanel();

		JPanel psent = new JPanel();
		BoxLayout boxlayout2 = new BoxLayout(psent, BoxLayout.Y_AXIS);
		psent.setLayout(boxlayout2);
		JLabel t1 = new JLabel("Sent:");
		JPanel insent = new JPanel();
		insent.setPreferredSize(new Dimension(230, 200));
		BoxLayout boxlayout1 = new BoxLayout(insent, BoxLayout.Y_AXIS);
		insent.setLayout(boxlayout1);
		JScrollPane jScrollinsent = new JScrollPane(alist1);
		jScrollinsent.setLayout(new ScrollPaneLayout());
		jScrollinsent.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		psent.add(t1);
		psent.add(insent);
		insent.add(jScrollinsent);

		JPanel preceive = new JPanel();
		BoxLayout boxlayout3 = new BoxLayout(preceive, BoxLayout.Y_AXIS);
		preceive.setLayout(boxlayout3);
		JLabel tr = new JLabel("Received:");
		JPanel inreceive = new JPanel();
		inreceive.setPreferredSize(new Dimension(230, 200));
		BoxLayout boxlayout4 = new BoxLayout(inreceive, BoxLayout.Y_AXIS);
		inreceive.setLayout(boxlayout4);
		JScrollPane jScrollinsent2 = new JScrollPane(alist2);
		jScrollinsent2.setLayout(new ScrollPaneLayout());
		jScrollinsent2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		preceive.add(tr);
		preceive.add(inreceive);
		inreceive.add(jScrollinsent2);

		JPanel pready = new JPanel();
		BoxLayout boxlayout5 = new BoxLayout(pready, BoxLayout.Y_AXIS);
		pready.setLayout(boxlayout5);
		JLabel tready = new JLabel("Ready:");

		JPanel inready = new JPanel();
		inready.setPreferredSize(new Dimension(230, 200));
		BoxLayout boxlayout6 = new BoxLayout(inready, BoxLayout.Y_AXIS);
		inready.setLayout(boxlayout6);
		JScrollPane jScrollinsent3 = new JScrollPane(alist3);
		jScrollinsent3.setLayout(new ScrollPaneLayout());
		jScrollinsent3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		pready.add(tready);
		pready.add(inready);
		inready.add(jScrollinsent3);

		ArrayList<String> sentlist = new ArrayList<String>();
		ArrayList<String> receivedlist = new ArrayList<String>();
		ArrayList<String> readylist = new ArrayList<String>();
		
		int[] dfip = new int[] { 8082, 8083, 8084, 8085, 8086 };

		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				addone();
				String msg = "Msg #" + count + " from Middleware 2  <EOM>";
				sentlist.add(msg);
				Object[] msgname = sentlist.toArray();
				alist1.setListData(msgname);
				frame.validate();
			

				try {

					InetAddress ip = InetAddress.getLocalHost();
					Socket s = new Socket(ip, 8081);
					DataOutputStream dataOutputStream = new DataOutputStream(s.getOutputStream());

					byte[] outmsg = msg.getBytes();
					dataOutputStream.write(outmsg);
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		ptotal.add(psent);
		ptotal.add(preceive);
		ptotal.add(pready);

		frame.add(jb);
		frame.add(ptotal);
		frame.setVisible(true);

		int timestap = 1;

		Map<String, Integer> time = new HashMap<String, Integer>();
		Map<Integer, ArrayList<String>> ggitem = new HashMap<Integer, ArrayList<String>>();
		
		Map<String, ArrayList<Integer>> save = new HashMap<String, ArrayList<Integer>>();
		ArrayList<String> atry = new ArrayList<String>();
		

		ServerSocket receive = new ServerSocket(8083);
		while (true) {
			try {

				Socket received = receive.accept();
				DataInputStream receiveinput = new DataInputStream(received.getInputStream());

				String content = "";

				while (true) {
					byte msgreceive = receiveinput.readByte();
					char convert = (char) msgreceive;
					content += convert;
					if (content.contains("<EOM>")) {
						break;
					}
				}
				
				//System.out.println(content);


				if (content.contains("Msg")) {
					receivedlist.add(content);
					Object[] msgre = receivedlist.toArray();
					alist2.setListData(msgre);
					frame.validate();


					time.put(content, timestap);
					
					
					if(ggitem.containsKey(timestap)) {
						ggitem.get(timestap).add(content);
					}else {
						ArrayList<String> newitem = new ArrayList<String>();
						newitem.add(content);
						ggitem.put(timestap, newitem);
					}


					String newcon = content.substring(4, content.indexOf("<"));
					String checknum = content.substring(content.indexOf("Middleware") + 11, content.indexOf("<") - 2);
					int num = Integer.parseInt(checknum) - 1;

					

					try {
						String msg = "Che ?" +newcon+ "!" + timestap + "<EOM>";
						InetAddress ip = InetAddress.getLocalHost();
						Socket s = new Socket(ip, dfip[num]);
						DataOutputStream dataOutputStream = new DataOutputStream(s.getOutputStream());

						byte[] outmsg = msg.getBytes();
						dataOutputStream.write(outmsg);
						s.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					timestap += 1;
				}
				
				if (content.contains("Che")) {
					String tempname = content.substring(content.indexOf("?")+1, content.indexOf("!"));
					String mescontent = content.substring(content.indexOf("!")+1, content.indexOf("<"));
					int newtime = Integer.parseInt(mescontent);
					
					if(save.containsKey(tempname)) {
						ArrayList<Integer> temp = save.get(tempname);
						temp.add(newtime);
						
						if(temp.size() ==5) {
							int maxx = Collections.max(temp);
							
							String boatca = "Upd ?" + tempname + "!" + maxx + "<EOM>";
							try {

								InetAddress ip = InetAddress.getLocalHost();
								Socket s = new Socket(ip, 8081);
								DataOutputStream dataOutputStream = new DataOutputStream(s.getOutputStream());

								byte[] outmsg = boatca.getBytes();
								dataOutputStream.write(outmsg);
								s.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							
						}else {
							save.put(tempname, temp);
						}
					}else {
						ArrayList<Integer> temp = new ArrayList<Integer>();
						temp.add(newtime);
						save.put(tempname, temp) ;
					}
				}
				
				if (content.contains("Upd")) {
					String item =  "Msg " + content.substring(content.indexOf("?")+1, content.indexOf("!")) + "<EOM>";
					String position = content.substring(content.indexOf("!")+1, content.indexOf("<"));
					int getposition = Integer.parseInt(position);
					
					atry.add(item);
					
					int itemindex = time.get(item);
					
					System.out.println(getposition);
					System.out.println(atry);
					System.out.println(time);
					System.out.println(ggitem);
					
					if(ggitem.get(itemindex).contains(item)) {
						int itempos = ggitem.get(itemindex).indexOf(item);
						ggitem.get(itemindex).remove(itempos);
						
						if(ggitem.containsKey(getposition)) {
							ggitem.get(getposition).add(item);
						}else {
							ArrayList<String> temp = new ArrayList<String>();
							temp.add(item);
							ggitem.put(getposition, temp) ;
						}
					}

					
					boolean x = true;
					
			    	for ( Integer key : ggitem.keySet() ) {
			    		ArrayList<String> finalcheck = ggitem.get(key);
			    		Collections.sort(finalcheck);  
			    		
			    		if(x) {
			    			if(!finalcheck.isEmpty()) {
			    				for(int i=0;i<finalcheck.size();i++) {
			    					if(!atry.contains(finalcheck.get(i))) {
			    						x = false;
			    					}
			    				}
			    			}
			    		}
			    		
			    		if(x) {
		    				for(int i=0;i<finalcheck.size();i++) {
		    					//System.out.println(finalcheck.get(i));
		    					if(!readylist.contains(finalcheck.get(i))) {
		    						readylist.add(finalcheck.get(i));
		    					}
		    				}
			    		}
			    	
			    	}
					Object[] finallist = readylist.toArray();
					alist3.setListData(finallist);
			    	frame.validate();
			    	System.out.println(readylist);
				}

				//System.out.println(time);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void addone() {
		count += 1;
	}

}
