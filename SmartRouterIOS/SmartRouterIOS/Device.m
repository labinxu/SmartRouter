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
    index[2]=self.binaryData[2];
    index[3]=self.binaryData[3];
    int r = [Utils bytes2int :index :4];
    return r;
}

- (DeviceType) getType{
    return kEnInvalid;
}
- (NSString*) getDescribe
{
    NSLog(@"parent getDescribe");
    return nil;
}
- (Device*) initWithData:(Byte *)data :(int)lenght
{
    memcpy(binaryData, data, lenght);
    return self;
}

- (int) getParam1
{
    Byte param[4];
    memset(param, 0, 4);
    param[0] = self.binaryData[9];
    memcpy(param, self.binaryData+9, 4);
    return [Utils bytes2int:param :4];
}
- (int) getParam2
{
    Byte param[4];
    memcpy(param, self.binaryData+13, 4);
    return [Utils bytes2int:param :4];
}
- (int) getFunctionParam
{
    Byte param[4];
    memcpy(param, self.binaryData+5, 4);
    return [Utils bytes2int:param :4];
}
-(Byte*) resetOrient
{
    self.binaryData[4] = 0x01;
    return (Byte*)&self.binaryData[0];
}
- (DeviceState)getState
{
    DeviceState state = kEnUnkown;

    int function = [self getFunctionParam];
    switch (function) {
        case 1:
            state = kEnOn;
            break;
        case 2:
            state = kEnOff;
            break;
        case 3 :
            state = kEnFlash;
        default:
            break;
    }
    return state;
}

-(void) switchState
{
    switch ([self getState]) {
        case kEnOn:
            [self turnOff];
            break;
        case kEnOff:
            [self turnOn];
        default:
            break;
    }
}

-(void) turnOff
{
    self.binaryData[8] = 0x02;
}

-(void) turnOn
{
    self.binaryData[8] = 0x01;
}
@end


///////////////////////
@implementation Light

-(DeviceType) getType{
    return kEnLight;
}

-(NSString*) getDescribe
{
    NSString *describe = [[NSString alloc] initWithFormat:@"Light %d", [self getIndex]];
    return describe;
}
@end
//////////////////////
@implementation Fan
- (DeviceType) getType
{
    return kEnFan;
}

- (NSString*) getDescribe
{
    NSString *describe = [[NSString alloc] initWithFormat:@"Fan %d", [self getIndex]];
    return describe;
}
@end
