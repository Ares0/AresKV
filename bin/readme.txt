
   
   
    支持dump操作，持久化到日志中；
DataOutputStream写入UTF，包装了length、unicode;
FileOutputStream-BufferedOutputStream-DataOutputStream逐层增强。

  db不能一直存在，考虑服务器模式；
 直接访问Map，存在写入频繁，造成dump一直完不成问题。