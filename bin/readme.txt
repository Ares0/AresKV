
   
   
   
       重构开发结构、范型滥用，确定为String\KVObject。
   
    connection不同的客户端不断连接，map会很大且难以回收。
  dump直接访问Map，存在写入频繁，可能造成dump一直完不成问题。