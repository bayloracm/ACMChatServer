#pragma comment(lib, "Ws2_32.lib")

#include <string>
#include <iostream>
#include <sys/types.h>
#include <windows.h>
#include <winsock.h>

class acmChatClient {

public:

  acmChatClient() {

  }

  acmChatClient(std::string server, int servPort) {
    this->serverName = server;
    this->serverPort = servPort;
  }

  void setUsername(std::string user) {
    this->username = user;
  }

  bool connectToServer() {
    std::cout << "Connecting to " << serverName << std::endl;

    int error = WSAStartup(0x0202, &wsadata);

    //Did we get the right Winsock version?
    if (wsadata.wVersion != 0x0202)
    {
      WSACleanup(); // Clean up Winsock
      return false;
    }

    //Fill out the information needed to initialize a socket…
    SOCKADDR_IN target; //Socket address information

    target.sin_family = AF_INET; // address family Internet 
    target.sin_port = htons(this->serverPort); //Port to connect on
    target.sin_addr.s_addr = inet_addr(this->serverName.c_str()); //Target IP

    s = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP); //Create socket
    if (s == INVALID_SOCKET)
    {
      std::cerr << "Couldn't Create the Socket" << std::endl;
      return false;
    }

    //Try connecting...

    if (connect(s, (SOCKADDR *)&target, sizeof(target)) == SOCKET_ERROR)
    {
      std::cerr << "Couldn't Connect to the Server" << std::endl;
      return false; //Couldn't connect
    }
    else
      return true; //Success
  }

  void CloseConnection()
  {
    //Close the socket if it exists
    if (s)
      closesocket(s);

    WSACleanup(); //Clean up Winsock
  }

  void error(const char* msg) {
    perror(msg);
    return;
  }

  void sendData(std::string data) {
    send(s, data.c_str(), data.length(), 0);
  }

  SOCKET getSocket() {
    return s;
  }

  friend std::istream& operator>> (std::istream& in, const acmChatClient& obj) {

    if (!(const_cast<acmChatClient&>(obj).connectToServer())) {
      std::exit(-1);
      return in;
    }

    std::string temp;

    std::getline(in, temp);

    temp = obj.username + "\n" + temp;

    const_cast<acmChatClient&>(obj).sendData(temp);

    const_cast<acmChatClient&>(obj).CloseConnection();
    return in;
  }

private:
  int serverPort = 1337;
  //std::string serverName = "bayloracm.mooo.com";
  std::string serverName = "149.56.101.4";
  //std::string serverName = "127.0.0.1";
  std::string username = "ACM User";
  WSADATA wsadata;
  SOCKET s; //Socket handle



};

