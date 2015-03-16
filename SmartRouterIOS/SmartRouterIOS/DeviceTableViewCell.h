//
//  DeviceTableViewCell.h
//  SmartRouterIOS
//
//  Created by labinxu on 15/3/16.
//  Copyright (c) 2015年 STC. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface DeviceTableViewCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIButton *deviceStatusBt;
@property (weak, nonatomic) IBOutlet UILabel *deviceDescribe;

@end
