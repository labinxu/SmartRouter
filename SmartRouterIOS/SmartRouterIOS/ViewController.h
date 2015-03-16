//
//  ViewController.h
//  SmartRouterIOS
//
//  Created by user on 3/10/15.
//  Copyright (c) 2015 STC. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ViewController : UIViewController <UITableViewDataSource, UITableViewDelegate>
@property (weak, nonatomic) IBOutlet UITextField *portTextField;
@property (weak, nonatomic) IBOutlet UITextField *hostAddrTextField;
@property (weak, nonatomic) IBOutlet UITextView *textView;

@property (strong, nonatomic) NSMutableDictionary* devices;

- (IBAction)onClicked:(id)sender;
- (void) networkFailedWithErrorMessage:(NSString*)errormessage;
- (void) didReceiveData:(NSData*) data;
- (void) readStream;
- (void) sendMessage;
@end

