# Tubectl 使用说明

### 命令

```bash
$ ./bin/tubectl.sh {topic|producer|consumer} [option]
# 查看具体用法
$ ./bin/tubectl.sh --help
```

#### Topic 管理相关命令

查看topic命令用法，

```bash
$ ./bin/tubectl.sh topic --help
```

Topic命令参数说明，

```bash
--brokerId <String: broker id>                                             The broker to operator on.
-h,--help                                                                     Print usage information.
--master-portal <String: format is master_ip:master_webport>               Master Service portal to which to connect.(default: 127.0.0.1:8080)
--method <String: http call method>                                        Http call method
--publish <bool: true or false>                                            Determin if topic accepts publish.
--show-methods                                                             Return http's methods.
--subscribe <bool: true or false>                                          Determine if topic accepts subscribe.
-token,--auth-token <String: API operation authorization code>             API operation authorization code, required when adding or modifying, optional when querying
-v,--version                                                                                         Display TubeMQ version.
```

查看操作topic的函数，

```bash
$ ./bin/tubectl.sh topic --show-methods
admin_query_topic_broker_config_info, admin_add_new_topic_record, admin_delete_topic_info, admin_modify_topic_info
# 四个函数分别用于查询topic，添加topic，删除topic和修改topic
```

查询topic命令举例，

```bash
$ ./bin/tubectl.sh topic --method admin_query_topic_broker_config_info --brokerId 1
...
{"result":true,"errCode":0,"errMsg":"OK","data":[],"count":0}
# 表明当前broker没有topic
```

添加topic命令举例，

```bash
$ ./bin/tubectl.sh topic --method admin_add_new_topic_record --topicName demo --brokerId 1 --auth-token abc
...
{"result":true,"errCode":0,"errMsg":"OK","data":[{"brokerId":1,"topicName":"demo","success":true,"errCode":200,"errInfo":"Ok!"}],"count":1}
```

将topic修改为不可发布和不可订阅状态命令举例，

```bash
$ ./bin/tubectl.sh topic --method admin_modify_topic_info --topicName demo --brokerId 1 --auth-token abc --subscribe false --publish false
...
{"result":true,"errCode":0,"errMsg":"OK","data":[{"brokerId":1,"topicName":"demo","success":true,"errCode":200,"errInfo":"Ok!"}],"count":1}
```

删除topic命令举例，

```bash
$ ./bin/tubectl.sh topic --method admin_delete_topic_info --topicName demo --brokerId 1 --auth-token abc
...
{"result":true,"errCode":0,"errMsg":"OK","data":[{"brokerId":1,"topicName":"demo","success":true,"errCode":200,"errInfo":"Ok!"}],"count":1}
```

#### 生产消息命令

查看生产消息命令用法，

```bash
$ ./bin/tubectl.sh producer --help
```

生产消息命令参数说明，

```bash
--client-count <Int: client count, [1, 100]>                               Number of producers or consumers to started.
--conn-reuse <bool: true or false>                                         Different clients reuse TCP connections. (default: true)
-h,--help                                                                     Print usage information.
--master-servers <String: format is master1_ip:port[,master2_ip:port]>     The master address(es) to connect to.
--message-data-size <Int: message size,(0, 1024 * 1024)>                   message's data size in bytes. Note that you must provide exactly one of --msg-data-size or --payload-file.
--messages <Long: count>                                                   The number of messages to send or consume, If not set, production or consumption is continual.
--num-send-threads <Integer: count, [1,200]>                               Number of send message threads, default: num of cpu count.
--output-interval <Integer: interval_ms, [5000, +)>                        Interval in milliseconds at which to print progress info. (default: 5000)
--rpc-timeout <Long: milliseconds>                                         The maximum duration between request and response in milliseconds. (default: 10000)
--sync-produce                                                             Synchronous production. (default: false)
-topic,--topicName <String: topic, format is topic_1[,topic_2[:filterCond_2.1[\;filterCond_2.2]]]>   The topic(s) to produce messages to.
-v,--version                                                               Display TubeMQ version.
--without-delay                                                            Production without delay. (default: false)
```

生产消息命令举例，

```bash
$ ./bin/tubectl.sh producer --master-servers YOUR_MASTER_IP1:port,YOUR_MASTER_IP2:port --topicName demo
...
Continue, cost time: 6579ms, required count VS sent count = -2 : 5000 (5000:0:0)
Continue, cost time: 11580ms, required count VS sent count = -2 : 9856 (9857:0:0)
...
```

#### 消费消息命令

查看消费消息命令用法，

```bash
$ ./bin/tubectl.sh consumer --help
```

消费消息命令参数说明，

```bash
--client-count <Int: client count, [1, 100]>                  Number of producers or consumers to started.
--conn-reuse <bool: true or false>                            Different clients reuse TCP connections. (default: true)
--consume-position <Integer: [-1,0, 1]>                       Set the start position of the consumer group. The value can be [-1, 0, 1]. Default value is 0. -1: Start from 0 for the first time. Otherwise start from last consume position. 0: Start from the latest position for the first time. Otherwise start from last consume position. 1: Start from the latest consume position.
--consume-push                                                Push consumption action.(default: pull)
-group,--groupName <String: consumer group>                   The consumer group name of the consumer. (default: test_consume)
-h,--help                                                     Print usage information.
--master-servers <String: format is master1_ip:port[,master2_ip:port]>              The master address(es) to connect to.
--messages <Long: count>                                      The number of messages to send or consume, If not set, production or consumption is continual.
--num-fetch-threads <Integer: count, [1,100]>                 Number of fetch threads, default: num of cpu count.
--output-interval <Integer: interval_ms, [5000, +)>           Interval in milliseconds at which to print progress info. (default: 5000)
--rpc-timeout <Long: milliseconds>                            The maximum duration between request and response in milliseconds. (default: 10000)
-topic,--topicName <String: topic, format is topic_1[[:filterCond_1.1[\;filterCond_1.2]][,topic_2]]>   The topic(s) to consume on.
-v,--version                                                  Display TubeMQ version.
```

消费消息命令举例，

```bash
$ ./bin/tubectl.sh consumer --master-servers YOUR_MASTER_IP1:port,YOUR_MASTER_IP2:port --topicName demo --groupName test_consume
...
Continue, cost time: 16626 ms, required count VS received count = -2 : 4000
Topic Name = demo, count=4000
Continue, cost time: 21626 ms, required count VS received count = -2 : 8000
Topic Name = demo, count=800
```



