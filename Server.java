package cse3040_fp_20181603;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/* "books.txt"�� ������ Books type�� ArrayList�� ���� */
class Books implements Comparable<Books>{
	private String title; //å�� ����
	private String author; //å�� ����
	private String borrower; //å�� �뿩�� ���
	
	public Books(String title, String author, String borrower) {
		this.title = title;
		this.author = author;
		this.borrower = borrower;
	}
	public String getTitle() {return this.title;}	
	public String getAuthor() {return this.author;}
	public String getBorrower() {return this.borrower;}

	public int compareTo(Books books) {
		return this.title.toLowerCase().compareTo(books.title.toLowerCase());
	}
	
	public String toString() {
		return this.title+"\t"+this.author+"\t"+this.borrower;
	}
}


public class Server{
	HashMap<String, DataOutputStream> clients; //�� ������ �����ϴ� clients�� hashmap���� ����
	
	Server(){
		clients = new HashMap<>();
		Collections.synchronizedMap(clients); //ó������ synchronized ������.
	}
	
	/* Read File 
	 * - "books.txt" ������ �о� Books type�� ArrayList�� �����Ѵ�. */
	public ArrayList<Books> readFile() {
		System.out.println("=============Book List -books.txt-=============");
		ArrayList<Books> list = new ArrayList<>();
		try{
			BufferedReader br = new BufferedReader(new FileReader("books.txt"));
			while(true) {
				String line = br.readLine();
				if(line == null) break;

				String[] results = line.split("\t");
				list.add(new Books(results[0], results[1], results[2]));
				
				System.out.println(line);
			}
			br.close();
			System.out.println("===============================================");
		} catch(IOException e) {
			System.out.println("file read error!");
			System.exit(0);
		}
		return list;
	}
	
	/* Write File
	 * - ArrayList�� title�� ���� case-insensitive�� ���ĺ� ������ �����Ѵ�.	
	 * - ���ĵ� list�� "books.txt"���Ͽ� ����. */
	public void writeFile(ArrayList<Books> list) {
		try {
			Collections.sort(list);
			FileWriter wr = new FileWriter("books.txt");
			for(int i=0; i<list.size(); i++) {
				wr.write(list.get(i).toString()+"\n");
			}
			System.out.println("File is updated!!!!");
			wr.close();				
		}catch(IOException e) {
			System.out.println("file write error!");
			System.exit(0);
		}
	}
	
	/* Server start */
	public void start(int portNum) {
		ServerSocket serverSocket = null;
		Socket socket = null;
		
		try {
			serverSocket = new ServerSocket(portNum);
			System.out.println("server has started.");
			System.out.println("Server IP Address : " +InetAddress.getLocalHost().getHostAddress());
			while(true) {
				socket = serverSocket.accept();
				System.out.println("connection established.");
				ServerReceiver thread = new ServerReceiver(socket);
				thread.start();
			}
		} catch(Exception e) {
			System.out.println("Connection establishment failed.");
			return ;
		}
	}
	
	/* Send messages to client
	 * - Ư�� client���� �ش� �޽����� �����Ѵ�. */
	public void sendToClient(String userID, String msg) {
		try {
			DataOutputStream out = (DataOutputStream)clients.get(userID);
			System.out.println("*send to " + userID + " : " + msg);
			out.writeUTF(msg);
		} catch(IOException e) {}
	}
	
