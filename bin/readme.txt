
   
   
     实现一个嵌入式，只支持String的内存KV存储；
引擎采用单线程，避免多线程并发问题。

  java -Xms512m -Xmx2048m -jar 100.jar 
   一百万写：345  五百万读：406
  
  java -Xms3000m -Xmx4096m -jar 1000.jar 
   一千万写：5203 五千万读：4693