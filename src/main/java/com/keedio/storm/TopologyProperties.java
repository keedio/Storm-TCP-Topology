package com.keedio.storm;

import backtype.storm.Config;
import backtype.storm.metric.LoggingMetricsConsumer;

import com.keedio.storm.metric.JMXMetricConsumer;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class TopologyProperties {
	
	private String kafkaTopic;
	private String topologyName;
	private int localTimeExecution;
	private Config stormConfig;
	private String zookeeperHosts;
	private String stormExecutionMode;
	private boolean kafkaStartFromBeginning;
	private String jsonFilter;

	public TopologyProperties(String fileName){
		
		stormConfig = new Config();

		try {
			setProperties(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Properties readPropertiesFile(String fileName) throws Exception{
		Properties properties = new Properties();
		FileInputStream in = new FileInputStream(fileName);
		properties.load(in);
		in.close();
		return properties;		
	}
	
	private void setProperties(String fileName) throws Exception{
		
		Properties properties = readPropertiesFile(fileName);
		topologyName = properties.getProperty("storm.topology.name","topologyName");
		localTimeExecution = Integer.parseInt(properties.getProperty("storm.local.execution.time","20000"));
		kafkaTopic = properties.getProperty("kafka.topic");
		kafkaStartFromBeginning = new Boolean(properties.getProperty("kafka.startFromBeginning"));
		setStormConfig(properties);
	}

	private void setStormConfig(Properties properties)
	{

		Iterator it = properties.keySet().iterator();
		while (it.hasNext()) {
			String key = (String)it.next();
			String value = (String)properties.get(key);
			
			stormConfig.put(key, value);
		}
		
		stormExecutionMode = properties.getProperty("storm.execution.mode","local");
		int stormWorkersNumber = Integer.parseInt(properties.getProperty("storm.workers.number","2"));
		int maxTaskParallism = Integer.parseInt(properties.getProperty("storm.max.task.parallelism","2"));
		zookeeperHosts = properties.getProperty("zookeeper.hosts");
		int topologyBatchEmitMillis = Integer.parseInt(
				properties.getProperty("storm.topology.batch.interval.miliseconds","2000"));
		String nimbusHost = properties.getProperty("storm.nimbus.host","localhost");
		String nimbusPort = properties.getProperty("storm.nimbus.port","6627");
		
		// How often a batch can be emitted in a Trident topology.
		stormConfig.put(Config.TOPOLOGY_TRIDENT_BATCH_EMIT_INTERVAL_MILLIS, topologyBatchEmitMillis);
		stormConfig.setNumWorkers(stormWorkersNumber);
		stormConfig.setMaxTaskParallelism(maxTaskParallism);
		// Storm cluster specific properties
		stormConfig.put(Config.NIMBUS_HOST, nimbusHost);
		stormConfig.put(Config.NIMBUS_THRIFT_PORT, Integer.parseInt(nimbusPort));
		stormConfig.put(Config.STORM_ZOOKEEPER_PORT, parseZkPort(zookeeperHosts));
		stormConfig.put(Config.STORM_ZOOKEEPER_SERVERS, parseZkHosts(zookeeperHosts));
		// Filter Messages Bolt properties
		//stormConfig.put("filter.bolt.allow", properties.getProperty("filter.bolt.allow",""));
		//stormConfig.put("filter.bolt.deny", properties.getProperty("filter.bolt.deny",""));
		// TCP bolt connection properties
		//stormConfig.put("tcp.bolt.host", properties.getProperty("tcp.bolt.host"));
		//stormConfig.put("tcp.bolt.port", properties.getProperty("tcp.bolt.port"));
		//stormConfig.put("metrics.reporter.yammer.facade..metric.bucket.seconds", properties.getProperty("metrics.reporter.yammer.facade..metric.bucket.seconds"));
		//stormConfig.put("group.separator", properties.getProperty("group.separator"));
		//stormConfig.put("storm.filter.json", properties.getProperty("storm.filter.json"));
		//stormConfig.put("storm.filter.json", properties.getProperty("storm.filter.json"));

		
        // register metric consumer
        //stormConfig.registerMetricsConsumer(JMXMetricConsumer.class, 1);
        stormConfig.registerMetricsConsumer(LoggingMetricsConsumer.class, 1);
	}

	private static int parseZkPort(String zkNodes) 
	{
		String[] hostsAndPorts = zkNodes.split(",");
		int port = Integer.parseInt(hostsAndPorts[0].split(":")[1]);
		return port;
	}
	
	private static List<String> parseZkHosts(String zkNodes) {

		String[] hostsAndPorts = zkNodes.split(",");
		List<String> hosts = new ArrayList<String>(hostsAndPorts.length);

		for (int i = 0; i < hostsAndPorts.length; i++) {
			hosts.add(i, hostsAndPorts[i].split(":")[0]);
		}
		return hosts;
	}
	
	public String getKafkaTopic() {
		return kafkaTopic;
	}

	public String getTopologyName() {
		return topologyName;
	}

	public int getLocalTimeExecution() {
		return localTimeExecution;
	}

	public Config getStormConfig() {
		return stormConfig;
	}
	
	public String getZookeeperHosts() {
		return zookeeperHosts;
	}
	
	public String getStormExecutionMode() {
		return stormExecutionMode;
	}	
	
	public boolean isKafkaStartFromBeginning() {
		return kafkaStartFromBeginning;
	}	
}