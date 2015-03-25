### Storm-TCP-Topology

## Compilation
``` mvn package ```

## Configuration properties file
```
# MANDATORY PROPERTIES

# zookeeper hosts and ports (eg: localhost:2181)
zookeeper.hosts=zookeeper1:2181,zookeeper2:2181,zookeeper3:2181

# kafka topic for read messages
kafka.topic=test
# start from the first message in topic (true) or proccess only new appends (false)
kafka.startFromBeginning=true

# Splunk tcp properties
tcp.bolt.host=tcpHost
tcp.bolt.port=2000

# OPTIONAL PROPERTIES

# Filter messages rules, regexp expression are used
# If allow is setted only the messages matching the regexp will be sent to host:port configured via TCP
#filter.bolt.allow=.*||22.9.43.17.*
# If deny is setted the messages matching the regexp will be discarded
#filter.bolt.deny=

# Numbers of workers to parallelize tasks (default 2)
storm.workers.number=4

# Numbers of max task for topology (default 2)
#storm.max.task.parallelism=4

# Storm topolgy execution mode (local or cluster, default local)
storm.execution.mode=cluster

# Storm Topology Name (default AuditActiveLoginsCount)
storm.topology.name=Topology-Name

# Storm batch emmit interval (default 2000)
#storm.topology.batch.interval.miliseconds

# Time of topology execution, in miliseconds (only in local mode, default 20000)
storm.local.execution.time=1200000


# CLUSTER PROPERTIES:
# Storm Nimbus host (default localhost)
storm.nimbus.host=streaming1

# Storm Nimbus port (default 6627)
# storm.nimbus.port
```

### Use
Follow the usual way to run Storm Topologies:  
```$ storm jar Storm-TCP-Topology-1.0.0.jar com/keedio/storm/StormSplunkTCPTopology <configuration-file>```
