#import "PackageinfosPlugin.h"
#import <packageinfos/packageinfos-Swift.h>

@implementation PackageinfosPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftPackageinfosPlugin registerWithRegistrar:registrar];
}
@end
