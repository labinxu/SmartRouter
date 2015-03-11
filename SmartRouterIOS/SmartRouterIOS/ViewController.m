//
//  ViewController.m
//  SmartRouterIOS
//
//  Created by user on 3/10/15.
//  Copyright (c) 2015 STC. All rights reserved.
//

#import "ViewController.h"
#import <sys/socket.h>
#import <netinet/in.h>
#import <arpa/inet.h>
#import <unistd.h>
const CFIndex kBufferSize = 20;

@interface ViewController ()
{
    CFSocketRef _socket;
    NSMutableData *_receivedData;
    int port;
}

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    self.hostAddrTextField.placeholder = @"请输入路由IP 默认 192.168.1.1";
    //self.portTextField.placeholder = @"8120";
    self.portTextField.text = @"8120";
    self.hostAddrTextField.text=@"127.0.0.1";
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
   
    // Dispose of any resources that can be recreated.
}

- (void) didReceiveData:(NSData *)data
{
    if (_receivedData==nil) {
        _receivedData = [[NSMutableData alloc] init];
    }
    [_receivedData appendData:data];
    
    
    // Update UI
    [[NSOperationQueue mainQueue] addOperationWithBlock:^{
        NSString *resultString = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        self.textView.text = resultString;
    }];

}
void socketCallback(CFReadStreamRef stream , CFStreamEventType event, void* myPtr)
{
    NSLog(@">> socketCallback in thread %@",[NSThread currentThread]);
    
    ViewController *controller = (__bridge ViewController*)myPtr;
    switch (event) {
        case kCFStreamEventHasBytesAvailable:
        {
            while (CFReadStreamHasBytesAvailable(stream)) {
                UInt8 buffer[kBufferSize];
                int numberRead = CFReadStreamRead(stream, buffer, kBufferSize);
                [controller didReceiveData:[NSData dataWithBytes:buffer length:numberRead]];
            }
        }
        break;
        case kCFStreamEventEndEncountered:
            default:
            break;
    }
}
- (void) readStream
{
    char buffer[20];
    //NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
    while (recv(CFSocketGetNative(_socket), buffer, sizeof(buffer), 0)) {
        NSLog(@"%@",[NSString stringWithUTF8String:buffer]);
    }
    
}

- (void)sendMessage{
    char buffer[20];
    memset(buffer, 0, sizeof(buffer));
    buffer[0]=0x00;
    buffer[1]=0x01;
    buffer[2]=0x00;
    buffer[3]=0x20;
    send(CFSocketGetNative(_socket), buffer, sizeof(buffer), 0);
    
}
void TcpSocketCallback(CFSocketRef socket, CFSocketCallBackType type,
                       CFDataRef address, const void *data, void *info)
{
 
    if (data != NULL) {
        NSLog(@"connect failed");
        return;
    }
    ViewController *controller = (__bridge ViewController*)info;
    
    [controller performSelectorInBackground:@selector(readStream) withObject:nil];
    
}
- (IBAction)onClicked:(id)sender {
   /* NSLog(@"Host addr %@, port %d", self.hostAddrTextField.text,20);
    NSString *host = self.hostAddrTextField.text;
    NSString *port = self.portTextField.text;
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"%@:%@", host,port]];
    NSThread *backgroundThread = [[NSThread alloc] initWithTarget:self selector:@selector(loadDataFromServer:) object:url];
    
    [backgroundThread start];*/
    
    CFSocketContext socketContext =
    {
        0,(__bridge void *)(self), NULL, NULL, NULL
    };
    _socket = CFSocketCreate(kCFAllocatorDefault, PF_INET, SOCK_STREAM, IPPROTO_TCP, kCFSocketConnectCallBack, TcpSocketCallback, &socketContext);
    if (_socket==nil) {
        [self networkFailedWithErrorMessage:@"connect failed"];
        return;
    }
    
    struct sockaddr_in addr4;
    memset(&addr4, 0, sizeof(addr4));
    addr4.sin_len = sizeof(addr4);
    addr4.sin_family = AF_INET;
    addr4.sin_port = htons(8120);
    addr4.sin_addr.s_addr = inet_addr([self.hostAddrTextField.text UTF8String]);
    
    CFDataRef address = CFDataCreate(kCFAllocatorDefault, (UInt8 *)&addr4, sizeof(addr4));
    CFSocketConnectToAddress(_socket, address, -1);//timeout negative means do not try again
    
    
    CFRunLoopRef cRunRef = CFRunLoopGetCurrent();
    CFRunLoopSourceRef sourceRef = CFSocketCreateRunLoopSource(kCFAllocatorDefault, _socket, 0);
    CFRunLoopAddSource(cRunRef, sourceRef, kCFRunLoopCommonModes);
    
    CFRelease(cRunRef);
}

- (void)loadDataFromServer:(NSURL *)url
{
    NSString *host = [url host];
    NSInteger port = [[url port] integerValue];
    
    CFStreamClientContext ctx ={0, (__bridge void*)(self), NULL, NULL, NULL};
    
    CFOptionFlags registeredEvents = (kCFStreamEventHasBytesAvailable|kCFStreamEventEndEncountered|kCFStreamEventErrorOccurred);

    CFReadStreamRef readStream;
    CFWriteStreamRef writeStream;
    CFStreamCreatePairWithSocketToHost(kCFAllocatorDefault, (__bridge CFStringRef)host, (UInt32)port, &readStream, &writeStream);
    //int socketFileDescriptor = socket(AF_INET, SOCKET_STREAM, 0);
    //Schedule the stream on the run loop to enable callbacks
    if (CFReadStreamSetClient(readStream, registeredEvents, socketCallback, &ctx)) {
        CFReadStreamScheduleWithRunLoop(readStream, CFRunLoopGetCurrent(), kCFRunLoopCommonModes);
    }
    else{
        NSLog(@"RUN error for cfreadstreamsetclient");
        return;
    }
    
    // open stream for reading
    if (CFReadStreamOpen(readStream) == NO) {
        [self networkFailedWithErrorMessage:@"Failed to open read stream"];
        return;
    }
    
    //CFError
    CFErrorRef error = CFReadStreamCopyError(readStream);
    if(error != NULL)
    {
        [self networkFailedWithErrorMessage:@"Failed to connect server"];
        return;
    }
    
    NSLog(@"Successfully connect to %@", url);
    CFRunLoopRun();
    
}

- (void) networkFailedWithErrorMessage:(NSString *)errormessage
{
    NSLog(@"%@",errormessage);
}
@end
