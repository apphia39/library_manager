# library manager
해당 project는 서강대학교 JAVA Final Project 과제로 수행한 project입니다.

## 1. Introduction
In this project, you are going to develop a library manager program. You need to write a server and a client program, in which the client and the server communicate through a TCP socket. Carefully go through the requirements described below.
<br><br>

## 2. Requirements
#### R1. 
서버 프로그램(Server.java)는 "books.txt"라는 이름의 파일에 접근한다. "books.txt"는 library에 있는 모든 책들이 기록된 텍스트 파일이다. "books.txt" 파일의 각 줄에는 title, author, borrower의 userID가 포함된 book entry가 입력되어 있고, 이들은 각각 '\t'로 구분되어있다. tab 문자는 title, author, borrower에는 포함되지 않고, 오직 이들을 구분할 때만 쓰인다고 가정한다. 책들은 title에 대해 case-insensitive한 알파벳 순으로 정렬되어있다. "books.txt"의 예시는 다음과 같다.<br>

<books.txt>  <br>
A Promised Land Barack Obama  -  <br>
Green Lights  Matthew McConaughey  -  <br>
Harry Potter and the Sorcerer's Stone J.K. Rowling  trump  <br>
How to Catch a Unicorn  Adam Wallace joebiden  <br>
I Love You to the Moon and Back Amelia Hepworth -  <br>

첫 줄에서, "A Promised Land"는 책의 title이다. 그 다음, tab 문자 이후에 오는 "Barack Obama"는 이 책의 author이다. 그 다음, tab 문자 이후에 오는 "trump"는 도서관에서 이 책을 빌린 borrower이다.

만약 아무도 책을 대여하지 않았다면, borrower field를 '-'로 표시한다.

책은 항상 title과 author를 모두 갖고 있어야한다.

책들은 title에 대해 case-insensitive한 알파벳 순으로 정렬되어있다. 예를 들어, "aaa", "Abc", "bbb", "CCC"의 정렬은 다음과 같다.<br>
aaa<br>
Abc<br>
bbb<br>
CCC<br>

#### R2. 
"books.txt"의 내용이 바뀌면, 서버는 곧바로 books.txt파일을 업데이트해야한다. 예를 들어, 새로운 책이 file에 추가될 경우, borrower가 바뀌는 경우 등이 생기면 books.txt는 바로 업데이트 되어야한다. 

#### R3. 
서버 프로그램은 오직 하나의 argument(port number)만 입력받는다. 예를 들어, 서버 프로그램을 실행할 때 command가 다음과 같아야 한다.

ex) C:\Users\eclipse-workspace\cse3040_fp> java -classpath .\bin cse3040_fp.Server 7777

만약 user argument의 수가 1이 아닐 경우, 프로그램은 "Please give the port number as an argument."를 출력한 후 프로그램을 종료한다.

#### R4. 
클라이언트 프로그램은 두 개의 arguments(server IP address, server port number)만 입력받는다. 예를 들어, 클라이언트 프로그램을 실행할 때 command는 다음과 같아야 한다.

ex) C:\Users\eclipse-workspace\cse3040_fp> java -classpath .\bin cse3040_fp.Client 127.0.0.1 7777

만약 user argument의 수가 2가 아닐 경우, 프로그램은 "Please give the IP address and port number as arguments."를 출력한 후 프로그램을 종료한다.

#### R5. 
클라이언트 프로그램이 시작되면, 서버와 TCP connection이 이루어져야 한다. 만약 connection request가 실패할 경우, "Connection establishment failed."를 출력한 후 프로그램을 종료한다.

#### R6. 
일단 클라이언트가 성공적으로 서버에 연결되면, 프로그램은 prompt를 출력하고, user input을 위해 대기한다. prompt는 다음과 같다.

ex)<br>
C:\Users\eclipse-workspace\cse3040_fp> java -classpath .\bin cse3040fp.Client 127.0.0.1 7777<br>
Enter userID>> 

user는 자신의 userID를 입력해야한다. userID의 조건은 다음과 같다.
- userID는 single word여야 한다.
- userID는 알파벳 소문자와 숫자만을 포함해야 한다.

