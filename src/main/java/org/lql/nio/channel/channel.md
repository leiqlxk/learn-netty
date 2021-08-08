### 文件编程：FileChannel，其只能工作在阻塞模式下，只有跟网络相关的channel才能放到selector里去
1. 获取  
不能直接打开FileChannel，必须通过FileInputStream、FileOutputStream或者RandomAccessFile来回去FileChannel，它们都有getChannel方法
   * 通过FileInputStream获取的channel只能读
   * 通过FileOutputStream获取的channel只能写
   * 通过RandomAccessFile是否能读写根据构造RandomAceesFile时的读写模式决定
2. 读取  
会从channel读取数据填充ByteBuffer，返回值表示读到了多少字节，-1表示到达了文件的末尾
    ```
        int readBytes = channel.read(buffer);
    ```
3. 写入  
    ``` 
       ByteBuffer buffer = ....;
        buffer.put(....); // 存入数据
        buffer.flip();// 切换模式
   
       // 因为write方法并不能保证一次将buffer中的内容全部写入channel，特别是在socketChannel中写入能力是有限的
        while(buffer.hasRemaining()) {
            channel.write(buffer);
        }
    ```
4. 关闭   
channel必须关闭，不过调用了FileInputStream、FileOutputStream或者RandomAccessFile的close方法会间接地调用channel的close方法
5. 位置                      
    获取当前位置
    ``` 
     long pos = channel.position();
    ```                           
    设置当前位置
    ``` 
    long newPos = ....;
    channel.position(newPos);
    ```                        
    设置当前位置时，如果设置文件的末尾               
      * 这时读取会返回-1  
      * 这时写入，会追加内容，但要注意如果position超过了文件末尾，再写入时在新内容和原末尾之间会有空洞（00）

6. 强制写入    
    操作系统出于性能的考虑，会将数据缓存，不是立刻写入磁盘。可以调用force(true)方法将文件内容和元数据立刻写入磁盘
7. 两个Channel传输数据    
    ````
     try (
            FileChannel from = new FileInputStream("data.txt").getChannel();
            FileChannel to = new FileOutputStream("to.txt").getChannel();
        ) {
            // 效率高，底层会利用操作系统的零拷贝进行优化，一次最多传输2g数据
            long size = from.size();
            // left 变量代表还剩余多少字节
            for (long left = size; left > 0;) {
                left -= from.transferTo((size - left), left, to);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    ```
8. Path  
  jdk7引入Path和Paths
    * Path用来表示文件路径
    * Paths是工具类
    ``` 
       Path source = Paths.get("1.txt");//相对路径，使用user.dir环境变量来定位1.txt
       Path source = Paths.get("d:\\1.txt");//绝对路径，代表了d:\1.txt
       Path source = Paths.get("d:/1.txt");//绝对路径，代表了d:\1.txt
       Path source = Paths.get("d:/data", "projects");//代表了d:\data\projects
    ```
    * .代表当前路径
    * ..代表上一级路径：`Path path = Paths.get("d:/data/projects/a/../b)`就是`d:\data\projects\b`
9. Files：也是jdk1.7引入
  ```
    // 检查文件是否存在
    Path path = Paths.get("hellword/data.txt);
    Files.exists(path);
    
    // 创建一级目录,如果目录已经存在会抛出FileAreadyExistsException异常，不能一次创建多级目录，否则抛出NoSuchFileException异常
    Path path = Paths.get("helloword/d1");
    Files.createDirectory(path);
    
    // 创建多级目录
    Path path = Paths.get("helloword/d1/d2");
    Files.createDirectiores(path)
    
    // 拷贝文件，如果文件已存在会抛出FileAlreadyExistsException，其底层也是用的操作系统底层实现，和Channel方式的拷贝文件各有优劣，效率也比较高
    Path source = Paths.get("helloword/data.txt");
    Path target = Paths.get("helloword/target.txt");
    Files.copy(source, target);
    // 用source覆盖掉target，需要使用StandardCopyOption来控制
    Files.copy(source, tartget, StandardCopyOption.REPLACE_EXISTING)
    
    // 移动文件，使用StandardCopyOption.ATOMIC_MOVE保证文件移动的原子性
    Path source = Paths.get("helloword/data.txt");
    Path target = Paths.get("helloword/target.txt");
    Files.copy(source, tartget, StandardCopyOption.ATOMIC_MOVE)
    
    // 删除文件，如果文件不存在，会抛出NoSuchFileException异常
    Path target = Paths.get("helloword/target.txt");
    Files.delete(target);
    
    // 删除目录，如果目录还有内容会抛出DirectoryNotEmptyException
    Path target = Paths.get("helloword/d1");
    Files.delete(target)
    
    // 遍历目录文件，jdk1.7之前只能自己去写递归遍历，1.7之后可以直接使用Files.walkFileTree方法
    Files.walkFileTree(Paths.get("D:\\Redis-x64-3.0.504"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("=====>" + dir);
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                return super.visitFile(file, attrs);
            }
        });
  ```
### 网络编程
1. 非阻塞 vs 阻塞
    * 非阻塞
