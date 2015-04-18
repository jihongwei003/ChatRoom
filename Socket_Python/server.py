from socket import *
from time import sleep,ctime

HOST = ''
PORT = 9999
ADDR = (HOST,PORT)
BUFSIZ = 1024 

def main():
    tcpSerSock = socket(AF_INET,SOCK_STREAM) #监听套接字
    tcpSerSock.bind(ADDR) 
    tcpSerSock.listen(5) 

    print 'Waiting for connect...'
    tcpCliSock,addr = tcpSerSock.accept() #目标客户端套接字
    print 'connet from:',addr

    while True:
        data = tcpCliSock.recv(BUFSIZ)
        if not data:
            break
        print('客户端说：'+data)
        dataOut = raw_input('>');
        tcpCliSock.send(dataOut+'('+ctime()+')')
        print('我说：'+dataOut+'('+ctime()+')')
           
    tcpCliSock.close()
    tcpSerSock.close()

if __name__=='__main__': 
    main()