	/* main
	 * - args�� ���� 1���� �ƴϸ�, ���� �޼��� ����ϰ� ���α׷��� �����Ѵ�.*/
	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.print("Please give the port number as an argument.");
			System.exit(0);
		}
		new Server().start(Integer.parseInt(args[0]));
	}
	
	/* Server Receiver 
	 * - client�� ������ ������ �ް�, �׿� �´� ��ġ�� ���Ѵ�.*/
	class ServerReceiver extends Thread{
		Socket socket;
		DataInputStream in;
		DataOutputStream out;
		
		ServerReceiver(Socket socket){
			this.socket = socket;
			try {
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
			} catch(IOException e) {}
		}
		
		public void run() {
			String userID ="";
			
			try {
				userID = in.readUTF(); //client�κ��� ���� ������ userID�� �޾ƿ´�.
				clients.put(userID, out); //���� ���� ���� clients�� �����صд�.
				System.out.println("join " + userID); //������ client�� id�� ����Ѵ�.
				
				while(in != null) {
					int flag = 0;
					
					String command = in.readUTF();
					System.out.println(userID+"'s input : " + command);
					
					ArrayList<Books> list = readFile(); //books.txt������ �о���δ�.
				
					/* 1) command�� add�� ���
					 * - client�κ��� å�� title�� author�� �Է¹޴´�.
					 * - title �Ǵ� author�� ������ ���� ��� �Է��� �����ϰ� main prompt�� ���ư���.
					 * - �׷��� ���� ���, list�� üũ�Ѵ�. �Է¹��� title�� �̹� list�� ������ ���, client���� �̸� �˸���.
					 * - list�� ���� title�� ���, �Է¹��� title�� author�� list�� �߰��Ѵ�. */
					if(command.equals("add") == true) {
						String title = in.readUTF();
						String author = in.readUTF();
					
						if(title.equals(" ") != true && author.equals(" ") != true) {
							for(int i=0; i<list.size(); i++) {		
								if(title.equalsIgnoreCase(list.get(i).getTitle()) == true){
									flag = 1; //�̹� list�� �����ϴ� title�� ���
									break;
								}
							}
							if(flag == 1) {
								sendToClient(userID, "The book already exists in the list."); //client���� �̹� �����ϴ� å�̶�� �˸�
							}
							else {
								list.add(new Books(title, author, "-")); //list�� å�� �߰�
								sendToClient(userID, "A new book added to the list.");
								writeFile(list); //file�� ������Ʈ
							}
						}
					}
					
					/* 2) command�� borrow�� ���
					 * - client�κ��� title�� �Է¹޴´�.
					 * - title�� ������ ���� ���, �Է��� �����ϰ� main prompt�� ���ư���.
					 * - �׷��� ���� ���, list�� üũ�Ѵ�. �Է¹��� title�� list�� �����ϴ� ���, borrower�� Ȯ���Ѵ�.
					 * - borrower�� ���� ���, borrower�� �ش� client�� ���̵�� �ٲ� �� client���� å�� �뿩�Ǿ����� �˸���.
					 * - �Է¹��� title�� list�� �������� �ʰų�, borrower�� �̹� �ִ� å�� ��� client���� �뿩�� �� ���� å�̶�� �˸���. */
					else if(command.equals("borrow") == true) {
						String title = in.readUTF();
						
						if(title.equals(" ") != true) {
							for(int i=0; i<list.size(); i++) {		
								if(title.equalsIgnoreCase(list.get(i).getTitle()) == true){
									if(list.get(i).getBorrower().equals("-") == true) {
										Books change = new Books(list.get(i).getTitle(), list.get(i).getAuthor(), userID);
										list.set(i, change);
										sendToClient(userID, "You borrowed a book. - " + list.get(i).getTitle());
										flag = 1;
										writeFile(list);
										break;
									}
								}
							}
							if(flag == 0) {
								sendToClient(userID, "The book is not available.");
							}
						}
					}
					
					/* 3) command�� return�� ���
					 * - client�κ��� title�� �Է¹޴´�.
					 * - title�� ������ �ƴ� ���, list�� üũ�Ѵ�. �Է¹��� title�� list�� ������ ���, borrower�� üũ�Ѵ�. 
					 * - borrower�� ���� ��� �ش� å�� borrower�� '-'�� �ٲ� ��, �ش� client���� å�� ���������� �ݳ��Ǿ����� �˸���.
					 * - �Է¹��� title�� list�� �������� �ʰų�, borrower�� ���� �ƴ� ���, �ش� client���� �� å�� ������ �ʾҴٰ� �˸���.*/
					else if(command.equals("return") == true) {
						String title = in.readUTF();
						
						if(title.equals(" ") != true) {
							for(int i=0; i<list.size(); i++) {		
								if(title.equalsIgnoreCase(list.get(i).getTitle()) == true){
									if(list.get(i).getBorrower().equals(userID) == true) {
										Books change = new Books(list.get(i).getTitle(), list.get(i).getAuthor(), "-");
										list.set(i, change);
										sendToClient(userID, "You returned a book. - " + list.get(i).getTitle());
										flag = 1;
										writeFile(list);
										break;
									}
								}
							}
							if(flag == 0) {
								sendToClient(userID, "You did not borrow the book.");
							}
						}
					}
					
					/* 4) command�� info�� ��� 
					 * - �ش� client�� ���� �뿩 ���� å ����� ����Ѵ�. */
					else if(command.equals("info") == true) {
						int num=0;
						Books[] borrowedBooks = new Books[list.size()];
						
						for(int i=0; i<list.size(); i++) {		
							if(userID.equals(list.get(i).getBorrower()) == true){
								borrowedBooks[num] = list.get(i);
								++num;
							}
						}
						String msg = "You are currently borrowing "+num+" books:";

						for(int i=0; i<num; i++) {
							msg += "\n" + (i+1) + ". "+borrowedBooks[i].getTitle() + ", " + borrowedBooks[i].getAuthor();
						}
						sendToClient(userID, msg);
					}
					
					/* 5) command�� search�� ���
					 * - �ش� client�κ��� pattern�� �Է¹޴´�.
					 * - list�� �����Ͽ� �ش� pattern�� ���Ե� title�̳� author�� ������ ���, �̿� �ش��ϴ� ��� å�� ����� client���� ������. */
					else if(command.equals("search") == true) {
						int num = 0;
						Books[] borrowedBooks = new Books[list.size()];
						
						String pattern = in.readUTF();
						if(pattern.equals(" ") != true) {
							for(int i=0; i<list.size(); i++) {		
								if(list.get(i).getTitle().toLowerCase().contains(pattern.toLowerCase()) == true){
									borrowedBooks[num] = list.get(i);
									++num;
								} else if(list.get(i).getAuthor().toLowerCase().contains(pattern.toLowerCase()) == true) {
									borrowedBooks[num] = list.get(i);
									++num;
								}
							}
							String msg = "Your search matched "+num+" results.";

							for(int i=0; i<num; i++) {
								msg += "\n" + (i+1) + ". "+borrowedBooks[i].getTitle() + ", " + borrowedBooks[i].getAuthor();
							}
							sendToClient(userID, msg);
						}
					}
				}
			} catch(IOException e) {
			} finally{
				clients.remove(userID);
				System.out.println(userID+ " has left.");
			}
		}

	}
}
