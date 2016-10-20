
   
   
      支持缓存有效期、watch事务；
  DB调用处理链，ExpireHandler、WatchHandler、DataHandler。
  更新dump的文件结构，添加NodeFacade。
   
     空转频繁，db、con空转百万级别；
   直接访问Map，存在写入频繁，造成dump一直完不成问题。