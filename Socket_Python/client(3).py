from socket import*
from time import sleep,ctime

HOST = '10.125.115.107'#名字服务器的IP
PORT = 8086#名字服务器的端口
BUFSIZ = 1024
ADDR = (HOST,PORT)

tcpCliSock = socket(AF_INET,SOCK_STREAM)
tcpCliSock.connect(ADDR)

HOST = tcpCliSock.recv(BUFSIZ)
PORT = (int)tcpCliSock.recv(BUFSIZ)

tcpCliSock.close()

raw_input('暂停中，随便输入点什么继续')

#连接真的服务器
ADDR = (HOST,PORT)
tcpCliSock = socket(AF_INET,SOCK_STREAM)
tcpCliSock.connect(ADDR)

f=open('log.txt','w')

while True:
    data = raw_input('>')
    if not data:
        break
    data = '客户端说：'+data+'('+ctime()+')'
    tcpCliSock.send(data)
    print(data)
    f.write(data)
    
    data = tcpCliSock.recv(BUFSIZ)
    if not data:
        break
    data = '服务器说：'+data
    print(data)
    f.write(data)

f.close
tcpCliSock.close()
