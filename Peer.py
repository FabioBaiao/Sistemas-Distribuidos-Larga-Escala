import socket
import threading
import pickle

class Peer:
	
	def __init__(self, addr, port):
		self.peers = {}
		self.handlers = {}

		self.peersLock = threading.RLock()

		self.port = port
		if addr:
			print(addr)
			self.address = addr
		else:
			self.__find_ip_address()
		
		self.id = "{}:{}".format(self.address, self.port)
		print("New peer at ", self.id)
		
	def add_peer(self, p_id, address, port):
		self.peers[p_id] = (address, port)
	
	def remove_peer(self, p_id):
		if p_id in self.peers:
			del self.peers[p_id]	
	
	def __find_ip_address(self):
		s = socket.socket( socket.AF_INET, socket.SOCK_STREAM )
		s.connect(("www.google.com", 80))
		self.address = s.getsockname()[0]
		s.close()
	
	def register_handler(self, msg_t, handler):
		# check if msg_t is a valid one
		self.handlers[msg_t] = handler
	
	def handle(self, sock):
		#addr, port = sock.getpeername()
		conn = PeerConnection(sock)
		
		try:
			"""
			Use PeerConnection to get the message from the peer
			use handler corresponding to the msg_type of the message received
			"""
			msg = conn.recv_msg().split(':')
			if msg[0] in self.handlers:
				print(self.id, "got an", msg[0])
				self.handlers[msg[0]](conn, msg[1])

		except Exception as e:
			print(e)


	def bootstrap(self, addr):
		s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		s.connect(addr)
		peers = pickle.loads(s.recv(1024))
		for peer in peers:
			peerid = peer[0]+':'+str(peer[1])
			if peerid not in self.peers and peerid != self.id:
				self.peers[peerid] = peer

	def dummy_protocol(self):
		for peer in self.peers.keys():
			s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			s.connect(self.peers[peer])
			conn = PeerConnection(s)
			conn.send_msg("hello","YO")
			rep = conn.recv_msg().split(':')
			print("Got reply:",rep[1])
			conn.close()

	def start_protocol(self, protocol):
		if protocol:
			print("TODO")
		else:
			t = threading.Thread(target=self.dummy_protocol, args=[])
			t.start()

	def start(self, bootstrap_server, protocol=None):
		s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
		s.bind((self.address, self.port))
		s.listen()
		self.bootstrap(bootstrap_server)

		self.start_protocol(protocol)
		while True:
			(sock, addr) = s.accept()
			t = threading.Thread(target=self.handle, args=[sock])
			t.start()

class PeerConnection:
	
	def __init__(self, socket):
		self.socket = socket
	
	def send_msg(self, msg_t, msg):
		payload = msg_t + ":" + msg
		self.socket.send(payload.encode())

	def recv_msg(self):
		m = self.socket.recv(1024).decode()
		print(m)
		return m
	
	def close(self):
		self.socket.close()

