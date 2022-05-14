#include<iostream>
#include<fstream>
#include<stdlib.h>
#include<string.h>
#include<ctype.h>
 
using namespace std;

bool isSeparator(char ch) {

	char keywords[] = ",;[]{}()";
	int i = 0;
	bool flag = false;
	
	for(int i = 1; i < 8; i++) {
		if(ch == keywords[i]) {
			flag = true;
			break;
		}
	}
	
	return flag;
}
 
bool isKeyword(char buffer[]){
	char keywords[34][10] = {"auto","break","case","char","const","continue","default",
	"do","double","else","enum","extern","float","for","goto",
	"if","int","long","register","return","short","signed",
	"sizeof","static","struct","switch","typedef","union",
	"unsigned","void","volatile","while","cout","cin"};
	int i= 0;
	bool flag = false;
	for(i = 0; i < 32; ++i){
		if(strcmp(keywords[i], buffer) == 0){
			flag = true;
			break;
		}
	}
	return flag;
}

bool isNumber(char buffer[]) {
	bool flag = true;
	char *arr_ptr = &buffer[0];	
	for(int i = 0; i < strlen(arr_ptr); i++) {
		if(!isdigit(buffer[i])) {
			flag = false;
		}
	}
	
	char *ptr;
	double ret;
	
	ret = strtod(buffer, &ptr);
	if(ret != 0) {
   		flag = true;
	}
   
	return flag;
}

bool isOperator(char ch) {
	char operators[] = "+-*/%=<>";
	bool flag = false;
	for(int i = 0; i < 8; ++i){
	   	if(ch == operators[i]) {
			flag = true;
		} 	
	}
	return flag;
}
 
int main(){
	char ch, buffer[15];
	ifstream fin("./program.txt");
	int i,j=0;
	int term = 0;
	
	if(!fin.is_open()){
		cout<<"error while opening the file\n";
		exit(0);
	}
	while(!fin.eof()){
	   	ch = fin.get();
	   	
		if(isOperator(ch)){
			cout<<ch<<" is Operator\n";	
		}
		 
		if(isSeparator(ch)) {
			cout<<ch<<" is Separator\n";
			term = 1;
		} 
		
		if(isalnum(ch) || ch == '.'){
			buffer[j++] = ch;
		} 
		
		else if((ch == ' ' || ch == '\n' || ch == '\0' || term == 1) && (j != 0)){
			
			buffer[j] = '\0';
			j = 0;
			if(isKeyword(buffer))
		   		cout<<buffer<<" is keyword\n";
			else if(isNumber(buffer)) {
				cout<<buffer<<" is number\n";
			} else {
		   		cout<<buffer<<" is indentifier\n";
			}
		   		
		}
		  
	}
	fin.close();
	return 0;
}
