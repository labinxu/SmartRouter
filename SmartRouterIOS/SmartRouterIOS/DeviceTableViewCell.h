//
//  DeviceTableViewCell.h
//  SmartRouterIOS
//
//  Created by labinxu on 15/3/16.
//  Copyright (c) 2015年 STC. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Device.h"
#import "ViewController.h"
@interface DeviceTableViewCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIButton *deviceStatusBt;
@property (weak, nonatomic) IBOutlet UILabel *deviceDescribe;
@property Device *device;
@property ViewController* controller;
- (IBAction)onClicked:(id)sender;

@end