만약 이러한 ID 형식을 지키지 않을 경우, 프로그램은 "UserID must be a single word with lowercase alphabets and numbers."를 출력하고, prompt를 다시 보여준다. 만약 user가 올바른 형식의 userID를 입력했을 경우, 해당 userID로 로그인된다. 이러한 경우, program은 greeting message를 출력하고, prompt를 해당 userID로 바꾼다. (예시는 다음과 같다.)

ex)<br>
Enter userID>> two words<br>
UserID must be a single word with lowercase alphabets and numbers.<br>
Enter userID>> Hel*o<br>
UserID must be a single word with lowercase alphabets and numbers.<br>
Enter userID>> <br>
UserID must be a single word with lowercase alphabets and numbers.<br>
Enter userID>> minseon<br>
Hello minseon!<br>
minseon>><br>


### ※ From now on, each requirement is on implementing a command from the client prompt. In order to implement the command, you will need to implement functions at the client as well as the server. Especially, you will need to design the message sent and received between the server and the client. Note that all the following commands are available after the user successfully logins with a user ID.


### ※ command가 "add"일 경우, client는 prompt를 이용하여 title과 author를 입력받는다.
#### R7.
만약 user가 title과 author를 모두 입력했을 경우, client는 해당 책을 list에 추가시키기 위해 입력받은 title과 author를 server로 보낸다. server는 같은 title을 갖는 책이 list에 이미 존재하는지 체크한다. 만약 해당 책이 list에 존재하니 않을 경우, server는 list에 해당 책을 추가한다. 책이 추가될 때에는 기본적으로 borrower는 "-"로 저장한다. 그 다음, 서버는 client에게 "A new book added to the list."를 전송하고, client는 이를 출력한다.

#### R8. 
만약 해당 책이 이미 list에 존재할 경우(같은 title을 갖는 책이 존재할 경우), 서버는 해당 책을 list에 추가시키지 않는다. 서버는 client에게 "The book already exists in the list."를 전송하고, client는 이를 출력한다.

#### R9. 
prompt "add-title> " 또는 "add-author> "에 대해, 입력이 없거나 공백일 경우, 해당 command는 무시되고 main prompt로 돌아온다.

책의 title과 author는 case-insensitive하다. 예를 들어, "A Promised Land"와 "a promised land"는 같은 책으로 취급한다. 따라서 이미 list에는 "A Promised Land"라는 책이 존재하므로, "a promised land"라는 책은 add할 수 없다.

ex)<br>
minseon>> add<br>
add-title> Trump: The Art of the Deal<br>
add-author> Donald J. Trump<br>
A new book added to the list.<br>
minseon>> add<br>
add-title> Trump: The Art of the Deal<br>
add-author> Donald J. Trump<br>
The book already exists in the list.<br>
minseon>> add<br>
add-title><br>
trump>> add<br>
add-title> Becoming<br>
add-author><br>
minseon>><br>

### ※ command가 "borrow"일 경우, 클라이언트는 prompt를 통해 title을 입력받는다.
#### R10. 
만약 user가 title을 입력했을 경우, client는 입력받은 title을 서버로 전송한다. 서버는 해당 title을 갖는 책이 list에 존재하는지 체크한다. 이 때 title은 case-insensitive하다. 만약 책이 list에 존재하고, 책의 borrower가 존재하지 않을 경우, 서버는 borrower를 해당 client의 userID로 바꾼다. 이후 서버는 client에게 책을 성공적으로 대여했다는 메시지를 전송하고, client는 이를 출력한다.

#### R11. 
만약 해당 title을 갖는 책이 list에 존재하지 않거나, 누군가 이미 대여를 한 경우, 서버는 클라이언트에게 "The book is not available"이라는 메시지를 전송하고, 클라이언트는 이를 출력한다.

#### R12. 
prompt "borrow-title>"에 대해 아무런 입력이 없거나, 공백이 입력될 경우, 해당 명령어는 무시되고 main prompt로 돌아간다.

ex)<br>
minseon>> borrow<br>
borrow-title> a promised land<br>
You borrowed a book. – A Promised Land<br>
minseon>> borrow<br>
borrow-title> a promised land<br>
The book is not available.<br>
minseon>> borrow<br>
borrow-title><br>
minseon>><br>

