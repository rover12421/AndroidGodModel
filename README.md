# Android GodModel Plugin / 上帝模式插件

`GodModel` 插件本身不做任何操作，只提供操作接口，通过配置 `godHands` 上帝之手来做具体操作。

## 使用

```groovy
buildscript {
    dependencies {
        classpath "com.rover12421.android.godmodel:core:0.7" // GodModel 插件核心/入口
        
        // 下面三个 根据需要添加，他们作为 GodModel 的插件/依赖引入 
        classpath "com.rover12421.android.godmodel.hash:plugin:0.1" // 根据注解生成 hash 注解的插件 
        classpath "com.rover12421.android.godmodel.namehash:plugin:0.3" // 根据注解中的名字，生成对应名字的hash注解
        classpath "com.rover12421.android.godmodel.removeAnnotation:plugin:0.2" // 移除注解
    }
}
```

```groovy
GodModel {
    incremental = false // 是否支持增量模式，默认 true
    debug = true // debug模式，有更多的信息输出
    godHands {  // 上帝之手 配置 / GodModel 的插件配置
        [
                // 下面是上帝之手的配置，根据需要配置。其中`property`属性为每个上帝之手独立支持数据
                hash { // 给上帝之手取个名
                    type = com.rover12421.android.godmodel.hash.plugin.HashGodHand.class // 具体实现的类，来源 com.rover12421.android.godmodel.hash:plugin
                    filterRegex = /com\.rover12421\..+/ // 过滤理白名单的正则表达式，默认是全部类
                    property["skipMethod"] = false  // 跳过Method检测，hash 模块支持
                    property["skipField"] = false  // 跳过Field检测，hash 模块支持
                },
                namehash {
                    type = com.rover12421.android.godmodel.namehash.plugin.NameHashGodHand.class // 具体实现的类，来源 com.rover12421.android.godmodel.namehash:plugin
                },
                removeAnnotation {
                    type = com.rover12421.android.godmodel.removeAnnotation.plugin.RemoveAnnotationGodHand.class // 具体实现的类，来源 com.rover12421.android.godmodel.removeAnnotation:plugin
                    filterRegex = /com\.rover12421\.godmodel\.test\..+/ // 只处理白名单，正则表达式
                    property["remove"] = ["kotlin.Metadata", "kotlin.coroutines.jvm.internal.DebugMetadata"] // 移除指定的注解，不支持 通配符/正则表达式
                }
        ]
    }
}
```