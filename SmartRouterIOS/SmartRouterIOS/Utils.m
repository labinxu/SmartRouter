//
//  Utils.m
//  SmartRouterIOS
//
//  Created by labinxu on 15/3/16.
//  Copyright (c) 2015年 STC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Utils.h"

@implementation Utils

+(int) bytes2int:(Byte*) data :(int) length
{
    Byte tmp[4];
    memset(tmp, 0, 4);
    NSInteger i = 4 - 1;
    NSInteger j = length - 1;
    for (; i >= 0; i--, j--) {
        if (j >=0) {
            tmp[i] = data[j];
        }
        else
        {
            tmp[i] = 0;
        }
        
    }
    int v0 = (tmp[0] & 0xff) << 24;
    int v1 = (tmp[1] & 0xff) << 16;
    int v2 = (tmp[2] & 0xff) << 8;
    int v3 = (tmp[3] & 0xff);
    return v0 + v1 + v2 + v3;
}



@end