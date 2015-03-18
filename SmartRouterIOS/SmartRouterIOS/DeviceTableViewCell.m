//
//  DeviceTableViewCell.m
//  SmartRouterIOS
//
//  Created by labinxu on 15/3/16.
//  Copyright (c) 2015年 STC. All rights reserved.
//

#import "DeviceTableViewCell.h"

@implementation DeviceTableViewCell 

- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (IBAction)onClicked:(id)sender {
    NSLog(@"%@clicked", self.deviceDescribe.text);
    [self.device switchState];
   // [self.device resetOrientation];
    [self.controller sendMessage:self.device.binaryData :17];
}
@end
