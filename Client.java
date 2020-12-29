package cse3040_fp_20181603;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	/* Client Sender
	 * - client�κ��� �Է��� �ް� �� ������ server�� �����Ѵ�.	 */
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
		
		/* client�� ���� �Է��� ���� ��, �� ������ server�� �����Ѵ�. */
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
					out.writeUTF(userID); //userID�� ������ ������.
				}
				
				while(out != null) {
					Scanner scanner = new Scanner(System.in);
					String msg;
					
					System.out.print(userID+">> ");
					command = scanner.nextLine();
					
					out.writeUTF(command);//command�� ������ ����
					
					if(command.equals("add") == true) {						
						System.out.print("add-title> ");
						
						/* title �Է��� ���� ��� */
						if(inputCommand() == 0)
							continue;

						System.out.print("add-author> ");
						
						/* author �Է��� ���� ��� */
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

						/* title �Է��� ���� ��� */
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
						
						/* title �Է��� ���� ��� */
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
							
							/* �ƹ� �Է� ���� �ٷ� enter�� �Է��ϸ� main prompt�� ���ư���. */
							if(pattern.length() == 0) { 
								out.writeUTF(" ");
								break;
							}
							
							/* user�� �Է��� pattern(spacebar����)�� ���̰� 3���� �̸��� ��� pattern�� �ٽ� �Է¹޴´�. */
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
						/* �� ��ɾ� ���� �Է��� ��� invalid command�̴�.
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
	 * - args�� ���� 2���� �ƴ϶��, ���� �޽��� ��� �� ���α׷��� �����Ѵ�.
	 * - ���� client�� server�� ���������� ����Ǿ��ٸ�, ���α׷��� userID�� �Է��� ��ٸ���. 
	 * - userID�� �ݵ�� �� �ܾ�� �̷������ �ϰ�, ���ĺ� �ҹ��ڳ� ���ڷθ� �̷���� �� �ִ�.
	 * - userID�� �̿� ���� ������ ��Ű�� �ʾ��� ���, ���� �޽����� ����ϰ� prompt�� �ٽ� �����ش�.
	 * - userID�� ������ ������ ���, �� userID�� �α��� �Ѵ�. �� ���, ���α׷��� greeting message�� ����ϰ�, prompt�� userID�� �ٲ۴�.*/	
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
					/* ���� user�� �Է��� ID�� ������ ��� */					
					nameflag = 1;
				}
				
				for(int i=0; i<name.length(); i++) {
					/* ���� user�� �Է��� ID�� �ҹ��ڳ� ���ڰ� �ƴ� ��� flag check */
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
