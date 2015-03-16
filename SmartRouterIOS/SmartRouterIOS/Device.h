//
//  Device.h
//  SmartRouterIOS
//
//  Created by labinxu on 15/3/16.
//  Copyright (c) 2015年 STC. All rights reserved.
//

#ifndef SmartRouterIOS_Device_h
#define SmartRouterIOS_Device_h
typedef enum _DEVICE_TYPE
{
    kEnInvalid = 0,
    kEnLight,
    kEnFan,
    
} DeviceType;
@interface Device : NSObject
{
    Byte binaryData[17];
}
-(Device*) initWithData:(Byte*)data :(int) lenght;
-(Byte*) binaryData;
-(void) setData: (char*)data;
-(DeviceType) getType;
-(int) getIndex;
-(NSString*) getDescribe;
@end


//////////////////////////////////

@interface Light : Device

@end
#endif
