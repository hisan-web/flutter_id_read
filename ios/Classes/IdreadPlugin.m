#import "IdreadPlugin.h"
#if __has_include(<idread/idread-Swift.h>)
#import <idread/idread-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "idread-Swift.h"
#endif

@implementation IdreadPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftIdreadPlugin registerWithRegistrar:registrar];
}
@end
