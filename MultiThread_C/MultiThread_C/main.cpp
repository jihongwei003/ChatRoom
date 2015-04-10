/*_beginthread是_beginthreadex的功能子集，
* _beginthreadex是微软的C/C++运行时库函数，
* CreateThread是操作系统的函数。
*/

//CRT函数就是标准的C语言函数

/*有些CRT的函数象malloc(),fopen(),_open(),strtok(),ctime(),
* 或localtime()等函数需要专门的线程局部存储的数据块，这个数据块通常需要在创建线程的时候就建立，
* 如果使用CreateThread，这个数据块就没有建立，然后会怎样呢？
* 在这样的线程中还是可以使用这些函数而且没有出错，实际上函数发现这个数据块的指针为空时，
* 会自己建立一个，然后将其与线程联系在一起，这意味着如果你用CreateThread来创建线程，
* 然后使用这样的函数，会有一块内存在不知不觉中创建，遗憾的是，这些函数并不将其删除，
* 而CreateThread和ExitThread也无法知道这件事，于是就会有Memory Leak
*/
#include <stdio.h>
#include <process.h>
#include <windows.h>

//子线程1
unsigned __stdcall ThreadFun(PVOID pM)
{
	//printf("线程ID号为%4d的子线程说：Hello World\n", GetCurrentThreadId());
	while (true)
	{
		printf("1\n");
		Sleep(1000);
	}
}

//子线程2
unsigned __stdcall ThreadFun2(LPVOID args)
{
	int *c = (int*)args;
	while (true)
	{
		printf("%d\n", *c);
		Sleep(1000);
	}
}

struct ArgList
{
	int a;
	int b;
};

//子线程3
unsigned __stdcall ThreadFun3(LPVOID args)
{

	ArgList *arg = (ArgList*)args;
	while (true)
	{
		printf("%d，%d\n", arg->a, arg->b);
		Sleep(1000);
	}
}

int main()
{
	//不向线程函数传递参数，第4个参数为null；最后一个参数为null代表自动分配线程ID
	//HANDLE handle = (HANDLE)_beginthreadex(NULL, 0, ThreadFun, NULL, 0, NULL);

	//同时创建多条线程
	//const int THREAD_NUM = 5;
	//HANDLE handle[THREAD_NUM];
	//for (int i = 0; i < THREAD_NUM; i++)
	//handle[i] = (HANDLE)_beginthreadex(NULL, 0, ThreadFun, NULL, 0, NULL);

	//向线程函数传递参数需要使用指针（void*）
	//int a = 3;
	//int* b = &a;
	//HANDLE handle2 = (HANDLE)_beginthreadex(NULL, 0, ThreadFun2, b, 0, NULL);

	//传递多个参数的线程需要使用结构体
	ArgList *arg = new ArgList();
	arg->a = 0;
	arg->b = 10;
	HANDLE handle3 = (HANDLE)_beginthreadex(NULL, 0, ThreadFun3, (LPVOID)arg, 0, NULL);

	while (true)
	{
		printf("2\n");
		Sleep(2000);
	}

	return 0;
}
