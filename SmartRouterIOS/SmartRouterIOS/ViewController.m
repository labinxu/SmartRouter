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

#import "DeviceTableViewCell.h"
#import "Device.h"
const CFIndex kBufferSize = 20;

@interface ViewController ()
{
    CFSocketRef _socket;
 
    NSMutableDictionary *_indexAndDevice;
 
    UIImage *light_on_img;
    UIImage *light_off_img;
    UIImage *light_flash_img;
}

@end

@implementation ViewController
@synthesize devices;


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    self.hostAddrTextField.placeholder = @"请输入路由IP 默认 192.168.1.1";
    self.portTextField.text = @"8120";
    self.hostAddrTextField.text=@"127.0.0.1";
    //self.devices = [[NSMutableDictionary alloc] initWithObjectsAndKeys:@"11",@"aa", nil];
    self.devices = [[NSMutableDictionary alloc] init];

    light_on_img = [[UIImage alloc] initWithContentsOfFile:@"light_on.png"];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
   
    // Dispose of any resources that can be recreated.
}
- (void) viewDidUnload
{
    [super viewDidUnload];
    self.devices = nil;
}

- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.devices count];
}

- (UITableViewCell *)tableView: (UITableView*)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellTableIdentifier = @"DeviceCellIdentifier";
    static BOOL nibsRegistered = NO;
    if (!nibsRegistered) {
        UINib *nib = [UINib nibWithNibName:@"DeviceCell" bundle:nil];
        [tableView registerNib:nib forCellReuseIdentifier:cellTableIdentifier];
        nibsRegistered = YES;
    }

    DeviceTableViewCell  *cell = [tableView dequeueReusableCellWithIdentifier:cellTableIdentifier];
    if (cell==nil) {
        cell = [[DeviceTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellTableIdentifier];
    }
    NSUInteger row =[indexPath row];
    NSString *key = [_indexAndDevice objectForKey:[NSString stringWithFormat:@"%d", (NSInteger)row]];
    Device *d = [self.devices objectForKey:key];
    cell.deviceDescribe.text = [d getDescribe];
    //[cell.deviceStatusBt setBackgroundImage:light_on_img forState:UIControlStateNormal];
    [cell.deviceStatusBt setTitle:@"Open" forState:UIControlStateNormal];
    return cell;
    //cell.deviceDescribe =
}
- (void) readStream
{
    Byte buffer[17];
    //NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
    while (recv(CFSocketGetNative(_socket), buffer, sizeof(buffer), 0)) {
            //NSLog(@"%@",[NSString stringWithUTF8String:buffer]);
        
        if (buffer[0]==0x00 && buffer[1]==0x01) {
            NSLog(@"Received a light");
            Light *light = [[Light alloc] initWithData: buffer :17];
            NSLog(@"%d", (int)[light getIndex]);

            NSString *keystr = [[NSString alloc] initWithFormat:@"%d%d", [light getType],[light getIndex]];
            //[self devices]
            [self.devices setValue:light forKey:keystr];
            NSString *strIndex = [NSString stringWithFormat:@"%u", [_indexAndDevice count]];
            [_indexAndDevice setValue:keystr forKey:strIndex];
            [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                //sel.f
                [self.devicesListView reloadData];
            }];
        }
    }
    NSLog(@"EndReadStream");
    
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

    _indexAndDevice = nil;
    _indexAndDevice = [[NSMutableDictionary alloc] init];
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


- (void) networkFailedWithErrorMessage:(NSString *)errormessage
{
    NSLog(@"%@",errormessage);
}
@end
