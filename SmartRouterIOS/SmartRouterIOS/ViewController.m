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
    UIImage *fan_on;
    UIImage *fan_off;
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

    light_on_img = [UIImage imageNamed:@"light_on.png"];
    light_off_img = [UIImage imageNamed:@"light_off.png"];
    light_flash_img = [UIImage imageNamed:@"light_flash.png"];
    
    fan_off = [UIImage imageNamed:@"fan_off.png"];
    fan_on = [UIImage imageNamed:@"fan_run.png"];
    //[self.cnnbt setBackgroundImage:light_on_img forState:UIControlStateNormal];
    self.tableView.rowHeight = 81;
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
    int function = [d getFunctionParam];
    DeviceType dvType = [d getType];
    cell.device = d;
    cell.controller = self;
    if (dvType == kEnLight) {
        if (function==1) {
            [cell.deviceStatusBt setBackgroundImage:light_on_img forState:UIControlStateNormal];
        }
        else if (function==2)
        {
            [cell.deviceStatusBt setBackgroundImage:light_off_img forState:UIControlStateNormal];
        }
        else
        {
            [cell.deviceStatusBt setBackgroundImage:light_flash_img forState:UIControlStateNormal];
        }
    }
    else if (dvType == kEnFan)
    {
        if (function == 1) {
            [cell.deviceStatusBt setBackgroundImage:fan_on forState:UIControlStateNormal];
        }
        else if (function == 2)
        {
            [cell.deviceStatusBt setBackgroundImage:fan_off forState:UIControlStateNormal];
        }
    }
    return cell;
}

- (void) readStream
{
    Byte buffer[17];
    //NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
    while (recv(CFSocketGetNative(_socket), buffer, sizeof(buffer), 0)) {
            //NSLog(@"%@",[NSString stringWithUTF8String:buffer]);
        Device *device = nil;
        if (buffer[0]==0x00 && buffer[1]==0x01) {
            NSLog(@"Received a light");
            device = [[Light alloc] initWithData: buffer :17];
            
        }
        else if(buffer[0]==0x00 && buffer[1] == 0x20)
        {
            NSLog(@"received a fan");
            device = [[Fan alloc] initWithData:buffer :17];
        }
        NSString *keystr = [[NSString alloc] initWithFormat:@"%d%d", [device getType],[device getIndex]];
            //[self devices]
        [self.devices setValue:device forKey:keystr];
        NSString *strIndex = [NSString stringWithFormat:@"%d", [_indexAndDevice count]];
        [_indexAndDevice setValue:keystr forKey:strIndex];
        [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                //sel.f
                [self.devicesListView reloadData];
        }];

    }
    NSLog(@"EndReadStream");
    
}

- (void)sendMessage:(Byte*)data :(int) lenght{
    send(CFSocketGetNative(_socket), data, lenght, 0);

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
