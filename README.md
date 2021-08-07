![img.png](img.png)# learn-netty
1. NIO三大核心组件：Channel(通道)、Buffer(缓冲区)、Selector(多路复用器）
   * channel类似于流，每个channel对应一个buffer缓冲区，buffer底层就是个数组
   * channel会注册到selector上，由selector根据channel读写时间的发生将其交由某个空闲的线程处理
   * NIO的Buffer和channel都是既读也可写
2. Nio的三个关键方法（以下为linux内实现，windows不支持epoll，底层是基于winsock2的select函数实现）
   * Selector.open：创建多路复用器，本质上是调用linux的epoll_create函数来创建epoll
   * socketChannel.register(selector, SelectionKey.OP_READ)：将channel注册到多路复用器上
   * selector.selector：阻塞等待需要处理的事件发生，其内部通过epoll_ctl来进行事件绑定以及通过epoll_wait函数阻塞等待事件发生
   ![img_1.png](img_1.png)
3. EPoll函数
   * epoll_create
   ``` 
    int epoll_create(int size)
    创建一个epoll实例，并返回一个非负数作为文件描述符，用于对epoll接口的后续调用，参数size代表可能会size个描述符，但size不是一
    个最大值，只是提示操作系统它的数量级，现在这个参数基本上已经弃用了
   ```
   * epoll_ctl
   ``` 
    int epoll_ctl(int epfd, int op, int fd, struct epoll_event *event)
    使用文件描述符epfd引用的epoll实例对目标文件描述符fd执行op操作
    参数epfd表示epoll实例对应的文件表述符，参数fd表示socket对应的文件描述符
    参数op有一下几个值：
        EPOLL_CTL_ADD：注册新的fd到epfd中，并关联事件event
        EPOLL_CTL_MOD：修改已经注册的fd的监听事件
        EPOLL_CTL_DEL：从epfd中移除fd，并且忽略掉绑定的event，这是event可以为null
    参数event是一个结构体，其有很多可选值如EPOLLIN（表示对应的文件描述符是可读的）、EPOLLOUT（表示对应的文件描述符设计可写的）、EPOLLERR（表示对应的文件描述符发生了错误）
     struct epoll_event {
        __uint32_t events; /* Epoll events */
        epoll_data_t data; /* User data variable */
        };
    
     typedef union epoll_data {
         void *ptr;
         int fd;
         __uint32_t u32;
         __uint64_t u64;
     } epoll_data_t;
   ```
   * epoll_wait
   ``` 
    int epoll_wait(int epfd, struct epoll_event *events, int maxevents, int timeout)
    等待文件描述符epfd上的时间，events表示调用者所有可用事件的集合，maxevents表示最多等到多少个事件就返回，timeout是超时时间
   ```
4. AIO
在linux上AIO的底层实现仍使用EPoll，没有很好实现AIO，因此在性能上没有明显的优势，而且被JDK封装了一层不容易深度优化，Linux上AIO还不够成熟，所以Netty选择了NIO而非AIO，
但是Netty是异步非阻塞框架，其在NIO上做了很多异步的封装
