
5 同一天同一个客户端id的下单，多次下单追加到同一个订单里
6 常购中商品按照种类排序

15. 下单时间的格式
16. 订单id格式？
17. 启动性能
18. how long to visit server to get new data?

19. setUserVisibleHint
26. do something when logout

test case !

====================

1. 找回密码功能接口
2. 获取订单信息接口
3. 注册时不需要：customer_name	string	Y	商户联系人名称
          customer_shop_address	string	Y	商户店铺地址


---------------------------------------------------------
TODO:

1. 本地添加供货商成功后，有可能上传服务端失败;类似的本地可修改的数据，都可能有此问题----需要一个数据同步方式; 数据同步区分：客户端只读数据和读写数据
2. 商品搜索功能
3. 无网络情况的UI展现以及逻辑处理