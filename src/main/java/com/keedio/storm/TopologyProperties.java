package com.keedio.storm;

import backtype.storm.Config;
import backtype.storm.metric.LoggingMetricsConsumer;

import com.keedio.storm.metric.JMXMetricConsumer;

import java.io.FileInputStream;
import java.util.ArrayList;
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


	public TopologyProperties(String fileName){
		
		stormConfig = new Config();

		try {
			setProperties(fileName);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
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
		topologyName = properties.getProperty("storm.topology.name","defaultTopologyName");
		localTimeExecution = Integer.parseInt(properties.getProperty("storm.local.execution.time","20000"));
		
		kafkaTopic = properties.getProperty("kafka.topic");
		if (kafkaTopic == null)
			throw new ConfigurationException("Kafka topic must be specified in topology properties file");
			
		kafkaStartFromBeginning = new Boolean(properties.getProperty("kafka.startFromBeginning","false"));
		setStormConfig(properties);
	}

	private void setStormConfig(Properties properties) throws ConfigurationException
	{
		stormExecutionMode = properties.getProperty("storm.execution.mode","local");
		int stormWorkersNumber = Integer.parseInt(properties.getProperty("storm.workers.number","2"));
		int maxTaskParallism = Integer.parseInt(properties.getProperty("storm.max.task.parallelism","2"));
		
		zookeeperHosts = properties.getProperty("zookeeper.hosts");
		if (zookeeperHosts == null){
			throw new ConfigurationException("Zookeeper hosts must be specified in configuration file");
		}
		
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
		stormConfig.put("filter.bolt.allow", properties.getProperty("filter.bolt.allow",""));
		stormConfig.put("filter.bolt.deny", properties.getProperty("filter.bolt.deny",""));
		stormConfig.put("metrics.reporter.yammer.facade..metric.bucket.seconds", properties.getProperty("metrics.reporter.yammer.facade..metric.bucket.seconds"));
		
		// TCP bolt connection properties
		String tcpHost = properties.getProperty("tcp.bolt.host");
		String tcpPort =  properties.getProperty("tcp.bolt.port");
		if (tcpHost == null || tcpPort == null)
			throw new ConfigurationException("TCP destination Host and Port must be specified in topology properties file");
		stormConfig.put("tcp.bolt.host", tcpHost);
		stormConfig.put("tcp.bolt.port", tcpPort);

        // register metric consumer
        stormConfig.registerMetricsConsumer(JMXMetricConsumer.class, 1);
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
