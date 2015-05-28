# Storm-TCP-Topology
## Description
Storm topology consuming JSON kafka messages; it performs message filtering (both vertical and horizontal) and finally publishes to Splunk (via TCP communications to configured host and port).

This topology consumes messages with next format:
```
{"extraData":"fsfsdf","message":"adios amigo <date>11-23-24  <time>22:22:22 sflhsldfjs"}
```
## Compilation

```
mvn clean package
````

## Dependecies
This project depends on next projects:
* [storm-filterregex-bolt](https://github.com/keedio/storm-filterregex-bolt)
* [storm-tcp-bolt](https://github.com/keedio/storm-tcp-bolt)
* [storm-filterkey-bolt] (https://github.com/keedio/Storm-filterkey-bolt)
* [metrics-core](https://github.com/dropwizard/metrics)

Configuration properties

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

# Filter extradata of message according maps of keys ([storm-filterkey-bolt] (https://github.com/keedio/Storm-filterkey-bolt))
 key.selection.criteria.1 = {"key":{"Field1":"value1"},"values":["Field2","Field3"]}"} 

# CLUSTER PROPERTIES:
# Storm Nimbus host (default localhost)
storm.nimbus.host=streaming1

# Storm Nimbus port (default 6627)
# storm.nimbus.port

#Bolt filtrado
filter.bolt.allow
filter.bolt.deny
conf.pattern1=(<date>[^\\s]+)\\s+(<time>[^\\s]+)\\s+
conf.pattern2=(<date>[^\\s]+)\\s+
```

## Deploy
In order to install this topology you have to copy next libs to the storm lib directory:
* Storm-TCP-Topology.XXX.jar

## Version history
2.0.5 added bolt filterkey map to topology.