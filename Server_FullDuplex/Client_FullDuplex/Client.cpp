#include <winsock2.h> 
#include <iostream>
#include <process.h>

using namespace std;

//该指令将一个注释记录放入一个对象文件或可执行文件中。常用的lib关键字，可以帮我们连入一个库文件。
#pragma comment(lib,"ws2_32.lib") 

#define SERVER_ADD "127.0.0.1"
#define PUBLIC_SIN_PORT 8888

//接受数据的线程方法
unsigned __stdcall ThreadFun(LPVOID socket)
{
	SOCKET *clientSocketP = (SOCKET*)socket;

	char RecvBuffer[MAX_PATH];

	while (true)
	{
		memset(RecvBuffer, 0x00, sizeof(RecvBuffer));
		int Ret = recv(*clientSocketP, RecvBuffer, MAX_PATH, 0);
		if (Ret == 0 || Ret == SOCKET_ERROR)
		{
			cout << "服务端退出!" << endl;
			break;
		}
		cout << "接收到客户信息为:" << RecvBuffer << endl;
	}
	return 0;
}

int main()
{
	//初始化WSA，不初始化会socket error
	WORD sockVersion = MAKEWORD(2, 2);
	WSADATA wsaData;
	if (WSAStartup(sockVersion, &wsaData) != 0)
	{
		return 0;
	}

	//创建套接字 socket 
	SOCKET clientSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	if (clientSocket == INVALID_SOCKET)
	{
		printf("invalid socket !");
		return 0;
	}

	sockaddr_in serAddr;
	serAddr.sin_family = AF_INET;
	serAddr.sin_port = htons(PUBLIC_SIN_PORT);
	serAddr.sin_addr.S_un.S_addr = inet_addr(SERVER_ADD);

	if (connect(clientSocket, (sockaddr *)&serAddr, sizeof(serAddr)) == SOCKET_ERROR)
	{
		cout << "Connect Error::" << GetLastError() << endl;
		return -1;
	}
	else
	{
		cout << "连接成功!" << endl;
	}

	//新建接受消息的线程
	SOCKET *clientSocketP = &clientSocket;
	HANDLE handle = (HANDLE)_beginthreadex(NULL, 0, ThreadFun, clientSocketP, 0, NULL);

	while (true)
	{
		//发送消息
		char SendBuffer[MAX_PATH];
		cin.getline(SendBuffer, sizeof(SendBuffer));
		if (send(clientSocket, SendBuffer, (int)strlen(SendBuffer), 0) == SOCKET_ERROR)
		{
			cout << "Send Info Error::" << GetLastError() << endl;
			break;
		}
	}
	closesocket(clientSocket);

	WSACleanup();
	return 0;
}