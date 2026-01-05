#import <Foundation/NSArray.h>
#import <Foundation/NSDictionary.h>
#import <Foundation/NSError.h>
#import <Foundation/NSObject.h>
#import <Foundation/NSSet.h>
#import <Foundation/NSString.h>
#import <Foundation/NSValue.h>

@class SharedBeanDefinition<T>, SharedCallbacks<T>, SharedCoreResolver, SharedExtensionManager, SharedFactoryInstanceFactory<T>, SharedInstanceFactory<T>, SharedInstanceFactoryCompanion, SharedInstanceRegistry, SharedKind, SharedKoin, SharedKoinApplication, SharedKoinApplicationCompanion, SharedKoinConfiguration, SharedKoinDefinition<R>, SharedKoinOption, SharedKoinPlatform, SharedKoinPlatformTools, SharedKotlinArray<T>, SharedKotlinEnum<E>, SharedKotlinEnumCompanion, SharedKotlinException, SharedKotlinIllegalStateException, SharedKotlinLazyThreadSafetyMode, SharedKotlinPair<__covariant A, __covariant B>, SharedKotlinRuntimeException, SharedKotlinThrowable, SharedKotlinUnit, SharedLevel, SharedLifecycle_viewmodelViewModel, SharedLockable, SharedLogger, SharedModule, SharedOptionRegistry, SharedParametersHolder, SharedPropertyRegistry, SharedResolutionContext, SharedScope, SharedScopeDSL, SharedScopeRegistry, SharedScopeRegistryCompanion, SharedScopedInstanceFactory<T>, SharedSettingsRepository, SharedSingleInstanceFactory<T>, SharedStringQualifier, SharedTypeQualifier, SharedUserRepository, UIViewController;

@protocol SharedCameraManager, SharedKoinComponent, SharedKoinContext, SharedKoinExtension, SharedKoinScopeComponent, SharedKotlinAutoCloseable, SharedKotlinComparable, SharedKotlinCoroutineContext, SharedKotlinCoroutineContextElement, SharedKotlinCoroutineContextKey, SharedKotlinIterator, SharedKotlinKAnnotatedElement, SharedKotlinKClass, SharedKotlinKClassifier, SharedKotlinKDeclarationContainer, SharedKotlinLazy, SharedKotlinx_coroutines_coreCoroutineScope, SharedKotlinx_coroutines_coreFlow, SharedKotlinx_coroutines_coreFlowCollector, SharedKotlinx_coroutines_coreSharedFlow, SharedKotlinx_coroutines_coreStateFlow, SharedMultiplatform_settingsSettings, SharedQualifier, SharedResolutionExtension, SharedScopeCallback;

NS_ASSUME_NONNULL_BEGIN
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunknown-warning-option"
#pragma clang diagnostic ignored "-Wincompatible-property-type"
#pragma clang diagnostic ignored "-Wnullability"

#pragma push_macro("_Nullable_result")
#if !__has_feature(nullability_nullable_result)
#undef _Nullable_result
#define _Nullable_result _Nullable
#endif

__attribute__((swift_name("KotlinBase")))
@interface SharedBase : NSObject
- (instancetype)init __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
+ (void)initialize __attribute__((objc_requires_super));
@end

@interface SharedBase (SharedBaseCopying) <NSCopying>
@end

__attribute__((swift_name("KotlinMutableSet")))
@interface SharedMutableSet<ObjectType> : NSMutableSet<ObjectType>
@end

__attribute__((swift_name("KotlinMutableDictionary")))
@interface SharedMutableDictionary<KeyType, ObjectType> : NSMutableDictionary<KeyType, ObjectType>
@end

@interface NSError (NSErrorSharedKotlinException)
@property (readonly) id _Nullable kotlinException;
@end

__attribute__((swift_name("KotlinNumber")))
@interface SharedNumber : NSNumber
- (instancetype)initWithChar:(char)value __attribute__((unavailable));
- (instancetype)initWithUnsignedChar:(unsigned char)value __attribute__((unavailable));
- (instancetype)initWithShort:(short)value __attribute__((unavailable));
- (instancetype)initWithUnsignedShort:(unsigned short)value __attribute__((unavailable));
- (instancetype)initWithInt:(int)value __attribute__((unavailable));
- (instancetype)initWithUnsignedInt:(unsigned int)value __attribute__((unavailable));
- (instancetype)initWithLong:(long)value __attribute__((unavailable));
- (instancetype)initWithUnsignedLong:(unsigned long)value __attribute__((unavailable));
- (instancetype)initWithLongLong:(long long)value __attribute__((unavailable));
- (instancetype)initWithUnsignedLongLong:(unsigned long long)value __attribute__((unavailable));
- (instancetype)initWithFloat:(float)value __attribute__((unavailable));
- (instancetype)initWithDouble:(double)value __attribute__((unavailable));
- (instancetype)initWithBool:(BOOL)value __attribute__((unavailable));
- (instancetype)initWithInteger:(NSInteger)value __attribute__((unavailable));
- (instancetype)initWithUnsignedInteger:(NSUInteger)value __attribute__((unavailable));
+ (instancetype)numberWithChar:(char)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedChar:(unsigned char)value __attribute__((unavailable));
+ (instancetype)numberWithShort:(short)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedShort:(unsigned short)value __attribute__((unavailable));
+ (instancetype)numberWithInt:(int)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedInt:(unsigned int)value __attribute__((unavailable));
+ (instancetype)numberWithLong:(long)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedLong:(unsigned long)value __attribute__((unavailable));
+ (instancetype)numberWithLongLong:(long long)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedLongLong:(unsigned long long)value __attribute__((unavailable));
+ (instancetype)numberWithFloat:(float)value __attribute__((unavailable));
+ (instancetype)numberWithDouble:(double)value __attribute__((unavailable));
+ (instancetype)numberWithBool:(BOOL)value __attribute__((unavailable));
+ (instancetype)numberWithInteger:(NSInteger)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedInteger:(NSUInteger)value __attribute__((unavailable));
@end

__attribute__((swift_name("KotlinByte")))
@interface SharedByte : SharedNumber
- (instancetype)initWithChar:(char)value;
+ (instancetype)numberWithChar:(char)value;
@end

__attribute__((swift_name("KotlinUByte")))
@interface SharedUByte : SharedNumber
- (instancetype)initWithUnsignedChar:(unsigned char)value;
+ (instancetype)numberWithUnsignedChar:(unsigned char)value;
@end

__attribute__((swift_name("KotlinShort")))
@interface SharedShort : SharedNumber
- (instancetype)initWithShort:(short)value;
+ (instancetype)numberWithShort:(short)value;
@end

__attribute__((swift_name("KotlinUShort")))
@interface SharedUShort : SharedNumber
- (instancetype)initWithUnsignedShort:(unsigned short)value;
+ (instancetype)numberWithUnsignedShort:(unsigned short)value;
@end

__attribute__((swift_name("KotlinInt")))
@interface SharedInt : SharedNumber
- (instancetype)initWithInt:(int)value;
+ (instancetype)numberWithInt:(int)value;
@end

__attribute__((swift_name("KotlinUInt")))
@interface SharedUInt : SharedNumber
- (instancetype)initWithUnsignedInt:(unsigned int)value;
+ (instancetype)numberWithUnsignedInt:(unsigned int)value;
@end

__attribute__((swift_name("KotlinLong")))
@interface SharedLong : SharedNumber
- (instancetype)initWithLongLong:(long long)value;
+ (instancetype)numberWithLongLong:(long long)value;
@end

__attribute__((swift_name("KotlinULong")))
@interface SharedULong : SharedNumber
- (instancetype)initWithUnsignedLongLong:(unsigned long long)value;
+ (instancetype)numberWithUnsignedLongLong:(unsigned long long)value;
@end

__attribute__((swift_name("KotlinFloat")))
@interface SharedFloat : SharedNumber
- (instancetype)initWithFloat:(float)value;
+ (instancetype)numberWithFloat:(float)value;
@end

__attribute__((swift_name("KotlinDouble")))
@interface SharedDouble : SharedNumber
- (instancetype)initWithDouble:(double)value;
+ (instancetype)numberWithDouble:(double)value;
@end

__attribute__((swift_name("KotlinBoolean")))
@interface SharedBoolean : SharedNumber
- (instancetype)initWithBool:(BOOL)value;
+ (instancetype)numberWithBool:(BOOL)value;
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SettingsRepository")))
@interface SharedSettingsRepository : SharedBase
- (instancetype)initWithSettings:(id<SharedMultiplatform_settingsSettings>)settings __attribute__((swift_name("init(settings:)"))) __attribute__((objc_designated_initializer));
- (void)clear __attribute__((swift_name("clear()")));
- (NSString * _Nullable)getUserName __attribute__((swift_name("getUserName()")));
- (void)saveUserNameName:(NSString *)name __attribute__((swift_name("saveUserName(name:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("UserRepository")))
@interface SharedUserRepository : SharedBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)loginEmail:(NSString *)email password:(NSString *)password completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("login(email:password:completionHandler:)")));
- (void)logout __attribute__((swift_name("logout()")));
@property (readonly) id<SharedKotlinx_coroutines_coreStateFlow> currentUser __attribute__((swift_name("currentUser")));
@end

