### 1、如何设计一个秒杀系统

#### 1.1、秒杀解决的问题

- **并发读**
  - 尽量减少用户到服务端来“读”数据
  - 或让用户读更少的数据
- **并发写**
  - 原则与并发读一致

#### 1.2、需要效果及方案

- **高性能**
  - 动静分离
  - 热点发现与隔离
  - 请求的削峰和分层过滤
  - 服务端的极致优化
- **一致性**
  - 拍下减库存
  - 付款减库存
  - 预扣减库存
- **高可用**
  - 多个方案

#### 1.3、分布式session解决方案

- Session复制
  - 优点
    - 无需修改代码，只需要修改Tomcat配置
  - 缺点Q
    - Session同步传输需要占用内网带宽
    - 多台Tomcat同步性能指数级下降
    - Session占用内存，无法有效水平扩展
- 前端存储
  - 优点
    - 不占用服务端内存
  - 缺点
    - 存在安全风险
    - 数据大小受cookie限制
    - 占用外网带宽
- Session粘滞（这种方式将同一用户的请求转发到特定的Tomcat服务器上，避免了集群中Session的复制）
  - 优点
    - 无需修改代码
    - 服务端可以水平扩展
  - 缺点
    - 增加新机器，会重新Hash，导致重新登录
    - 应用重启，需要重新登录
- 后端集中存储
  - 优点
    - 安全
    - 容易集中存储
  - 缺点
    - 增加复杂度
    - 需要修改代码



#### 4、分布式session实现

- #### SpringSession（简单配置）

  - **添加依赖**

    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <!--    对象池依赖    -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-pool2</artifactId>
    </dependency>
    <!--    spring-session   -->
    <dependency>
        <groupId>org.springframework.session</groupId>
        <artifactId>spring-session-data-redis</artifactId>
    </dependency>
    ```

  - **redis配置**

    ```yaml
    spring:
        redis:
            host: 192.168.111.11
            port: 6379
            database: 0
            lettuce:
                pool:
                    max-active: 8
                    max-wait: 10000ms
                    min-idle: 0
                    max-idle: 8
            timeout: 10000ms
    ```

> 通过以上配置后，程序中所有对session的操作都会替换为对redis的操作

- **代码操作redis (直接将用户信息存入redis中，不使用session)**

  - **依赖添加**

    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <!--    对象池依赖    -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-pool2</artifactId>
    </dependency>
    ```

  - **redis配置**

    ```yaml
    spring:
        redis:
            host: 192.168.111.11
            port: 6379
            database: 0
            lettuce:
                pool:
                    max-active: 8
                    max-wait: 10000ms
                    min-idle: 0
                    max-idle: 8
            timeout: 10000ms
    ```

  - **序列化操作**




#### 1.5、压测秒杀遇到的问题

- 超卖
- 只要知道接口，就可以进行购买，无论秒杀是否开始或结束



#### 1.6、优化

- 缓存
  - 页面缓存
  - 对象缓存
  - URL缓存
- 静态资源CDN