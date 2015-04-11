#include <winsock2.h> 
#include <iostream>
#include <process.h>

using namespace std;

//该指令将一个注释记录放入一个对象文件或可执行文件中。常用的lib关键字，可以帮我们连入一个库文件。
#pragma comment(lib,"ws2_32.lib") 

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
			cout << "客户端退出!" << endl;
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
	SOCKET serverSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	if (serverSocket == INVALID_SOCKET)
	{
		cout << "socket error !";
		return 0;
	}

	//绑定IP和端口 bind   
	sockaddr_in sin;
	sin.sin_family = AF_INET;
	sin.sin_port = htons(PUBLIC_SIN_PORT);
	sin.sin_addr.S_un.S_addr = INADDR_ANY;
	if (bind(serverSocket, (LPSOCKADDR)&sin, sizeof(sin)) == SOCKET_ERROR)
	{
		cout << "bind error !";
	}

	//开始监听 listen  
	if (listen(serverSocket, 20) == SOCKET_ERROR)
	{
		cout << "listen error !";
		return 0;
	}

	cout << "服务器启动，开始监听。。。" << endl;

	while (true)
	{
		//循环接收accept
		SOCKET clientSocket = 0;
		sockaddr_in remoteAddr;
		int nAddrlen = sizeof(remoteAddr);

		clientSocket = accept(serverSocket, (SOCKADDR *)&remoteAddr, &nAddrlen);
		if (clientSocket == INVALID_SOCKET)
		{
			cout << "accept error !";
			break;
		}
		cout << "接受到一个连接：" << inet_ntoa(remoteAddr.sin_addr) << endl;

		//新建接受消息的线程
		SOCKET *clientSocketP = &clientSocket;
		HANDLE handle = (HANDLE)_beginthreadex(NULL, 0, ThreadFun, clientSocketP, 0, NULL);

		while (true)
		{
			//发送消息
			char SendBuffer[MAX_PATH];
			cin.getline(SendBuffer, sizeof(SendBuffer));
			int Ret = send(clientSocket, SendBuffer, (int)strlen(SendBuffer), 0);
			if (Ret == SOCKET_ERROR)
			{
				cout << "Send Info Error::" << GetLastError() << endl;
				break;
			}
		}

		closesocket(clientSocket);
	}

	//关闭监听
	closesocket(serverSocket);

	WSACleanup();
	return 0;
}