__attribute__((swift_name("CameraManager")))
@protocol SharedCameraManager
@required
- (void)openCamera __attribute__((swift_name("openCamera()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("IOSCameraManager")))
@interface SharedIOSCameraManager : SharedBase <SharedCameraManager>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (void)openCamera __attribute__((swift_name("openCamera()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("IOSCameraManager_")))
@interface SharedIOSCameraManager_ : SharedBase <SharedCameraManager>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (void)openCamera __attribute__((swift_name("openCamera()")));
@end

__attribute__((swift_name("Lifecycle_viewmodelViewModel")))
@interface SharedLifecycle_viewmodelViewModel : SharedBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithCloseables:(SharedKotlinArray<id<SharedKotlinAutoCloseable>> *)closeables __attribute__((swift_name("init(closeables:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithViewModelScope:(id<SharedKotlinx_coroutines_coreCoroutineScope>)viewModelScope __attribute__((swift_name("init(viewModelScope:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithViewModelScope:(id<SharedKotlinx_coroutines_coreCoroutineScope>)viewModelScope closeables:(SharedKotlinArray<id<SharedKotlinAutoCloseable>> *)closeables __attribute__((swift_name("init(viewModelScope:closeables:)"))) __attribute__((objc_designated_initializer));
- (void)addCloseableCloseable:(id<SharedKotlinAutoCloseable>)closeable __attribute__((swift_name("addCloseable(closeable:)")));
- (void)addCloseableKey:(NSString *)key closeable:(id<SharedKotlinAutoCloseable>)closeable __attribute__((swift_name("addCloseable(key:closeable:)")));
- (id<SharedKotlinAutoCloseable> _Nullable)getCloseableKey:(NSString *)key __attribute__((swift_name("getCloseable(key:)")));

/**
 * @note This method has protected visibility in Kotlin source and is intended only for use by subclasses.
*/
- (void)onCleared __attribute__((swift_name("onCleared()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("WelcomeViewModel")))
@interface SharedWelcomeViewModel : SharedLifecycle_viewmodelViewModel
- (instancetype)initWithUserRepository:(SharedUserRepository *)userRepository cameraManager:(id<SharedCameraManager>)cameraManager settingsRepository:(SharedSettingsRepository *)settingsRepository __attribute__((swift_name("init(userRepository:cameraManager:settingsRepository:)"))) __attribute__((objc_designated_initializer));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
- (instancetype)initWithCloseables:(SharedKotlinArray<id<SharedKotlinAutoCloseable>> *)closeables __attribute__((swift_name("init(closeables:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithViewModelScope:(id<SharedKotlinx_coroutines_coreCoroutineScope>)viewModelScope __attribute__((swift_name("init(viewModelScope:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithViewModelScope:(id<SharedKotlinx_coroutines_coreCoroutineScope>)viewModelScope closeables:(SharedKotlinArray<id<SharedKotlinAutoCloseable>> *)closeables __attribute__((swift_name("init(viewModelScope:closeables:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (void)onCameraButtonClick __attribute__((swift_name("onCameraButtonClick()")));
- (void)updateTextText:(NSString *)text __attribute__((swift_name("updateText(text:)")));
@property (readonly) id<SharedKotlinx_coroutines_coreStateFlow> uiState __attribute__((swift_name("uiState")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Koin")))
@interface SharedKoin : SharedBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (void)close __attribute__((swift_name("close()")));
- (void)createEagerInstances __attribute__((swift_name("createEagerInstances()")));
- (SharedScope *)createScopeT:(id<SharedKoinScopeComponent>)t __attribute__((swift_name("createScope(t:)")));
- (SharedScope *)createScopeScopeId:(NSString *)scopeId __attribute__((swift_name("createScope(scopeId:)")));
- (SharedScope *)createScopeScopeId:(NSString *)scopeId source:(id _Nullable)source scopeArchetype:(SharedTypeQualifier * _Nullable)scopeArchetype __attribute__((swift_name("createScope(scopeId:source:scopeArchetype:)")));
- (SharedScope *)createScopeScopeId:(NSString *)scopeId qualifier:(id<SharedQualifier>)qualifier source:(id _Nullable)source scopeArchetype:(SharedTypeQualifier * _Nullable)scopeArchetype __attribute__((swift_name("createScope(scopeId:qualifier:source:scopeArchetype:)")));
- (void)declareInstance:(id _Nullable)instance qualifier:(id<SharedQualifier> _Nullable)qualifier secondaryTypes:(NSArray<id<SharedKotlinKClass>> *)secondaryTypes allowOverride:(BOOL)allowOverride __attribute__((swift_name("declare(instance:qualifier:secondaryTypes:allowOverride:)")));
- (void)deletePropertyKey:(NSString *)key __attribute__((swift_name("deleteProperty(key:)")));
- (void)deleteScopeScopeId:(NSString *)scopeId __attribute__((swift_name("deleteScope(scopeId:)")));
- (id)getQualifier:(id<SharedQualifier> _Nullable)qualifier parameters:(SharedParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("get(qualifier:parameters:)")));
- (id _Nullable)getClazz:(id<SharedKotlinKClass>)clazz qualifier:(id<SharedQualifier> _Nullable)qualifier parameters:(SharedParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("get(clazz:qualifier:parameters:)")));
- (NSArray<id> *)getAll __attribute__((swift_name("getAll()")));
- (SharedScope *)getOrCreateScopeScopeId:(NSString *)scopeId __attribute__((swift_name("getOrCreateScope(scopeId:)")));
- (SharedScope *)getOrCreateScopeScopeId:(NSString *)scopeId qualifier:(id<SharedQualifier>)qualifier source:(id _Nullable)source __attribute__((swift_name("getOrCreateScope(scopeId:qualifier:source:)")));
- (id _Nullable)getOrNullQualifier:(id<SharedQualifier> _Nullable)qualifier parameters:(SharedParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("getOrNull(qualifier:parameters:)")));
- (id _Nullable)getOrNullClazz:(id<SharedKotlinKClass>)clazz qualifier:(id<SharedQualifier> _Nullable)qualifier parameters:(SharedParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("getOrNull(clazz:qualifier:parameters:)")));
- (id _Nullable)getPropertyKey:(NSString *)key __attribute__((swift_name("getProperty(key:)")));
- (id)getPropertyKey:(NSString *)key defaultValue:(id)defaultValue __attribute__((swift_name("getProperty(key:defaultValue:)")));
- (SharedScope *)getScopeScopeId:(NSString *)scopeId __attribute__((swift_name("getScope(scopeId:)")));
- (SharedScope * _Nullable)getScopeOrNullScopeId:(NSString *)scopeId __attribute__((swift_name("getScopeOrNull(scopeId:)")));
- (id<SharedKotlinLazy>)injectQualifier:(id<SharedQualifier> _Nullable)qualifier mode:(SharedKotlinLazyThreadSafetyMode *)mode parameters:(SharedParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("inject(qualifier:mode:parameters:)")));
- (id<SharedKotlinLazy>)injectOrNullQualifier:(id<SharedQualifier> _Nullable)qualifier mode:(SharedKotlinLazyThreadSafetyMode *)mode parameters:(SharedParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("injectOrNull(qualifier:mode:parameters:)")));
- (void)loadModulesModules:(NSArray<SharedModule *> *)modules allowOverride:(BOOL)allowOverride createEagerInstances:(BOOL)createEagerInstances __attribute__((swift_name("loadModules(modules:allowOverride:createEagerInstances:)")));
- (void)setPropertyKey:(NSString *)key value:(id)value __attribute__((swift_name("setProperty(key:value:)")));
- (void)setupLoggerLogger:(SharedLogger *)logger __attribute__((swift_name("setupLogger(logger:)")));
- (void)unloadModulesModules:(NSArray<SharedModule *> *)modules __attribute__((swift_name("unloadModules(modules:)")));
@property (readonly) SharedExtensionManager *extensionManager __attribute__((swift_name("extensionManager")));
@property (readonly) SharedInstanceRegistry *instanceRegistry __attribute__((swift_name("instanceRegistry")));
@property (readonly) SharedLogger *logger __attribute__((swift_name("logger")));
@property (readonly) SharedOptionRegistry *optionRegistry __attribute__((swift_name("optionRegistry")));
@property (readonly) SharedPropertyRegistry *propertyRegistry __attribute__((swift_name("propertyRegistry")));
@property (readonly) SharedCoreResolver *resolver __attribute__((swift_name("resolver")));
@property (readonly) SharedScopeRegistry *scopeRegistry __attribute__((swift_name("scopeRegistry")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KoinApplication")))
@interface SharedKoinApplication : SharedBase
@property (class, readonly, getter=companion) SharedKoinApplicationCompanion *companion __attribute__((swift_name("companion")));
- (void)allowOverrideOverride:(BOOL)override __attribute__((swift_name("allowOverride(override:)")));
- (void)close __attribute__((swift_name("close()")));
- (void)createEagerInstances __attribute__((swift_name("createEagerInstances()")));
- (SharedKoinApplication *)loggerLogger:(SharedLogger *)logger __attribute__((swift_name("logger(logger:)")));
- (SharedKoinApplication *)modulesModules:(SharedKotlinArray<SharedModule *> *)modules __attribute__((swift_name("modules(modules:)")));
- (SharedKoinApplication *)modulesModules_:(NSArray<SharedModule *> *)modules __attribute__((swift_name("modules(modules_:)")));
- (SharedKoinApplication *)modulesModules__:(SharedModule *)modules __attribute__((swift_name("modules(modules__:)")));
- (SharedKoinApplication *)optionsOptionValue:(SharedKotlinArray<SharedKotlinPair<SharedKoinOption *, id> *> *)optionValue __attribute__((swift_name("options(optionValue:)")));
- (SharedKoinApplication *)printLoggerLevel:(SharedLevel *)level __attribute__((swift_name("printLogger(level:)")));
- (SharedKoinApplication *)propertiesValues:(NSDictionary<NSString *, id> *)values __attribute__((swift_name("properties(values:)")));
@property (readonly) SharedKoin *koin __attribute__((swift_name("koin")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KoinApplication.Companion")))
@interface SharedKoinApplicationCompanion : SharedBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKoinApplicationCompanion *shared __attribute__((swift_name("shared")));
- (SharedKoinApplication *)doInit __attribute__((swift_name("doInit()")));
@end

__attribute__((swift_name("KoinComponent")))
@protocol SharedKoinComponent
@required
- (SharedKoin *)getKoin __attribute__((swift_name("getKoin()")));
@end

__attribute__((swift_name("KoinScopeComponent")))
@protocol SharedKoinScopeComponent <SharedKoinComponent>
@required
@property (readonly) SharedScope *scope __attribute__((swift_name("scope")));
@end

__attribute__((swift_name("KoinContext")))
@protocol SharedKoinContext
@required
- (SharedKoin *)get __attribute__((swift_name("get()")));
- (SharedKoin * _Nullable)getOrNull __attribute__((swift_name("getOrNull()")));
- (void)loadKoinModulesModules:(NSArray<SharedModule *> *)modules createEagerInstances:(BOOL)createEagerInstances __attribute__((swift_name("loadKoinModules(modules:createEagerInstances:)")));
- (void)loadKoinModulesModule:(SharedModule *)module createEagerInstances:(BOOL)createEagerInstances __attribute__((swift_name("loadKoinModules(module:createEagerInstances:)")));
- (SharedKoinApplication *)startKoinAppDeclaration:(void (^)(SharedKoinApplication *))appDeclaration __attribute__((swift_name("startKoin(appDeclaration:)")));
- (SharedKoinApplication *)startKoinKoinApplication:(SharedKoinApplication *)koinApplication __attribute__((swift_name("startKoin(koinApplication:)")));
- (void)stopKoin __attribute__((swift_name("stopKoin()")));
- (void)unloadKoinModulesModules:(NSArray<SharedModule *> *)modules __attribute__((swift_name("unloadKoinModules(modules:)")));
- (void)unloadKoinModulesModule:(SharedModule *)module __attribute__((swift_name("unloadKoinModules(module:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("BeanDefinition")))
@interface SharedBeanDefinition<T> : SharedBase
- (instancetype)initWithScopeQualifier:(id<SharedQualifier>)scopeQualifier primaryType:(id<SharedKotlinKClass>)primaryType qualifier:(id<SharedQualifier> _Nullable)qualifier definition:(T _Nullable (^)(SharedScope *, SharedParametersHolder *))definition kind:(SharedKind *)kind secondaryTypes:(NSArray<id<SharedKotlinKClass>> *)secondaryTypes __attribute__((swift_name("init(scopeQualifier:primaryType:qualifier:definition:kind:secondaryTypes:)"))) __attribute__((objc_designated_initializer));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (BOOL)hasTypeClazz:(id<SharedKotlinKClass>)clazz __attribute__((swift_name("hasType(clazz:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (BOOL)isClazz:(id<SharedKotlinKClass>)clazz qualifier:(id<SharedQualifier> _Nullable)qualifier scopeDefinition:(id<SharedQualifier>)scopeDefinition __attribute__((swift_name("is(clazz:qualifier:scopeDefinition:)")));
- (NSString *)description __attribute__((swift_name("description()")));
@property SharedCallbacks<T> *callbacks __attribute__((swift_name("callbacks")));
@property (readonly) T _Nullable (^definition)(SharedScope *, SharedParametersHolder *) __attribute__((swift_name("definition")));
@property (readonly) SharedKind *kind __attribute__((swift_name("kind")));
@property (readonly) id<SharedKotlinKClass> primaryType __attribute__((swift_name("primaryType")));
@property id<SharedQualifier> _Nullable qualifier __attribute__((swift_name("qualifier")));
@property (readonly) id<SharedQualifier> scopeQualifier __attribute__((swift_name("scopeQualifier")));
@property NSArray<id<SharedKotlinKClass>> *secondaryTypes __attribute__((swift_name("secondaryTypes")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Callbacks")))
@interface SharedCallbacks<T> : SharedBase
- (instancetype)initWithOnClose:(void (^ _Nullable)(T _Nullable))onClose __attribute__((swift_name("init(onClose:)"))) __attribute__((objc_designated_initializer));
- (SharedCallbacks<T> *)doCopyOnClose:(void (^ _Nullable)(T _Nullable))onClose __attribute__((swift_name("doCopy(onClose:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) void (^ _Nullable onClose)(T _Nullable) __attribute__((swift_name("onClose")));
@end

__attribute__((swift_name("KotlinComparable")))
@protocol SharedKotlinComparable
@required
- (int32_t)compareToOther:(id _Nullable)other __attribute__((swift_name("compareTo(other:)")));
@end

__attribute__((swift_name("KotlinEnum")))
@interface SharedKotlinEnum<E> : SharedBase <SharedKotlinComparable>
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) SharedKotlinEnumCompanion *companion __attribute__((swift_name("companion")));
- (int32_t)compareToOther:(E)other __attribute__((swift_name("compareTo(other:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@property (readonly) int32_t ordinal __attribute__((swift_name("ordinal")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Kind")))
@interface SharedKind : SharedKotlinEnum<SharedKind *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedKind *singleton __attribute__((swift_name("singleton")));
@property (class, readonly) SharedKind *factory __attribute__((swift_name("factory")));
@property (class, readonly) SharedKind *scoped __attribute__((swift_name("scoped")));
+ (SharedKotlinArray<SharedKind *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedKind *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KoinDefinition")))
@interface SharedKoinDefinition<R> : SharedBase
- (instancetype)initWithModule:(SharedModule *)module factory:(SharedInstanceFactory<R> *)factory __attribute__((swift_name("init(module:factory:)"))) __attribute__((objc_designated_initializer));
- (SharedKoinDefinition<R> *)doCopyModule:(SharedModule *)module factory:(SharedInstanceFactory<R> *)factory __attribute__((swift_name("doCopy(module:factory:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) SharedInstanceFactory<R> *factory __attribute__((swift_name("factory")));
@property (readonly) SharedModule *module __attribute__((swift_name("module")));
@end

__attribute__((swift_name("KotlinThrowable")))
@interface SharedKotlinThrowable : SharedBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));

/**
 * @note annotations
 *   kotlin.experimental.ExperimentalNativeApi
*/
- (SharedKotlinArray<NSString *> *)getStackTrace __attribute__((swift_name("getStackTrace()")));
- (void)printStackTrace __attribute__((swift_name("printStackTrace()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) SharedKotlinThrowable * _Nullable cause __attribute__((swift_name("cause")));
@property (readonly) NSString * _Nullable message __attribute__((swift_name("message")));
- (NSError *)asError __attribute__((swift_name("asError()")));
@end

__attribute__((swift_name("KotlinException")))
@interface SharedKotlinException : SharedKotlinThrowable
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ClosedScopeException")))
@interface SharedClosedScopeException : SharedKotlinException
- (instancetype)initWithMsg:(NSString *)msg __attribute__((swift_name("init(msg:)"))) __attribute__((objc_designated_initializer));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("DefinitionOverrideException")))
@interface SharedDefinitionOverrideException : SharedKotlinException
- (instancetype)initWithMsg:(NSString *)msg __attribute__((swift_name("init(msg:)"))) __attribute__((objc_designated_initializer));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("DefinitionParameterException")))
@interface SharedDefinitionParameterException : SharedKotlinException
- (instancetype)initWithStr:(NSString *)str __attribute__((swift_name("init(str:)"))) __attribute__((objc_designated_initializer));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("InstanceCreationException")))
@interface SharedInstanceCreationException : SharedKotlinException
- (instancetype)initWithMsg:(NSString *)msg parent:(SharedKotlinException *)parent __attribute__((swift_name("init(msg:parent:)"))) __attribute__((objc_designated_initializer));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KoinApplicationAlreadyStartedException")))
@interface SharedKoinApplicationAlreadyStartedException : SharedKotlinException
- (instancetype)initWithMsg:(NSString *)msg __attribute__((swift_name("init(msg:)"))) __attribute__((objc_designated_initializer));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("MissingPropertyException")))
@interface SharedMissingPropertyException : SharedKotlinException
- (instancetype)initWithMsg:(NSString *)msg __attribute__((swift_name("init(msg:)"))) __attribute__((objc_designated_initializer));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("MissingScopeValueException")))
@interface SharedMissingScopeValueException : SharedKotlinException
- (instancetype)initWithMsg:(NSString *)msg __attribute__((swift_name("init(msg:)"))) __attribute__((objc_designated_initializer));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("NoDefinitionFoundException")))
@interface SharedNoDefinitionFoundException : SharedKotlinException
- (instancetype)initWithMsg:(NSString *)msg __attribute__((swift_name("init(msg:)"))) __attribute__((objc_designated_initializer));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("NoParameterFoundException")))
@interface SharedNoParameterFoundException : SharedKotlinException
- (instancetype)initWithMsg:(NSString *)msg __attribute__((swift_name("init(msg:)"))) __attribute__((objc_designated_initializer));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("NoPropertyFileFoundException")))
@interface SharedNoPropertyFileFoundException : SharedKotlinException
- (instancetype)initWithMsg:(NSString *)msg __attribute__((swift_name("init(msg:)"))) __attribute__((objc_designated_initializer));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("NoScopeDefFoundException")))
@interface SharedNoScopeDefFoundException : SharedKotlinException
- (instancetype)initWithS:(NSString *)s __attribute__((swift_name("init(s:)"))) __attribute__((objc_designated_initializer));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ScopeAlreadyCreatedException")))
@interface SharedScopeAlreadyCreatedException : SharedKotlinException
- (instancetype)initWithS:(NSString *)s __attribute__((swift_name("init(s:)"))) __attribute__((objc_designated_initializer));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ScopeNotCreatedException")))
@interface SharedScopeNotCreatedException : SharedKotlinException
- (instancetype)initWithMsg:(NSString *)msg __attribute__((swift_name("init(msg:)"))) __attribute__((objc_designated_initializer));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ExtensionManager")))
@interface SharedExtensionManager : SharedBase
- (instancetype)initWith_koin:(SharedKoin *)_koin __attribute__((swift_name("init(_koin:)"))) __attribute__((objc_designated_initializer));
- (void)close __attribute__((swift_name("close()")));
- (id<SharedKoinExtension>)getExtensionId:(NSString *)id __attribute__((swift_name("getExtension(id:)")));
- (id<SharedKoinExtension> _Nullable)getExtensionOrNullId:(NSString *)id __attribute__((swift_name("getExtensionOrNull(id:)")));
- (void)registerExtensionId:(NSString *)id extension:(id<SharedKoinExtension>)extension __attribute__((swift_name("registerExtension(id:extension:)")));
@end

__attribute__((swift_name("KoinExtension")))
@protocol SharedKoinExtension
@required
- (void)onClose __attribute__((swift_name("onClose()")));
- (void)onRegisterKoin:(SharedKoin *)koin __attribute__((swift_name("onRegister(koin:)")));
@end

__attribute__((swift_name("Lockable")))
@interface SharedLockable : SharedBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
@end

__attribute__((swift_name("InstanceFactory")))
@interface SharedInstanceFactory<T> : SharedLockable
- (instancetype)initWithBeanDefinition:(SharedBeanDefinition<T> *)beanDefinition __attribute__((swift_name("init(beanDefinition:)"))) __attribute__((objc_designated_initializer));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
@property (class, readonly, getter=companion) SharedInstanceFactoryCompanion *companion __attribute__((swift_name("companion")));
- (T _Nullable)createContext:(SharedResolutionContext *)context __attribute__((swift_name("create(context:)")));
- (void)dropScope:(SharedScope * _Nullable)scope __attribute__((swift_name("drop(scope:)")));
- (void)dropAll __attribute__((swift_name("dropAll()")));
- (T _Nullable)getContext:(SharedResolutionContext *)context __attribute__((swift_name("get(context:)")));
- (BOOL)isCreatedContext:(SharedResolutionContext * _Nullable)context __attribute__((swift_name("isCreated(context:)")));
@property (readonly) SharedBeanDefinition<T> *beanDefinition __attribute__((swift_name("beanDefinition")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("FactoryInstanceFactory")))
@interface SharedFactoryInstanceFactory<T> : SharedInstanceFactory<T>
- (instancetype)initWithBeanDefinition:(SharedBeanDefinition<T> *)beanDefinition __attribute__((swift_name("init(beanDefinition:)"))) __attribute__((objc_designated_initializer));
- (void)dropScope:(SharedScope * _Nullable)scope __attribute__((swift_name("drop(scope:)")));
- (void)dropAll __attribute__((swift_name("dropAll()")));
- (T _Nullable)getContext:(SharedResolutionContext *)context __attribute__((swift_name("get(context:)")));
- (BOOL)isCreatedContext:(SharedResolutionContext * _Nullable)context __attribute__((swift_name("isCreated(context:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("InstanceFactoryCompanion")))
@interface SharedInstanceFactoryCompanion : SharedBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedInstanceFactoryCompanion *shared __attribute__((swift_name("shared")));
@property (readonly) NSString *ERROR_SEPARATOR __attribute__((swift_name("ERROR_SEPARATOR")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("NoClass")))
@interface SharedNoClass : SharedBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ResolutionContext")))
@interface SharedResolutionContext : SharedBase
- (instancetype)initWithLogger:(SharedLogger *)logger scope:(SharedScope *)scope clazz:(id<SharedKotlinKClass>)clazz qualifier:(id<SharedQualifier> _Nullable)qualifier parameters:(SharedParametersHolder * _Nullable)parameters __attribute__((swift_name("init(logger:scope:clazz:qualifier:parameters:)"))) __attribute__((objc_designated_initializer));
- (SharedResolutionContext *)doNewContextForScopeS:(SharedScope *)s __attribute__((swift_name("doNewContextForScope(s:)")));
@property (readonly) id<SharedKotlinKClass> clazz __attribute__((swift_name("clazz")));
@property (readonly) NSString *debugTag __attribute__((swift_name("debugTag")));
@property (readonly) SharedLogger *logger __attribute__((swift_name("logger")));
@property (readonly) SharedParametersHolder * _Nullable parameters __attribute__((swift_name("parameters")));
@property (readonly) id<SharedQualifier> _Nullable qualifier __attribute__((swift_name("qualifier")));
@property (readonly) SharedScope *scope __attribute__((swift_name("scope")));
@property SharedTypeQualifier * _Nullable scopeArchetype __attribute__((swift_name("scopeArchetype")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ScopedInstanceFactory")))
@interface SharedScopedInstanceFactory<T> : SharedInstanceFactory<T>
- (instancetype)initWithBeanDefinition:(SharedBeanDefinition<T> *)beanDefinition holdInstance:(BOOL)holdInstance __attribute__((swift_name("init(beanDefinition:holdInstance:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithBeanDefinition:(SharedBeanDefinition<id> *)beanDefinition __attribute__((swift_name("init(beanDefinition:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (T _Nullable)createContext:(SharedResolutionContext *)context __attribute__((swift_name("create(context:)")));
- (void)dropScope:(SharedScope * _Nullable)scope __attribute__((swift_name("drop(scope:)")));
- (void)dropAll __attribute__((swift_name("dropAll()")));
- (T _Nullable)getContext:(SharedResolutionContext *)context __attribute__((swift_name("get(context:)")));
- (BOOL)isCreatedContext:(SharedResolutionContext * _Nullable)context __attribute__((swift_name("isCreated(context:)")));
- (void)refreshInstanceScopeID:(NSString *)scopeID instance:(id)instance __attribute__((swift_name("refreshInstance(scopeID:instance:)")));
- (int32_t)size __attribute__((swift_name("size()")));
@property (readonly) BOOL holdInstance __attribute__((swift_name("holdInstance")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SingleInstanceFactory")))
@interface SharedSingleInstanceFactory<T> : SharedInstanceFactory<T>
- (instancetype)initWithBeanDefinition:(SharedBeanDefinition<T> *)beanDefinition __attribute__((swift_name("init(beanDefinition:)"))) __attribute__((objc_designated_initializer));
- (T _Nullable)createContext:(SharedResolutionContext *)context __attribute__((swift_name("create(context:)")));
- (void)dropScope:(SharedScope * _Nullable)scope __attribute__((swift_name("drop(scope:)")));
- (void)dropAll __attribute__((swift_name("dropAll()")));
- (T _Nullable)getContext:(SharedResolutionContext *)context __attribute__((swift_name("get(context:)")));
- (BOOL)isCreatedContext:(SharedResolutionContext * _Nullable)context __attribute__((swift_name("isCreated(context:)")));
@end

__attribute__((swift_name("Logger")))
@interface SharedLogger : SharedBase
- (instancetype)initWithLevel:(SharedLevel *)level __attribute__((swift_name("init(level:)"))) __attribute__((objc_designated_initializer));
- (void)debugMsg:(NSString *)msg __attribute__((swift_name("debug(msg:)")));
- (void)displayLevel:(SharedLevel *)level msg:(NSString *)msg __attribute__((swift_name("display(level:msg:)")));
- (void)errorMsg:(NSString *)msg __attribute__((swift_name("error(msg:)")));
- (void)infoMsg:(NSString *)msg __attribute__((swift_name("info(msg:)")));
- (BOOL)isAtLvl:(SharedLevel *)lvl __attribute__((swift_name("isAt(lvl:)")));
- (void)logLvl:(SharedLevel *)lvl msg:(NSString *(^)(void))msg __attribute__((swift_name("log(lvl:msg:)")));
- (void)logLvl:(SharedLevel *)lvl msg_:(NSString *)msg __attribute__((swift_name("log(lvl:msg_:)")));
- (void)warnMsg:(NSString *)msg __attribute__((swift_name("warn(msg:)")));
@property SharedLevel *level __attribute__((swift_name("level")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("EmptyLogger")))
@interface SharedEmptyLogger : SharedLogger
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithLevel:(SharedLevel *)level __attribute__((swift_name("init(level:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (void)displayLevel:(SharedLevel *)level msg:(NSString *)msg __attribute__((swift_name("display(level:msg:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Level")))
@interface SharedLevel : SharedKotlinEnum<SharedLevel *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedLevel *debug __attribute__((swift_name("debug")));
@property (class, readonly) SharedLevel *info __attribute__((swift_name("info")));
@property (class, readonly) SharedLevel *warning __attribute__((swift_name("warning")));
@property (class, readonly) SharedLevel *error __attribute__((swift_name("error")));
@property (class, readonly) SharedLevel *none __attribute__((swift_name("none")));
+ (SharedKotlinArray<SharedLevel *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedLevel *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("PrintLogger")))
@interface SharedPrintLogger : SharedLogger
- (instancetype)initWithLevel:(SharedLevel *)level __attribute__((swift_name("init(level:)"))) __attribute__((objc_designated_initializer));
- (void)displayLevel:(SharedLevel *)level msg:(NSString *)msg __attribute__((swift_name("display(level:msg:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Module")))
@interface SharedModule : SharedBase
- (instancetype)initWith_createdAtStart:(BOOL)_createdAtStart __attribute__((swift_name("init(_createdAtStart:)"))) __attribute__((objc_designated_initializer));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (SharedKoinDefinition<id> *)factoryQualifier:(id<SharedQualifier> _Nullable)qualifier definition:(id _Nullable (^)(SharedScope *, SharedParametersHolder *))definition __attribute__((swift_name("factory(qualifier:definition:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (void)includesModule:(SharedKotlinArray<SharedModule *> *)module __attribute__((swift_name("includes(module:)")));
- (void)includesModule_:(id)module __attribute__((swift_name("includes(module_:)")));
- (void)indexPrimaryTypeInstanceFactory:(SharedInstanceFactory<id> *)instanceFactory __attribute__((swift_name("indexPrimaryType(instanceFactory:)")));
- (void)indexSecondaryTypesInstanceFactory:(SharedInstanceFactory<id> *)instanceFactory __attribute__((swift_name("indexSecondaryTypes(instanceFactory:)")));
- (NSArray<SharedModule *> *)plusModules:(NSArray<SharedModule *> *)modules __attribute__((swift_name("plus(modules:)")));
- (NSArray<SharedModule *> *)plusModule:(SharedModule *)module __attribute__((swift_name("plus(module:)")));
- (void)prepareForCreationAtStartInstanceFactory:(SharedSingleInstanceFactory<id> *)instanceFactory __attribute__((swift_name("prepareForCreationAtStart(instanceFactory:)")));
- (void)scopeScopeSet:(void (^)(SharedScopeDSL *))scopeSet __attribute__((swift_name("scope(scopeSet:)")));
- (void)scopeQualifier:(id<SharedQualifier>)qualifier scopeSet:(void (^)(SharedScopeDSL *))scopeSet __attribute__((swift_name("scope(qualifier:scopeSet:)")));
- (SharedKoinDefinition<id> *)singleQualifier:(id<SharedQualifier> _Nullable)qualifier createdAtStart:(BOOL)createdAtStart definition:(id _Nullable (^)(SharedScope *, SharedParametersHolder *))definition __attribute__((swift_name("single(qualifier:createdAtStart:definition:)")));
@property (readonly) SharedMutableSet<SharedSingleInstanceFactory<id> *> *eagerInstances __attribute__((swift_name("eagerInstances")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) NSMutableArray<SharedModule *> *includedModules __attribute__((swift_name("includedModules")));
@property (readonly) BOOL isLoaded __attribute__((swift_name("isLoaded")));
@property (readonly) SharedMutableDictionary<NSString *, SharedInstanceFactory<id> *> *mappings __attribute__((swift_name("mappings")));
@property (readonly) SharedMutableSet<id<SharedQualifier>> *scopes __attribute__((swift_name("scopes")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KoinOption")))
@interface SharedKoinOption : SharedKotlinEnum<SharedKoinOption *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedKoinOption *viewmodelScopeFactory __attribute__((swift_name("viewmodelScopeFactory")));
+ (SharedKotlinArray<SharedKoinOption *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedKoinOption *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((swift_name("ParametersHolder")))
@interface SharedParametersHolder : SharedBase
- (instancetype)initWith_values:(NSMutableArray<id> *)_values useIndexedValues:(SharedBoolean * _Nullable)useIndexedValues __attribute__((swift_name("init(_values:useIndexedValues:)"))) __attribute__((objc_designated_initializer));
- (SharedParametersHolder *)addValue:(id)value __attribute__((swift_name("add(value:)")));
- (id _Nullable)component1 __attribute__((swift_name("component1()")));
- (id _Nullable)component2 __attribute__((swift_name("component2()")));
- (id _Nullable)component3 __attribute__((swift_name("component3()")));
- (id _Nullable)component4 __attribute__((swift_name("component4()")));
- (id _Nullable)component5 __attribute__((swift_name("component5()")));
- (id _Nullable)elementAtI:(int32_t)i clazz:(id<SharedKotlinKClass>)clazz __attribute__((swift_name("elementAt(i:clazz:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (id)get __attribute__((swift_name("get()")));
- (id _Nullable)getI:(int32_t)i __attribute__((swift_name("get(i:)")));
- (id _Nullable)getOrNull __attribute__((swift_name("getOrNull()")));
- (id _Nullable)getOrNullClazz:(id<SharedKotlinKClass>)clazz __attribute__((swift_name("getOrNull(clazz:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (SharedParametersHolder *)insertIndex:(int32_t)index value:(id)value __attribute__((swift_name("insert(index:value:)")));
- (BOOL)isEmpty __attribute__((swift_name("isEmpty()")));
- (BOOL)isNotEmpty __attribute__((swift_name("isNotEmpty()")));
- (void)setI:(int32_t)i t:(id _Nullable)t __attribute__((swift_name("set(i:t:)")));
- (int32_t)size __attribute__((swift_name("size()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property int32_t index __attribute__((swift_name("index")));
@property (readonly) SharedBoolean * _Nullable useIndexedValues __attribute__((swift_name("useIndexedValues")));
@property (readonly) NSArray<id> *values __attribute__((swift_name("values")));
@end

__attribute__((swift_name("Qualifier")))
@protocol SharedQualifier
@required
@property (readonly) NSString *value __attribute__((swift_name("value")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("StringQualifier")))
@interface SharedStringQualifier : SharedBase <SharedQualifier>
- (instancetype)initWithValue:(NSString *)value __attribute__((swift_name("init(value:)"))) __attribute__((objc_designated_initializer));
- (SharedStringQualifier *)doCopyValue:(NSString *)value __attribute__((swift_name("doCopy(value:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *value __attribute__((swift_name("value")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("TypeQualifier")))
@interface SharedTypeQualifier : SharedBase <SharedQualifier>
- (instancetype)initWithType:(id<SharedKotlinKClass>)type __attribute__((swift_name("init(type:)"))) __attribute__((objc_designated_initializer));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) id<SharedKotlinKClass> type __attribute__((swift_name("type")));
@property (readonly) NSString *value __attribute__((swift_name("value")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("InstanceRegistry")))
@interface SharedInstanceRegistry : SharedBase
- (instancetype)initWith_koin:(SharedKoin *)_koin __attribute__((swift_name("init(_koin:)"))) __attribute__((objc_designated_initializer));
- (void)saveMappingAllowOverride:(BOOL)allowOverride mapping:(NSString *)mapping factory:(SharedInstanceFactory<id> *)factory logWarning:(BOOL)logWarning __attribute__((swift_name("saveMapping(allowOverride:mapping:factory:logWarning:)")));
- (int32_t)size __attribute__((swift_name("size()")));
@property (readonly) SharedKoin *_koin __attribute__((swift_name("_koin")));
@property (readonly) NSDictionary<NSString *, SharedInstanceFactory<id> *> *instances __attribute__((swift_name("instances")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("OptionRegistry")))
@interface SharedOptionRegistry : SharedBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("PropertyRegistry")))
@interface SharedPropertyRegistry : SharedBase
- (instancetype)initWith_koin:(SharedKoin *)_koin __attribute__((swift_name("init(_koin:)"))) __attribute__((objc_designated_initializer));
- (void)close __attribute__((swift_name("close()")));
- (void)deletePropertyKey:(NSString *)key __attribute__((swift_name("deleteProperty(key:)")));
- (id _Nullable)getPropertyKey:(NSString *)key __attribute__((swift_name("getProperty(key:)")));
- (void)savePropertiesProperties:(NSDictionary<NSString *, id> *)properties __attribute__((swift_name("saveProperties(properties:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ScopeRegistry")))
@interface SharedScopeRegistry : SharedBase
- (instancetype)initWith_koin:(SharedKoin *)_koin __attribute__((swift_name("init(_koin:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) SharedScopeRegistryCompanion *companion __attribute__((swift_name("companion")));
- (void)loadScopesModules:(NSSet<SharedModule *> *)modules __attribute__((swift_name("loadScopes(modules:)")));
@property (readonly) SharedScope *rootScope __attribute__((swift_name("rootScope")));
@property (readonly) NSSet<id<SharedQualifier>> *scopeDefinitions __attribute__((swift_name("scopeDefinitions")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ScopeRegistry.Companion")))
@interface SharedScopeRegistryCompanion : SharedBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedScopeRegistryCompanion *shared __attribute__((swift_name("shared")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("CoreResolver")))
@interface SharedCoreResolver : SharedBase
- (instancetype)initWith_koin:(SharedKoin *)_koin __attribute__((swift_name("init(_koin:)"))) __attribute__((objc_designated_initializer));
- (void)addResolutionExtensionResolutionExtension:(id<SharedResolutionExtension>)resolutionExtension __attribute__((swift_name("addResolutionExtension(resolutionExtension:)")));
- (id _Nullable)resolveFromContextScope:(SharedScope *)scope instanceContext:(SharedResolutionContext *)instanceContext __attribute__((swift_name("resolveFromContext(scope:instanceContext:)")));
@end

__attribute__((swift_name("ResolutionExtension")))
@protocol SharedResolutionExtension
@required
- (id _Nullable)resolveScope:(SharedScope *)scope instanceContext:(SharedResolutionContext *)instanceContext __attribute__((swift_name("resolve(scope:instanceContext:)")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Scope")))
@interface SharedScope : SharedLockable
- (instancetype)initWithScopeQualifier:(id<SharedQualifier>)scopeQualifier id:(NSString *)id isRoot:(BOOL)isRoot scopeArchetype:(SharedTypeQualifier * _Nullable)scopeArchetype _koin:(SharedKoin *)_koin __attribute__((swift_name("init(scopeQualifier:id:isRoot:scopeArchetype:_koin:)"))) __attribute__((objc_designated_initializer));
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
- (void)close __attribute__((swift_name("close()")));
- (void)declareInstance:(id _Nullable)instance qualifier:(id<SharedQualifier> _Nullable)qualifier secondaryTypes:(NSArray<id<SharedKotlinKClass>> *)secondaryTypes allowOverride:(BOOL)allowOverride holdInstance:(BOOL)holdInstance __attribute__((swift_name("declare(instance:qualifier:secondaryTypes:allowOverride:holdInstance:)")));
- (id)getQualifier:(id<SharedQualifier> _Nullable)qualifier parameters:(SharedParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("get(qualifier:parameters:)")));
- (id _Nullable)getClazz:(id<SharedKotlinKClass>)clazz qualifier:(id<SharedQualifier> _Nullable)qualifier parameters:(SharedParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("get(clazz:qualifier:parameters:)")));
- (NSArray<id> *)getAll __attribute__((swift_name("getAll()")));
- (NSArray<id> *)getAllClazz:(id<SharedKotlinKClass>)clazz __attribute__((swift_name("getAll(clazz:)")));
- (SharedKoin *)getKoin __attribute__((swift_name("getKoin()")));
- (NSArray<NSString *> *)getLinkedScopeIds __attribute__((swift_name("getLinkedScopeIds()")));
- (id _Nullable)getOrNullQualifier:(id<SharedQualifier> _Nullable)qualifier parameters:(SharedParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("getOrNull(qualifier:parameters:)")));
- (id _Nullable)getOrNullClazz:(id<SharedKotlinKClass>)clazz qualifier:(id<SharedQualifier> _Nullable)qualifier parameters:(SharedParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("getOrNull(clazz:qualifier:parameters:)")));
- (id)getPropertyKey:(NSString *)key __attribute__((swift_name("getProperty(key:)")));
- (id)getPropertyKey:(NSString *)key defaultValue:(id)defaultValue __attribute__((swift_name("getProperty(key:defaultValue:)")));
- (id _Nullable)getPropertyOrNullKey:(NSString *)key __attribute__((swift_name("getPropertyOrNull(key:)")));
- (SharedScope *)getScopeScopeID:(NSString *)scopeID __attribute__((swift_name("getScope(scopeID:)")));
- (id _Nullable)getSource __attribute__((swift_name("getSource()")));
- (id _Nullable)getWithParametersClazz:(id<SharedKotlinKClass>)clazz qualifier:(id<SharedQualifier> _Nullable)qualifier parameters:(SharedParametersHolder * _Nullable)parameters __attribute__((swift_name("getWithParameters(clazz:qualifier:parameters:)")));
- (id<SharedKotlinLazy>)injectQualifier:(id<SharedQualifier> _Nullable)qualifier mode:(SharedKotlinLazyThreadSafetyMode *)mode parameters:(SharedParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("inject(qualifier:mode:parameters:)")));
- (id<SharedKotlinLazy>)injectOrNullQualifier:(id<SharedQualifier> _Nullable)qualifier mode:(SharedKotlinLazyThreadSafetyMode *)mode parameters:(SharedParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("injectOrNull(qualifier:mode:parameters:)")));
- (BOOL)isNotClosed __attribute__((swift_name("isNotClosed()")));
- (void)linkToScopes:(SharedKotlinArray<SharedScope *> *)scopes __attribute__((swift_name("linkTo(scopes:)")));
- (void)registerCallbackCallback:(id<SharedScopeCallback>)callback __attribute__((swift_name("registerCallback(callback:)")));
- (NSString *)description __attribute__((swift_name("description()")));
- (void)unlinkScopes:(SharedKotlinArray<SharedScope *> *)scopes __attribute__((swift_name("unlink(scopes:)")));
@property (readonly) BOOL closed __attribute__((swift_name("closed")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) BOOL isRoot __attribute__((swift_name("isRoot")));
@property (readonly) SharedLogger *logger __attribute__((swift_name("logger")));
@property (readonly) SharedTypeQualifier * _Nullable scopeArchetype __attribute__((swift_name("scopeArchetype")));
@property (readonly) id<SharedQualifier> scopeQualifier __attribute__((swift_name("scopeQualifier")));
@property id _Nullable sourceValue __attribute__((swift_name("sourceValue")));
@end

__attribute__((swift_name("ScopeCallback")))
@protocol SharedScopeCallback
@required
- (void)onScopeCloseScope:(SharedScope *)scope __attribute__((swift_name("onScopeClose(scope:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KoinConfiguration")))
@interface SharedKoinConfiguration : SharedBase
- (instancetype)initWithConfig:(void (^)(SharedKoinApplication *))config __attribute__((swift_name("init(config:)"))) __attribute__((objc_designated_initializer));
- (void (^)(SharedKoinApplication *))invoke __attribute__((swift_name("invoke()")));
@property (readonly) void (^appDeclaration)(SharedKoinApplication *) __attribute__((swift_name("appDeclaration")));
@property (readonly) void (^config)(SharedKoinApplication *) __attribute__((swift_name("config")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ScopeDSL")))
@interface SharedScopeDSL : SharedBase
- (instancetype)initWithScopeQualifier:(id<SharedQualifier>)scopeQualifier module:(SharedModule *)module __attribute__((swift_name("init(scopeQualifier:module:)"))) __attribute__((objc_designated_initializer));
- (SharedKoinDefinition<id> *)factoryQualifier:(id<SharedQualifier> _Nullable)qualifier definition:(id _Nullable (^)(SharedScope *, SharedParametersHolder *))definition __attribute__((swift_name("factory(qualifier:definition:)")));
- (SharedKoinDefinition<id> *)scopedQualifier:(id<SharedQualifier> _Nullable)qualifier definition:(id _Nullable (^)(SharedScope *, SharedParametersHolder *))definition __attribute__((swift_name("scoped(qualifier:definition:)")));
@property (readonly) SharedModule *module __attribute__((swift_name("module")));
@property (readonly) id<SharedQualifier> scopeQualifier __attribute__((swift_name("scopeQualifier")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KoinPlatform")))
@interface SharedKoinPlatform : SharedBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)koinPlatform __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKoinPlatform *shared __attribute__((swift_name("shared")));
- (SharedKoin *)getKoin __attribute__((swift_name("getKoin()")));
- (SharedKoin * _Nullable)getKoinOrNull __attribute__((swift_name("getKoinOrNull()")));
- (void)startKoinModules:(NSArray<SharedModule *> *)modules level:(SharedLevel *)level __attribute__((swift_name("startKoin(modules:level:)")));
- (void)stopKoin __attribute__((swift_name("stopKoin()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KoinPlatformTools")))
@interface SharedKoinPlatformTools : SharedBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)koinPlatformTools __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKoinPlatformTools *shared __attribute__((swift_name("shared")));
- (id<SharedKoinContext>)defaultContext __attribute__((swift_name("defaultContext()")));
- (SharedKotlinLazyThreadSafetyMode *)defaultLazyMode __attribute__((swift_name("defaultLazyMode()")));
- (SharedLogger *)defaultLoggerLevel:(SharedLevel *)level __attribute__((swift_name("defaultLogger(level:)")));
- (NSString * _Nullable)getClassFullNameOrNullKClass:(id<SharedKotlinKClass>)kClass __attribute__((swift_name("getClassFullNameOrNull(kClass:)")));
- (NSString *)getClassNameKClass:(id<SharedKotlinKClass>)kClass __attribute__((swift_name("getClassName(kClass:)")));
- (NSString *)getStackTraceE:(SharedKotlinException *)e __attribute__((swift_name("getStackTrace(e:)")));
- (SharedMutableDictionary<id, id> *)safeHashMap __attribute__((swift_name("safeHashMap()")));
- (SharedMutableSet<id> *)safeSet __attribute__((swift_name("safeSet()")));
- (id _Nullable)synchronizedLock:(SharedLockable *)lock block:(id _Nullable (^)(void))block __attribute__((swift_name("synchronized(lock:block:)")));
@end

@interface SharedKotlinEnum (Extensions)
@property (readonly) id<SharedQualifier> qualifier __attribute__((swift_name("qualifier")));
@end

@interface SharedKoinApplication (Extensions)
- (SharedKoinApplication *)includesConfigurations:(SharedKotlinArray<SharedKotlinUnit *(^)(SharedKoinApplication *)> *)configurations __attribute__((swift_name("includes(configurations:)")));
- (SharedKoinApplication *)includesConfigurations_:(SharedKotlinArray<SharedKoinConfiguration *> *)configurations __attribute__((swift_name("includes(configurations_:)")));
@end

@interface SharedBeanDefinition (Extensions)
- (void)bind __attribute__((swift_name("bind()")));
- (void)bindsClasses:(NSArray<id<SharedKotlinKClass>> *)classes __attribute__((swift_name("binds(classes:)")));
- (void)createdAtStart __attribute__((swift_name("createdAtStart()")));
- (void)named __attribute__((swift_name("named()")));
- (void)namedName:(NSString *)name __attribute__((swift_name("named(name:)")));
- (void)onCloseOnClose:(void (^)(id _Nullable))onClose __attribute__((swift_name("onClose(onClose:)")));
@end

@interface SharedKoinDefinition (Extensions)
- (SharedKoinDefinition<id> *)bind __attribute__((swift_name("bind()")));
- (SharedKoinDefinition<id> *)bindClazz:(id<SharedKotlinKClass>)clazz __attribute__((swift_name("bind(clazz:)")));
- (SharedKoinDefinition<id> *)bindsClasses:(SharedKotlinArray<id<SharedKotlinKClass>> *)classes __attribute__((swift_name("binds(classes:)")));
- (SharedKoinDefinition<id> *)onCloseOnClose:(void (^)(id _Nullable))onClose __attribute__((swift_name("onClose(onClose:)")));
- (SharedKoinDefinition<id> *)onOptionsOptions:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("onOptions(options:)")));
- (SharedKoinDefinition<id> *)withOptionsOptions:(void (^)(SharedBeanDefinition<id> *))options __attribute__((swift_name("withOptions(options:)")));
@end

@interface SharedModule (Extensions)
- (SharedKoinDefinition<id> *)factoryKClass:(id<SharedKotlinKClass>)kClass qualifier:(id<SharedQualifier> _Nullable)qualifier definition:(id (^)(SharedScope *, SharedParametersHolder *))definition scopeQualifier:(id<SharedQualifier>)scopeQualifier __attribute__((swift_name("factory(kClass:qualifier:definition:scopeQualifier:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(void))constructor options:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options_:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options__:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options__:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options___:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options___:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options____:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options____:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_____:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options_____:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options______:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options______:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_______:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options_______:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options_________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options__________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options__________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable))constructor options___________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options___________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options____________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options____________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_____________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options_____________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options______________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options______________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable))constructor options_______________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options_______________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable))constructor options________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options________________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options_________________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options__________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options__________________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options___________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options___________________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options____________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options____________________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_____________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options_____________________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options______________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options______________________:)")));
- (SharedKoinDefinition<id> *)singleKClass:(id<SharedKotlinKClass>)kClass qualifier:(id<SharedQualifier> _Nullable)qualifier definition:(id (^)(SharedScope *, SharedParametersHolder *))definition createdAtStart:(BOOL)createdAtStart scopeQualifier:(id<SharedQualifier>)scopeQualifier __attribute__((swift_name("single(kClass:qualifier:definition:createdAtStart:scopeQualifier:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(void))constructor options:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options_:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options__:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options__:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options___:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options___:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options____:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options____:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_____:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options_____:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options______:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options______:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_______:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options_______:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options________:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options_________:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options__________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options__________:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable))constructor options___________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options___________:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options____________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options____________:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_____________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options_____________:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options______________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options______________:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable))constructor options_______________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options_______________:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable))constructor options________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options________________:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options_________________:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options__________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options__________________:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options___________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options___________________:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options____________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options____________________:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_____________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options_____________________:)")));
- (SharedKoinDefinition<id> *)singleOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options______________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("singleOf(constructor:options______________________:)")));
@end

@interface SharedOptionRegistry (Extensions)
- (BOOL)hasViewModelScopeFactory __attribute__((swift_name("hasViewModelScopeFactory()")));
@end

@interface SharedScope (Extensions)
- (id _Nullable)doNewConstructor:(id _Nullable (^)(void))constructor __attribute__((swift_name("doNew(constructor:)")));
- (id _Nullable)doNewConstructor_:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor_:)")));
- (id _Nullable)doNewConstructor__:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor__:)")));
- (id _Nullable)doNewConstructor___:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor___:)")));
- (id _Nullable)doNewConstructor____:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor____:)")));
- (id _Nullable)doNewConstructor_____:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor_____:)")));
- (id _Nullable)doNewConstructor______:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor______:)")));
- (id _Nullable)doNewConstructor_______:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor_______:)")));
- (id _Nullable)doNewConstructor________:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor________:)")));
- (id _Nullable)doNewConstructor_________:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor_________:)")));
- (id _Nullable)doNewConstructor__________:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor__________:)")));
- (id _Nullable)doNewConstructor___________:(id _Nullable (^)(id _Nullable))constructor __attribute__((swift_name("doNew(constructor___________:)")));
- (id _Nullable)doNewConstructor____________:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor____________:)")));
- (id _Nullable)doNewConstructor_____________:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor_____________:)")));
- (id _Nullable)doNewConstructor______________:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor______________:)")));
- (id _Nullable)doNewConstructor_______________:(id _Nullable (^)(id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor_______________:)")));
- (id _Nullable)doNewConstructor________________:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor________________:)")));
- (id _Nullable)doNewConstructor_________________:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor_________________:)")));
- (id _Nullable)doNewConstructor__________________:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor__________________:)")));
- (id _Nullable)doNewConstructor___________________:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor___________________:)")));
- (id _Nullable)doNewConstructor____________________:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor____________________:)")));
- (id _Nullable)doNewConstructor_____________________:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor_____________________:)")));
- (id _Nullable)doNewConstructor______________________:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor __attribute__((swift_name("doNew(constructor______________________:)")));
@end

@interface SharedScopeDSL (Extensions)
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(void))constructor options:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options_:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options__:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options__:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options___:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options___:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options____:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options____:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_____:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options_____:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options______:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options______:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_______:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options_______:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options_________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options__________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options__________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable))constructor options___________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options___________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options____________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options____________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_____________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options_____________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options______________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options______________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable))constructor options_______________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options_______________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable))constructor options________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options________________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options_________________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options__________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options__________________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options___________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options___________________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options____________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options____________________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_____________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options_____________________:)")));
- (SharedKoinDefinition<id> *)factoryOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options______________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("factoryOf(constructor:options______________________:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(void))constructor options:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options_:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options__:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options__:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options___:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options___:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options____:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options____:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_____:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options_____:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options______:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options______:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_______:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options_______:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options________:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options_________:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options__________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options__________:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable))constructor options___________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options___________:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options____________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options____________:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_____________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options_____________:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options______________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options______________:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable))constructor options_______________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options_______________:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable))constructor options________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options________________:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options_________________:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options__________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options__________________:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options___________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options___________________:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options____________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options____________________:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options_____________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options_____________________:)")));
- (SharedKoinDefinition<id> *)scopedOfConstructor:(id _Nullable (^)(id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable, id _Nullable))constructor options______________________:(void (^ _Nullable)(SharedBeanDefinition<id> *))options __attribute__((swift_name("scopedOf(constructor:options______________________:)")));
@end

@interface SharedKoinPlatformTools (Extensions)
- (NSString *)generateId __attribute__((swift_name("generateId()")));
- (NSString *)getKClassDefaultNameKClass:(id<SharedKotlinKClass>)kClass __attribute__((swift_name("getKClassDefaultName(kClass:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("BeanDefinitionKt")))
@interface SharedBeanDefinitionKt : SharedBase
+ (SharedBeanDefinition<id> *)_createDeclaredDefinitionKind:(SharedKind *)kind qualifier:(id<SharedQualifier> _Nullable)qualifier secondaryTypes:(NSArray<id<SharedKotlinKClass>> *)secondaryTypes scopeQualifier:(id<SharedQualifier>)scopeQualifier __attribute__((swift_name("_createDeclaredDefinition(kind:qualifier:secondaryTypes:scopeQualifier:)")));
+ (SharedBeanDefinition<id> *)_createDefinitionKind:(SharedKind *)kind qualifier:(id<SharedQualifier> _Nullable)qualifier definition:(id _Nullable (^)(SharedScope *, SharedParametersHolder *))definition secondaryTypes:(NSArray<id<SharedKotlinKClass>> *)secondaryTypes scopeQualifier:(id<SharedQualifier>)scopeQualifier __attribute__((swift_name("_createDefinition(kind:qualifier:definition:secondaryTypes:scopeQualifier:)")));
+ (NSString *)indexKeyClazz:(id<SharedKotlinKClass>)clazz typeQualifier:(id<SharedQualifier> _Nullable)typeQualifier scopeQualifier:(id<SharedQualifier>)scopeQualifier __attribute__((swift_name("indexKey(clazz:typeQualifier:scopeQualifier:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("CoreResolverKt")))
@interface SharedCoreResolverKt : SharedBase
+ (NSSet<SharedScope *> *)flattenScopes:(NSArray<SharedScope *> *)scopes __attribute__((swift_name("flatten(scopes:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("DefaultContextExtKt")))
@interface SharedDefaultContextExtKt : SharedBase
+ (void)loadKoinModulesModules:(NSArray<SharedModule *> *)modules __attribute__((swift_name("loadKoinModules(modules:)")));
+ (void)loadKoinModulesModule:(SharedModule *)module __attribute__((swift_name("loadKoinModules(module:)")));
+ (SharedKoinApplication *)startKoinAppDeclaration:(void (^)(SharedKoinApplication *))appDeclaration __attribute__((swift_name("startKoin(appDeclaration:)")));
+ (SharedKoinApplication *)startKoinKoinApplication:(SharedKoinApplication *)koinApplication __attribute__((swift_name("startKoin(koinApplication:)")));
+ (SharedKoinApplication *)startKoinAppConfiguration:(SharedKoinConfiguration *)appConfiguration __attribute__((swift_name("startKoin(appConfiguration:)")));
+ (void)stopKoin __attribute__((swift_name("stopKoin()")));
+ (void)unloadKoinModulesModules:(NSArray<SharedModule *> *)modules __attribute__((swift_name("unloadKoinModules(modules:)")));
+ (void)unloadKoinModulesModule:(SharedModule *)module __attribute__((swift_name("unloadKoinModules(module:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("DurationExtKt")))
@interface SharedDurationExtKt : SharedBase
+ (double)inMs:(int64_t)receiver __attribute__((swift_name("inMs(_:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("InjectPropertyKt")))
@interface SharedInjectPropertyKt : SharedBase
+ (void)inject:(id _Nullable (^)(void))receiver __attribute__((swift_name("inject(_:)")));
+ (void)inject:(id _Nullable (^)(void))receiver koin:(SharedKoin *)koin __attribute__((swift_name("inject(_:koin:)")));
+ (void)inject:(id _Nullable (^)(void))receiver scope:(SharedScope *)scope __attribute__((swift_name("inject(_:scope:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("IosCameraManagerKt")))
@interface SharedIosCameraManagerKt : SharedBase
@property (class, readonly) SharedModule *platformModule __attribute__((swift_name("platformModule")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KClassExtKt")))
@interface SharedKClassExtKt : SharedBase
+ (NSString *)getFullName:(id<SharedKotlinKClass>)receiver __attribute__((swift_name("getFullName(_:)")));
+ (NSString *)saveCache:(id<SharedKotlinKClass>)receiver __attribute__((swift_name("saveCache(_:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KoinApplicationKt")))
@interface SharedKoinApplicationKt : SharedBase
+ (SharedKoinApplication *)koinApplicationCreateEagerInstances:(BOOL)createEagerInstances __attribute__((swift_name("koinApplication(createEagerInstances:)")));
+ (SharedKoinApplication *)koinApplicationAppDeclaration:(void (^ _Nullable)(SharedKoinApplication *))appDeclaration __attribute__((swift_name("koinApplication(appDeclaration:)")));
+ (SharedKoinApplication *)koinApplicationConfiguration:(SharedKoinConfiguration * _Nullable)configuration __attribute__((swift_name("koinApplication(configuration:)")));
+ (SharedKoinApplication *)koinApplicationCreateEagerInstances:(BOOL)createEagerInstances appDeclaration:(void (^ _Nullable)(SharedKoinApplication *))appDeclaration __attribute__((swift_name("koinApplication(createEagerInstances:appDeclaration:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KoinComponentKt")))
@interface SharedKoinComponentKt : SharedBase
+ (id)get:(id<SharedKoinComponent>)receiver qualifier:(id<SharedQualifier> _Nullable)qualifier parameters:(SharedParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("get(_:qualifier:parameters:)")));
+ (id<SharedKotlinLazy>)inject:(id<SharedKoinComponent>)receiver qualifier:(id<SharedQualifier> _Nullable)qualifier mode:(SharedKotlinLazyThreadSafetyMode *)mode parameters:(SharedParametersHolder *(^ _Nullable)(void))parameters __attribute__((swift_name("inject(_:qualifier:mode:parameters:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KoinConfigurationKt")))
@interface SharedKoinConfigurationKt : SharedBase
+ (SharedKoinConfiguration *)koinConfigurationDeclaration:(void (^)(SharedKoinApplication *))declaration __attribute__((swift_name("koinConfiguration(declaration:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KoinHelperKt")))
@interface SharedKoinHelperKt : SharedBase
+ (void)startKoin __attribute__((swift_name("startKoin()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KoinOptionKt")))
@interface SharedKoinOptionKt : SharedBase
+ (SharedKotlinPair<SharedKoinOption *, SharedBoolean *> *)viewModelScopeFactory __attribute__((swift_name("viewModelScopeFactory()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KoinScopeComponentKt")))
@interface SharedKoinScopeComponentKt : SharedBase
+ (SharedScope *)createScope:(id<SharedKoinScopeComponent>)receiver source:(id _Nullable)source __attribute__((swift_name("createScope(_:source:)")));
+ (SharedScope *)createScope:(id<SharedKoinScopeComponent>)receiver scopeId:(NSString *)scopeId source:(id _Nullable)source scopeArchetype:(SharedTypeQualifier * _Nullable)scopeArchetype __attribute__((swift_name("createScope(_:scopeId:source:scopeArchetype:)")));
+ (id<SharedKotlinLazy>)getOrCreateScope:(id<SharedKoinScopeComponent>)receiver __attribute__((swift_name("getOrCreateScope(_:)")));
+ (NSString *)getScopeId:(id)receiver __attribute__((swift_name("getScopeId(_:)")));
+ (SharedTypeQualifier *)getScopeName:(id)receiver __attribute__((swift_name("getScopeName(_:)")));
+ (SharedScope * _Nullable)getScopeOrNull:(id<SharedKoinScopeComponent>)receiver __attribute__((swift_name("getScopeOrNull(_:)")));
+ (id<SharedKotlinLazy>)doNewScope:(id<SharedKoinScopeComponent>)receiver __attribute__((swift_name("doNewScope(_:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("LoggerKt")))
@interface SharedLoggerKt : SharedBase
@property (class, readonly) NSString *KOIN_TAG __attribute__((swift_name("KOIN_TAG")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("MainViewControllerKt")))
@interface SharedMainViewControllerKt : SharedBase
+ (UIViewController *)mainViewController __attribute__((swift_name("mainViewController()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ModuleKt")))
@interface SharedModuleKt : SharedBase
+ (SharedFactoryInstanceFactory<id> *)_factoryInstanceFactoryQualifier:(id<SharedQualifier> _Nullable)qualifier definition:(id _Nullable (^)(SharedScope *, SharedParametersHolder *))definition scopeQualifier:(id<SharedQualifier>)scopeQualifier __attribute__((swift_name("_factoryInstanceFactory(qualifier:definition:scopeQualifier:)")));
+ (SharedScopedInstanceFactory<id> *)_scopedInstanceFactoryQualifier:(id<SharedQualifier> _Nullable)qualifier definition:(id _Nullable (^)(SharedScope *, SharedParametersHolder *))definition scopeQualifier:(id<SharedQualifier>)scopeQualifier __attribute__((swift_name("_scopedInstanceFactory(qualifier:definition:scopeQualifier:)")));
+ (SharedSingleInstanceFactory<id> *)_singleInstanceFactoryQualifier:(id<SharedQualifier> _Nullable)qualifier definition:(id _Nullable (^)(SharedScope *, SharedParametersHolder *))definition scopeQualifier:(id<SharedQualifier>)scopeQualifier __attribute__((swift_name("_singleInstanceFactory(qualifier:definition:scopeQualifier:)")));
+ (NSSet<SharedModule *> *)flattenModules:(NSArray<SharedModule *> *)modules __attribute__((swift_name("flatten(modules:)")));
+ (NSArray<SharedModule *> *)plus:(NSArray<SharedModule *> *)receiver module:(SharedModule *)module __attribute__((swift_name("plus(_:module:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ModuleDSLKt")))
@interface SharedModuleDSLKt : SharedBase
+ (SharedModule *)moduleCreatedAtStart:(BOOL)createdAtStart moduleDeclaration:(void (^)(SharedModule *))moduleDeclaration __attribute__((swift_name("module(createdAtStart:moduleDeclaration:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ParametersHolderKt")))
@interface SharedParametersHolderKt : SharedBase
+ (SharedParametersHolder *)emptyParametersHolder __attribute__((swift_name("emptyParametersHolder()")));
+ (SharedParametersHolder *)parameterArrayOfParameters:(SharedKotlinArray<id> *)parameters __attribute__((swift_name("parameterArrayOf(parameters:)")));
+ (SharedParametersHolder *)parameterSetOfParameters:(SharedKotlinArray<id> *)parameters __attribute__((swift_name("parameterSetOf(parameters:)")));
+ (SharedParametersHolder *)parametersOfParameters:(SharedKotlinArray<id> *)parameters __attribute__((swift_name("parametersOf(parameters:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("QualifierKt")))
@interface SharedQualifierKt : SharedBase
+ (SharedTypeQualifier *)_q __attribute__((swift_name("_q()")));
+ (SharedStringQualifier *)_qName:(NSString *)name __attribute__((swift_name("_q(name:)")));
+ (SharedTypeQualifier *)named __attribute__((swift_name("named()")));
+ (id<SharedQualifier>)namedEnum:(SharedKotlinEnum<SharedKotlinEnum *> *)enum_ __attribute__((swift_name("named(enum:)")));
+ (SharedStringQualifier *)namedName:(NSString *)name __attribute__((swift_name("named(name:)")));
+ (SharedTypeQualifier *)qualifier __attribute__((swift_name("qualifier()")));
+ (id<SharedQualifier>)qualifierEnum:(SharedKotlinEnum<SharedKotlinEnum *> *)enum_ __attribute__((swift_name("qualifier(enum:)")));
+ (SharedStringQualifier *)qualifierName:(NSString *)name __attribute__((swift_name("qualifier(name:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("StringExtKt")))
@interface SharedStringExtKt : SharedBase
+ (NSString *)clearQuotes:(NSString *)receiver __attribute__((swift_name("clearQuotes(_:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("CommonModuleKt")))
@interface SharedCommonModuleKt : SharedBase
@property (class, readonly) SharedModule *commonModule __attribute__((swift_name("commonModule")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("IosModuleKt")))
@interface SharedIosModuleKt : SharedBase
@property (class, readonly) SharedModule *iosModule __attribute__((swift_name("iosModule")));
@end

__attribute__((swift_name("Multiplatform_settingsSettings")))
@protocol SharedMultiplatform_settingsSettings
@required
- (void)clear __attribute__((swift_name("clear()")));
- (BOOL)getBooleanKey:(NSString *)key defaultValue:(BOOL)defaultValue __attribute__((swift_name("getBoolean(key:defaultValue:)")));
- (SharedBoolean * _Nullable)getBooleanOrNullKey:(NSString *)key __attribute__((swift_name("getBooleanOrNull(key:)")));
- (double)getDoubleKey:(NSString *)key defaultValue:(double)defaultValue __attribute__((swift_name("getDouble(key:defaultValue:)")));
- (SharedDouble * _Nullable)getDoubleOrNullKey:(NSString *)key __attribute__((swift_name("getDoubleOrNull(key:)")));
- (float)getFloatKey:(NSString *)key defaultValue:(float)defaultValue __attribute__((swift_name("getFloat(key:defaultValue:)")));
- (SharedFloat * _Nullable)getFloatOrNullKey:(NSString *)key __attribute__((swift_name("getFloatOrNull(key:)")));
- (int32_t)getIntKey:(NSString *)key defaultValue:(int32_t)defaultValue __attribute__((swift_name("getInt(key:defaultValue:)")));
- (SharedInt * _Nullable)getIntOrNullKey:(NSString *)key __attribute__((swift_name("getIntOrNull(key:)")));
- (int64_t)getLongKey:(NSString *)key defaultValue:(int64_t)defaultValue __attribute__((swift_name("getLong(key:defaultValue:)")));
- (SharedLong * _Nullable)getLongOrNullKey:(NSString *)key __attribute__((swift_name("getLongOrNull(key:)")));
- (NSString *)getStringKey:(NSString *)key defaultValue:(NSString *)defaultValue __attribute__((swift_name("getString(key:defaultValue:)")));
- (NSString * _Nullable)getStringOrNullKey:(NSString *)key __attribute__((swift_name("getStringOrNull(key:)")));
- (BOOL)hasKeyKey:(NSString *)key __attribute__((swift_name("hasKey(key:)")));
- (void)putBooleanKey:(NSString *)key value:(BOOL)value __attribute__((swift_name("putBoolean(key:value:)")));
- (void)putDoubleKey:(NSString *)key value:(double)value __attribute__((swift_name("putDouble(key:value:)")));
- (void)putFloatKey:(NSString *)key value:(float)value __attribute__((swift_name("putFloat(key:value:)")));
- (void)putIntKey:(NSString *)key value:(int32_t)value __attribute__((swift_name("putInt(key:value:)")));
- (void)putLongKey:(NSString *)key value:(int64_t)value __attribute__((swift_name("putLong(key:value:)")));
- (void)putStringKey:(NSString *)key value:(NSString *)value __attribute__((swift_name("putString(key:value:)")));
- (void)removeKey:(NSString *)key __attribute__((swift_name("remove(key:)")));
@property (readonly) NSSet<NSString *> *keys __attribute__((swift_name("keys")));
@property (readonly, getter=size_) int32_t size __attribute__((swift_name("size")));
@end

__attribute__((swift_name("KotlinRuntimeException")))
@interface SharedKotlinRuntimeException : SharedKotlinException
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end

__attribute__((swift_name("KotlinIllegalStateException")))
@interface SharedKotlinIllegalStateException : SharedKotlinRuntimeException
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.4")
*/
__attribute__((swift_name("KotlinCancellationException")))
@interface SharedKotlinCancellationException : SharedKotlinIllegalStateException
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreFlow")))
@protocol SharedKotlinx_coroutines_coreFlow
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)collectCollector:(id<SharedKotlinx_coroutines_coreFlowCollector>)collector completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("collect(collector:completionHandler:)")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreSharedFlow")))
@protocol SharedKotlinx_coroutines_coreSharedFlow <SharedKotlinx_coroutines_coreFlow>
@required
@property (readonly) NSArray<id> *replayCache __attribute__((swift_name("replayCache")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreStateFlow")))
@protocol SharedKotlinx_coroutines_coreStateFlow <SharedKotlinx_coroutines_coreSharedFlow>
@required
@property (readonly) id _Nullable value __attribute__((swift_name("value")));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="2.0")
*/
__attribute__((swift_name("KotlinAutoCloseable")))
@protocol SharedKotlinAutoCloseable
@required
- (void)close __attribute__((swift_name("close()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinArray")))
@interface SharedKotlinArray<T> : SharedBase
+ (instancetype)arrayWithSize:(int32_t)size init:(T _Nullable (^)(SharedInt *))init __attribute__((swift_name("init(size:init:)")));
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (T _Nullable)getIndex:(int32_t)index __attribute__((swift_name("get(index:)")));
- (id<SharedKotlinIterator>)iterator __attribute__((swift_name("iterator()")));
- (void)setIndex:(int32_t)index value:(T _Nullable)value __attribute__((swift_name("set(index:value:)")));
@property (readonly) int32_t size __attribute__((swift_name("size")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreCoroutineScope")))
@protocol SharedKotlinx_coroutines_coreCoroutineScope
@required
@property (readonly) id<SharedKotlinCoroutineContext> coroutineContext __attribute__((swift_name("coroutineContext")));
@end

__attribute__((swift_name("KotlinKDeclarationContainer")))
@protocol SharedKotlinKDeclarationContainer
@required
@end

__attribute__((swift_name("KotlinKAnnotatedElement")))
@protocol SharedKotlinKAnnotatedElement
@required
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.1")
*/
__attribute__((swift_name("KotlinKClassifier")))
@protocol SharedKotlinKClassifier
@required
@end

__attribute__((swift_name("KotlinKClass")))
@protocol SharedKotlinKClass <SharedKotlinKDeclarationContainer, SharedKotlinKAnnotatedElement, SharedKotlinKClassifier>
@required

/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.1")
*/
- (BOOL)isInstanceValue:(id _Nullable)value __attribute__((swift_name("isInstance(value:)")));
@property (readonly) NSString * _Nullable qualifiedName __attribute__((swift_name("qualifiedName")));
@property (readonly) NSString * _Nullable simpleName __attribute__((swift_name("simpleName")));
@end

__attribute__((swift_name("KotlinLazy")))
@protocol SharedKotlinLazy
@required
- (BOOL)isInitialized __attribute__((swift_name("isInitialized()")));
@property (readonly) id _Nullable value __attribute__((swift_name("value")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinLazyThreadSafetyMode")))
@interface SharedKotlinLazyThreadSafetyMode : SharedKotlinEnum<SharedKotlinLazyThreadSafetyMode *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedKotlinLazyThreadSafetyMode *synchronized __attribute__((swift_name("synchronized")));
@property (class, readonly) SharedKotlinLazyThreadSafetyMode *publication __attribute__((swift_name("publication")));
@property (class, readonly) SharedKotlinLazyThreadSafetyMode *none __attribute__((swift_name("none")));
+ (SharedKotlinArray<SharedKotlinLazyThreadSafetyMode *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedKotlinLazyThreadSafetyMode *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinPair")))
@interface SharedKotlinPair<__covariant A, __covariant B> : SharedBase
- (instancetype)initWithFirst:(A _Nullable)first second:(B _Nullable)second __attribute__((swift_name("init(first:second:)"))) __attribute__((objc_designated_initializer));
- (SharedKotlinPair<A, B> *)doCopyFirst:(A _Nullable)first second:(B _Nullable)second __attribute__((swift_name("doCopy(first:second:)")));
- (BOOL)equalsOther:(id _Nullable)other __attribute__((swift_name("equals(other:)")));
- (int32_t)hashCode __attribute__((swift_name("hashCode()")));
- (NSString *)toString __attribute__((swift_name("toString()")));
@property (readonly) A _Nullable first __attribute__((swift_name("first")));
@property (readonly) B _Nullable second __attribute__((swift_name("second")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinEnumCompanion")))
@interface SharedKotlinEnumCompanion : SharedBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKotlinEnumCompanion *shared __attribute__((swift_name("shared")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinUnit")))
@interface SharedKotlinUnit : SharedBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)unit __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKotlinUnit *shared __attribute__((swift_name("shared")));
- (NSString *)description __attribute__((swift_name("description()")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreFlowCollector")))
@protocol SharedKotlinx_coroutines_coreFlowCollector
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)emitValue:(id _Nullable)value completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("emit(value:completionHandler:)")));
@end

__attribute__((swift_name("KotlinIterator")))
@protocol SharedKotlinIterator
@required
- (BOOL)hasNext __attribute__((swift_name("hasNext()")));
- (id _Nullable)next __attribute__((swift_name("next()")));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.3")
*/
__attribute__((swift_name("KotlinCoroutineContext")))
@protocol SharedKotlinCoroutineContext
@required
- (id _Nullable)foldInitial:(id _Nullable)initial operation:(id _Nullable (^)(id _Nullable, id<SharedKotlinCoroutineContextElement>))operation __attribute__((swift_name("fold(initial:operation:)")));
- (id<SharedKotlinCoroutineContextElement> _Nullable)getKey:(id<SharedKotlinCoroutineContextKey>)key __attribute__((swift_name("get(key:)")));
- (id<SharedKotlinCoroutineContext>)minusKeyKey:(id<SharedKotlinCoroutineContextKey>)key __attribute__((swift_name("minusKey(key:)")));
- (id<SharedKotlinCoroutineContext>)plusContext:(id<SharedKotlinCoroutineContext>)context __attribute__((swift_name("plus(context:)")));
@end

__attribute__((swift_name("KotlinCoroutineContextElement")))
@protocol SharedKotlinCoroutineContextElement <SharedKotlinCoroutineContext>
@required
@property (readonly) id<SharedKotlinCoroutineContextKey> key __attribute__((swift_name("key")));
@end

__attribute__((swift_name("KotlinCoroutineContextKey")))
@protocol SharedKotlinCoroutineContextKey
@required
@end

#pragma pop_macro("_Nullable_result")
#pragma clang diagnostic pop
NS_ASSUME_NONNULL_END
