import SocketServer as SocketServer
import struct
import time
host = 'localhost'
port = 8120
addr = (host, port)


class RouterDummy(SocketServer.BaseRequestHandler):
    def handle(self):
        print("got connection from %s" % str(self.client_address))
        # msg = "connection %s:%s at %s succeed!" % (host, port, ctime())
        # self.wfile.write(msg)
        time.sleep(1)
        # will send them device list
        self.deviceList()
        while True:
            data = self.request.recv(1024)
            if data:
                d = Device()
                d.unpack(data)
                d.display()
                self.request.send(data)

    def deviceList(self):
        light = self.generateLightOn(2)
        self.request.send(light)
        time.sleep(1)
        self.request.send(self.generateLightOff(1))
        time.sleep(1)
        self.request.send(self.generateLightFlash(3))

    def generateLightOn(self, index):
        d = Device(1, 1, b'\x02', 1)
        return d.toBytes()

    def generateLightOff(self, index):
        d = Device(1, 2, b'\x02', 2, 2000, 180000)
        return d.toBytes()

    def generateLightFlash(self, index):
        d = Device(1, index, b'\x02', 3, 2000, 180000)
        return d.toBytes()


class Device:
    def __init__(self, dtype=None,
                 index=None,
                 orientation=None,
                 functions=None,
                 interval=0,
                 continuousTime=0):
        self.deviceType = dtype
        self.orientation = orientation
        self.functions = functions
        self.index = index
        self.flashContinuousTime = continuousTime
        self.flashInterval = interval

    def resetOrient(self):
        self.orientation = 2

    def unpack(self, data):
        print(len(data))
        if (len(data)) == 21:
            info = struct.unpack("!hh1silq", data)
        elif (len(data)) == 25:
            info = struct.unpack("!hh1siqq", data)
        elif (len(data)) == 17:
            info = struct.unpack("!hh1sIII", data)
        else:
            return
        self.deviceType = info[0]
        self.index = info[1]
        self.orientation = info[2]
        self.functions = info[3]
        self.flashInterval = info[4]
        self.flashContinuousTime = info[5]

    def display(self):
        print("Indentity %s%s functions %s param1 %s param2 %s"
              % (self.deviceType, self.index,
                 self.functions,
                 self.flashInterval,
                 self.flashContinuousTime))

    def toBytes(self):
        print(self.deviceType, self.index, self.orientation,
              self.functions, self.flashInterval, self.flashContinuousTime)
        device = struct.pack("!hh1sIII",
                             self.deviceType,
                             self.index,
                             self.orientation,
                             self.functions,
                             self.flashInterval,
                             self.flashContinuousTime)
        print("device len %d" % len(device))

        return device


print("Server is running %s:%s", (host, port))
server = SocketServer.ThreadingTCPServer(addr, RouterDummy)
server.serve_forever()


if __name__ != '__main__':
    import socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.bind(('localhost', 8210))
    sock.listen(5)
    while True:

        connection, address = sock.accept()
        print(address)
        try:
            connection.settimeout(5)
            buf = connection.recv(1024)
            print(buf)
            if buf == '1':
                connection.send('welcome to server!')
            else:
                connection.send('please go out!')
                print(buf)
        except socket.timeout:
            print('time out')

    connection.close()
