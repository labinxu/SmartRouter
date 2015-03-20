//
//  DeviceDetailsView.m
//  SmartRouterIOS
//
//  Created by labinxu on 15/3/20.
//  Copyright (c) 2015年 STC. All rights reserved.
//

#import "DeviceDetailsView.h"
@implementation DeviceDetailsView
@synthesize device;

-(void ) viewDidLoad
{
    [super viewDidLoad];
    NSLog(@"Received a %@", [self.device getDescribe]);
   // [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notificationHandler:) name:@"myNotification" object:self.device];
    
}
-(void) notificationHandler:(NSNotification*)notification
{
    device = [notification object];
    NSLog(@"Received a %@", [self.device getDescribe]);
}
@end