### ※ command가 "return"일 경우, 클라이언트는 prompt를 통해 title을 입력받는다.
#### R13. 
만약 user가 title을 입력했을 경우, 클라이언트는 이를 서버로 전송한다. 서버는 해당 책이 list에 존재하는지 체크한다.(이 때 title은 case-insensitive하다) 만약 책이 list에 존재하고, 그 책의 borrower가 해당 client의 userID와 일치하는 경우, 책은 성공적으로 반납된다. 즉, 서버에서는 해당 책의 borrower를 "-"로 바꾼 뒤, 클라이언트에게 책이 성공적으로 반납되었음을 알린다. 

#### R14. 
만약 입력받은 title이 list에 존재하지 않거나, borrower가 해당 client의 userID와 일치하지 않는 경우, 서버는 클라이언트에게 "You did not borrow the book."을 전송하고, 클라이언트는 이를 출력한다.

#### R15. 
prompt "return-title>"에 대해 user가 아무런 입력을 하지 않았거나, 공백을 입력했을 경우, 해당 명령어는 무시되고, main prompt로 돌아간다.

ex)<br>
trump>> return<br>
return-title> a promised land<br>
You returned a book. – A Promised Land<br>
trump>> return<br>
return-title> a promised land<br>
You did not borrow the book.<br>
trump>> return<br>
return-title><br>
trump>> <br>


### ※ command가 "info"일 경우, 해당 user가 현재 대여 중인 책의 목록을 출력한다. 
#### R16. 
이는 서버로의 요청을 통해 이루어질 수 있다. 출력 예시는 다음과 같다. (책 목록은 title에 대해 case-insensitive한 알파벳 순으로 정렬되어있다.)

ex)<br>
minseon>> info<br>
You are currently borrowing 1 books:
1. A Promised Land, Barack Obama
minseon>> <br>

### ※ command가 "search"일 경우, user는 prompt를 통해 string pattern을 입력받는다. 
#### R17.
만약 user input(space bar 포함)이 3글자 미만일 경우, client는 "Search string must be longer than 2 characters."를 출력하고, prompt를 다시 보여준다.

#### R18. 
만약 입력받은 string pattern이 3글자 이상일 경우, client는 server에게 해당 pattern을 전송한다. 서버는 그 pattern이 포함된 title이나 author이 book list에 존재하는지 탐색한다. 만약 존재할 경우, 서버는 이를 만족하는 모든 책의 목록을 client에게 전송하고, client는 이를 출력한다. 만약 pattern이 포함된 책이 2개 이상일 경우, title에 대해 case-insensitive한 알파벳 순으로 이를 정렬한다. 

#### R19. 
prompt "search-string>"에 대해 user가 아무런 입력도 하지 않을 경우(바로 enter를 입력한 경우) command는 무시되고, main prompt로 돌아간다.

ex)<br>
minseon>> search<br>
search-string> ab<br>
Search string must be longer than 2 characters.<br>
search-string> the<br>
Your search matched 3 results.
1. Green Lights, Matthew McConaughey
2. Harry Potter and the Sorcerer's Stone, J.K. Rowling
3. I Love You to the Moon and Back, Amelia Hepworth
minseon>> search<br>
search-string><br>
minseon>><br>

### ※ R20. command가 "exit"일 경우, 해당 client는 프로그램을 종료한다.

### ※ invalid command
#### R21. 
"add", "borrow", "return", "info", "search", "exit"외의 command는 전부 invalid command로 취급한다. invalid command가 입력될 경우, 프로그램은 available command list를 출력한다.

ex)<br>
minseon>> abcde<br>
[available commands]<br>
add: add a new book to the list of books.<br>
borrow: borrow a book from the library.<br>
return: return a book to the library.<br>
info: show list of books I am currently borrowing.<br>
search: search for books.<br>
exit: terminate program.<br>
minseon>><br>


## 3. Other Requirements
- "books.txt"파일로의 접근은 서버만 할 수 있다. 클라이언트는 books.txt파일로 직접 접근해서는 안되며 오직 서버를 통해서만 접근해야한다.
- 다수의 클라이언트들이 concurrent하게 서버로 접근할 수 있도록 해야한다.

