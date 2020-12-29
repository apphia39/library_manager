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

/* "books.txt"의 내용을 Books type의 ArrayList로 저장 */
class Books implements Comparable<Books>{
	private String title; //책의 제목
	private String author; //책의 저자
	private String borrower; //책을 대여한 사람
	
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
	HashMap<String, DataOutputStream> clients; //이 서버에 접속하는 clients를 hashmap으로 저장
	
	Server(){
		clients = new HashMap<>();
		Collections.synchronizedMap(clients); //처음에는 synchronized 상태임.
	}
	
	/* Read File 
	 * - "books.txt" 파일을 읽어 Books type의 ArrayList로 저장한다. */
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
	 * - ArrayList를 title에 대해 case-insensitive한 알파벳 순으로 정렬한다.	
	 * - 정렬된 list를 "books.txt"파일에 쓴다. */
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
	 * - 특정 client에게 해당 메시지를 전송한다. */
	public void sendToClient(String userID, String msg) {
		try {
			DataOutputStream out = (DataOutputStream)clients.get(userID);
			System.out.println("*send to " + userID + " : " + msg);
			out.writeUTF(msg);
		} catch(IOException e) {}
	}
	
	/* main
	 * - args의 수가 1개가 아니면, 오류 메세지 출력하고 프로그램을 종료한다.*/
	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.print("Please give the port number as an argument.");
			System.exit(0);
		}
		new Server().start(Integer.parseInt(args[0]));
	}
	
	/* Server Receiver 
	 * - client가 보내온 내용을 받고, 그에 맞는 조치를 취한다.*/
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
				userID = in.readUTF(); //client로부터 현재 접속한 userID를 받아온다.
				clients.put(userID, out); //현재 접속 중인 clients를 저장해둔다.
				System.out.println("join " + userID); //접속한 client의 id를 출력한다.
				
				while(in != null) {
					int flag = 0;
					
					String command = in.readUTF();
					System.out.println(userID+"'s input : " + command);
					
					ArrayList<Books> list = readFile(); //books.txt파일을 읽어들인다.
				
					/* 1) command가 add일 경우
					 * - client로부터 책의 title과 author를 입력받는다.
					 * - title 또는 author에 공백이 들어온 경우 입력을 무시하고 main prompt로 돌아간다.
					 * - 그렇지 않을 경우, list를 체크한다. 입력받은 title이 이미 list에 존재할 경우, client에게 이를 알린다.
					 * - list에 없는 title인 경우, 입력받은 title과 author를 list에 추가한다. */
					if(command.equals("add") == true) {
						String title = in.readUTF();
						String author = in.readUTF();
					
						if(title.equals(" ") != true && author.equals(" ") != true) {
							for(int i=0; i<list.size(); i++) {		
								if(title.equalsIgnoreCase(list.get(i).getTitle()) == true){
									flag = 1; //이미 list에 존재하는 title인 경우
									break;
								}
							}
							if(flag == 1) {
								sendToClient(userID, "The book already exists in the list."); //client에게 이미 존재하는 책이라고 알림
							}
							else {
								list.add(new Books(title, author, "-")); //list에 책을 추가
								sendToClient(userID, "A new book added to the list.");
								writeFile(list); //file을 업데이트
							}
						}
					}
					
					/* 2) command가 borrow일 경우
					 * - client로부터 title을 입력받는다.
					 * - title에 공백이 들어온 경우, 입력을 무시하고 main prompt로 돌아간다.
					 * - 그렇지 않을 경우, list를 체크한다. 입력받은 title이 list에 존재하는 경우, borrower를 확인한다.
					 * - borrower가 없는 경우, borrower를 해당 client의 아이디로 바꾼 뒤 client에게 책이 대여되었음을 알린다.
					 * - 입력받은 title이 list에 존재하지 않거나, borrower가 이미 있는 책일 경우 client에게 대여할 수 없는 책이라고 알린다. */
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
					
					/* 3) command가 return일 경우
					 * - client로부터 title을 입력받는다.
					 * - title이 공백이 아닐 경우, list를 체크한다. 입력받은 title이 list에 존재할 경우, borrower를 체크한다. 
					 * - borrower가 나일 경우 해당 책의 borrower를 '-'로 바꾼 뒤, 해당 client에게 책이 성공적으로 반납되었음을 알린다.
					 * - 입력받은 title이 list에 존재하지 않거나, borrower가 내가 아닐 경우, 해당 client에게 그 책을 빌리지 않았다고 알린다.*/
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
					
					/* 4) command가 info일 경우 
					 * - 해당 client가 현재 대여 중인 책 목록을 출력한다. */
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
					
					/* 5) command가 search일 경우
					 * - 해당 client로부터 pattern을 입력받는다.
					 * - list를 조사하여 해당 pattern이 포함된 title이나 author이 존재할 경우, 이에 해당하는 모든 책의 목록을 client에게 보낸다. */
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
