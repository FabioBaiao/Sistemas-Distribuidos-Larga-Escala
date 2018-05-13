import sys
import socket
import threading
import pickle
import Peer

def hello_handler(pconn, msg):
	print(msg)
	pconn.send_msg("hack", "hello to you too")
	pconn.close()

def deploy_peer(addr, port, bootstrap):
	p = Peer.Peer(addr,port)
	p.register_handler("hello",hello_handler)
	p.start(bootstrap)

def find_ip_address():
	s = socket.socket( socket.AF_INET, socket.SOCK_STREAM )
	s.connect(("www.google.com", 80))
	address = s.getsockname()[0]
	s.close()
	return address

port_b = int(sys.argv[1])
peers = {} 
address = find_ip_address()
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
s.bind(('', port_b))
s.listen()

for port in range(10000, 10004):
	peers[(address, port)] = True
	threading.Thread(target=deploy_peer, args=[address, port, ('',port_b)]).start()

packed_peers = pickle.dumps(list(peers.keys()))

while True:
	(sock, addr) = s.accept()
	sock.send(packed_peers)
	sock.close()
