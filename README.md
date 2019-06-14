## 一、为什么要使用两级缓存框架？

#### 1、避免项目重新部署后，内存缓存清空，引起的『雪崩』问题。
#### 2、分担单Redis服务器（或集群）网络流量大，解决特殊时段网卡吞吐瓶颈问题。
#### 3、实现微服务化架构下的缓存（或Session）共享。


## 二、实现思路

#### 1、使用EhCache作为一级缓存，使用Redis-Server作为二级缓存。
#### 2、数据读取：首先查找一级缓存，找到返回，未找到查找二级缓存。如找到返回，否则执行方法操作。
#### 3、使用Redis存储方式实现session共享。
#### 4、使用注解方式方便开发。

## 三、用法

- 新建SpringBoot项目（建议使用SpringBoot2，必须是JDK8及更新版本。）
- 在pom.xml中添加如下maven依赖。（注意：请使用3.1.3及更新版本，当前最新statable-version:3.1.3）

    ````
    <dependency>
        <groupId>com.zhisland</groupId>
        <artifactId>thetis-cache</artifactId>
        <version>3.1.3</version>
    </dependency>
    
    ````  
    
- 在SpringBoot的启动类添加如下注解：

    ````
    @EnableThetisCache
    ````
    
- 例如：

  ````
      package com.zhisland.testthetis;
      
      import EnableThetisCache;
      import org.springframework.boot.SpringApplication;
      import org.springframework.boot.autoconfigure.SpringBootApplication;
      
      @SpringBootApplication
      @EnableThetisCache
      public class TestThetisApplication {
      
          public static void main(String[] args) {
              SpringApplication.run(TestThetisApplication.class, args);
          }
      }

  ````
- 在需要缓存的方法中使用如下两种注解，分别用于『设置缓存』和『删除缓存』，被设置的缓存值为方法的返回值。
- 注解时，key可使用 key="#paramName" 或 key="#paramName.field" 的方式，取用参数值；其中的paramNName是参数名称，根据参数名称生成动态的key；也可为固定字符串。取值使用 『 #』

  
  ````
      @CacheThis 
      @CacheBreak

  ````
- 注解定义

  ````  
  public @interface CacheThis {
  
      // 指定用于缓存的实体模型
      // 若不设置，将默认使用『被此注解标记的方法所在类的全名』
      Class<? extends Object> model() default DefaultModel.class;
  
      // Key值指定；
      // 若不指定，将默认使用『被此注解标记的方法的方法名，与参数数组的类型签名，所组成的字符串』作为key
      String key() default "";
  
  }
  
  public @interface CacheBreak {
  
  
      // 指定用于缓存的实体模型
      // 若不设置，将默认使用『被此注解标记的方法所在类的全名』
      Class<? extends Object> model() default DefaultModel.class;
  
      // Key值指定；
      // 若不指定，将默认使用『被此注解标记的方法的方法名，与参数数组的类型签名，所组成的字符串』作为key
      String key() default "";
  
  
  }
  

  ````  
- 示例  

  ````    
  @CacheThis(model=TestModel.class,key="#a")
  public TestModel getTestModel(String a){
    return new TestModel();
  }
    
  ````

  
### 重要说明 ：若要保证缓存数据的正常删除、更新，建议指定注解的model与key值。
### 此缓存框架（Thetis-Cache）将使用model值与key连接的字符串，作为缓存的 Key。

  
- SpringBoot的Redis-Server配置(application.yml)，将读取此处配置作为Redis-Server连接；以构建二级缓存。  

 ````
  spring:
    redis:
      host: localhost
      port: 6379
      database: 0

 ````
 
- 若Redis-Server无法正常连接，将不启用二级缓存，Session也将无法实现应用间共享。

## 四、Session的使用

### 应用间共享原理  

 
#### 使用Redis-Server实现数据的缓存和读取，通过定义『域』的方式实现键的区分。


#### 用法：以自动注入的方式，使用SessionStorage类型对象  

 ````  
     @Autowired
     private SessionStorage sessionStorage;

 ````  
 
- 使用如下：  

 ````    
 // 存值
 sessionStorage.put("gao",m);
 // 读取
 sessionStorage.get("gao")
 // 删除
 sessionStorage.remove("gao");
 ````    
 
- 方法定义说明  

  ````    
  put(String key, Object value, String... region)
  get(String key, String... region)
  remove(String key, String... region)

  ````    
  
- 其中的region若未定义将自动读取应用名称，如下：

 ````
 spring:
   application:
     name: 高广林

 ````
     
- 若未配置应用名称，将使用当前项目的『根文件夹名称』


### 重要说明： 分布式部署架构中，若需要session共享，必须保证Region的一致。

## 过期配置：若无以下配置，默认7天后过期；配置值 0 为永不过期

- 可配置缓存过期时间如下    

````
spring:
  cache:
    redis:
      time-to-live: 7d

````    

- 7d 7天；5h 5小时； 8m 8分钟； 9s 9秒； 10 ms 10毫秒；0  永不过期


## 五、上下文缓存，不使用Redis存储。（@since 3.0.1 ）

- 用法：自定义过期时间;若不设置缓存持续（duration）时间（以毫秒为单位），运行期永不过期。    

````    

@Autowired
private ContextStorage contextStorage;

````


