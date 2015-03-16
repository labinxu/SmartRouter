//
//  Device.m
//  SmartRouterIOS
//
//  Created by labinxu on 15/3/16.
//  Copyright (c) 2015年 STC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Device.h"
#import "Utils.h"

@implementation Device

-(Byte*) binaryData
{
    return binaryData;
}

-(void)setData:(char *)data
{
    memcpy(binaryData, data, 17);
}

- (int) getIndex
{
    Byte index[4];
    memset(index, 0, 4);
    index[2]=self.binaryData[0];
    index[3]=self.binaryData[1];
    int r = [Utils bytes2int :index :4];
    return r;
}

- (DeviceType) getType{
    return kEnInvalid;
}
- (NSString*) getDescribe
{
    return nil;
}
- (Device*) initWithData:(Byte *)data :(int)lenght
{
    memcpy(binaryData, data, lenght);
    return self;
}

@end


///////////////////////
@implementation Light

-(DeviceType) getType{
    return kEnLight;
}

-(NSString*) getDescribe
{
    return [[NSString alloc] initWithFormat:@"Light %d", [self getIndex] ];
}
@end