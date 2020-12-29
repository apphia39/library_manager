package cse3040_fp_20181603;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	/* Client Sender
	 * - client로부터 입력을 받고 그 내용을 server로 전송한다.	 */
	static class ClientSender extends Thread{
		Socket socket;
		DataOutputStream out;
		DataInputStream in;
		String userID;
		
		ClientSender(Socket socket, String userID){
			this.socket = socket;
			try {
				out = new DataOutputStream(socket.getOutputStream());
				in = new DataInputStream(socket.getInputStream());
				this.userID = userID;				
			} catch(Exception e) {}
		}
		
		/* client로 부터 입력을 받은 뒤, 그 내용을 server로 전송한다. */
		public int inputCommand() {
			Scanner scanner = new Scanner(System.in);
			String cmd = scanner.nextLine();
			try {
				if(cmd.trim().length() == 0) {
					out.writeUTF(" ");
					return 0;
				}
				out.writeUTF(cmd);
				return 1;
			} catch(IOException e) {}
			return 0;
		}
		
		@SuppressWarnings("all")
		public void run() {			
			String command="";
			String title="";
			String author="";
			
			try {
				if(out!= null) {
					out.writeUTF(userID); //userID를 서버로 보낸다.
				}
				
				while(out != null) {
					Scanner scanner = new Scanner(System.in);
					String msg;
					
					System.out.print(userID+">> ");
					command = scanner.nextLine();
					
					out.writeUTF(command);//command를 서버로 보냄
					
					if(command.equals("add") == true) {						
						System.out.print("add-title> ");
						
						/* title 입력을 안한 경우 */
						if(inputCommand() == 0)
							continue;

						System.out.print("add-author> ");
						
						/* author 입력을 안한 경우 */
						if(inputCommand() == 0)
							continue;
						
						msg = in.readUTF();
						if(msg.equals("The book already exists in the list.") == true) {
							System.out.println(msg);
							continue;
						}
						System.out.println(msg);
					}
					else if(command.equals("borrow") == true) {
						System.out.print("borrow-title> ");

						/* title 입력을 안한 경우 */
						if(inputCommand() == 0)
							continue;
						
						msg = in.readUTF();
						if(msg.equals("The book is not available.") == true) {
							System.out.println(msg);
							continue;
						}
						System.out.println(msg);					
					}
					else if(command.equals("return") == true) {
						System.out.print("return-title> ");
						
						/* title 입력을 안한 경우 */
						if(inputCommand() == 0)
							continue;
						
						System.out.println(in.readUTF());
					}
					else if(command.equals("info") == true) {
						System.out.println(in.readUTF());
					}
					else if(command.equals("search") == true) {
						while(true) {
							System.out.print("search-string> ");

							String pattern = scanner.nextLine();
							
							/* 아무 입력 없이 바로 enter를 입력하면 main prompt로 돌아간다. */
							if(pattern.length() == 0) { 
								out.writeUTF(" ");
								break;
							}
							
							/* user가 입력한 pattern(spacebar포함)의 길이가 3글자 미만일 경우 pattern을 다시 입력받는다. */
							if(pattern.length() < 3) {
								System.out.println("Search string must be longer that 2 characters.");
								continue;
							} 
							else {
								out.writeUTF(pattern);
								System.out.println(in.readUTF());
								break;
							}
						}
					}
					else if(command.equals("exit") == true) {
						System.exit(0);
					}
					else{
						/* 위 명령어 외의 입력은 모두 invalid command이다.
						 * - invalid command : show the valid command list. */
						System.out.println("[available commands]");
						System.out.println("add: add a new book to the list of books.");
						System.out.println("borrow: borrow a book from the library.");
						System.out.println("return: return a book to the library.");
						System.out.println("info: show list of books I am currently borrowing.");
						System.out.println("search: search for books.");
						System.out.println("exit : terminate promgram");
					}
				}
			}catch(IOException e) {}			
		}
	}
	
	/* main
	 * - args의 수가 2개가 아니라면, 오류 메시지 출력 후 프로그램을 종료한다.
	 * - 만약 client가 server에 성공적으로 연결되었다면, 프로그램은 userID의 입력을 기다린다. 
	 * - userID는 반드시 한 단어로 이루어져야 하고, 알파벳 소문자나 숫자로만 이루어질 수 있다.
	 * - userID가 이와 같은 형식을 지키지 않았을 경우, 오류 메시지를 출력하고 prompt를 다시 보여준다.
	 * - userID가 형식을 지켰을 경우, 그 userID로 로그인 한다. 이 경우, 프로그램은 greeting message를 출력하고, prompt를 userID로 바꾼다.*/	
	public static void main(String[] args) {
		if(args.length != 2) {
			System.out.print("Please give the IP address and port number as arguments.");
			System.exit(0);
		}
		
		try {
			Socket socket = new Socket(args[0], Integer.parseInt(args[1]));			
			Scanner scanner = new Scanner(System.in);
			String name;
			
			while(true) {
				System.out.print("Enter userID>> ");
				name = scanner.nextLine();
				int nameflag = 0;
				
				if(name.trim().length() == 0) {
					/* 만약 user가 입력한 ID가 공백일 경우 */					
					nameflag = 1;
				}
				
				for(int i=0; i<name.length(); i++) {
					/* 만약 user가 입력한 ID가 소문자나 숫자가 아닐 경우 flag check */
					if('a' <= name.charAt(i) && name.charAt(i) <= 'z')
						continue;
					if('0' <= name.charAt(i) && name.charAt(i) <= '9')
						continue;
					nameflag = 1;
					break;
				}
				
				if(nameflag == 1) {
					System.out.println("UserID must be a single word with lowercase alphabets and numbers.");
					continue;
				}
				
				System.out.println("Hello "+name+"!");
				break;
			}
			Thread sender = new Thread(new ClientSender(socket, name));
			sender.start();
					
		} catch(ConnectException e) {
			System.out.print("Connection establishment failed.");
			System.exit(0);
		} catch(Exception e) {}
	}

}
