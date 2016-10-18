/**
* Author: <Insert ACM member name here>
* Date: <Insert Date Here>
* Project: <Project name goes here!>
* Description: <What does this do?>
*/

#include "acmChat.hpp"
#include <iostream>


int main(int argc, char** argv){

	std::cout << "Starting Client" << std::endl;
  acmChatClient myClient;
  myClient.setUsername("Sean Lavoie The Awesome");
  while (true) {
      std::cout << "Type quit to exit or send a chat: " << std::endl;
      std::cin >> myClient;        
  }
	
  return 0;
}
