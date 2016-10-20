
   
   
      支持多线程操作，通过生产&消费实现；
 connection-kvdatabase为请求-响应的生产-消费方。
 
      使用blockingQueue即可，不用自己就行控制。
   
    spin模式下，175万个TPS；
   直接访问Map，存在写入频繁，造成dump一直完不成问题。