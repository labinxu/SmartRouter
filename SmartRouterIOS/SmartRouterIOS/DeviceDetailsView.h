//
//  DeviceDetailsView.h
//  SmartRouterIOS
//
//  Created by labinxu on 15/3/20.
//  Copyright (c) 2015年 STC. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Device.h"
@interface DeviceDetailsView : UIViewController
@property (weak, nonatomic) Device *device;
-(void) notificationHandler:(NSNotification*) notification;
@end
