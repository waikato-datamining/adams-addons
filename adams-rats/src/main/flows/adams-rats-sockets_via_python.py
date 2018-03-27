# taken from here:
# https://stackoverflow.com/a/18297623

import socket

serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serversocket.bind(('localhost', 8000))
serversocket.listen(5) # become a server socket, maximum 5 connections

while True:
    connection, address = serversocket.accept()
    buf = connection.recv(64)  # buffer size
    if len(buf) > 0:
        print(buf)
        clientsocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        clientsocket.connect(('localhost', 8001))
        clientsocket.sendall(buf)
        clientsocket.close()
