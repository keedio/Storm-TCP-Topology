package com.keedio.storm.topology;

import org.keedio.storm.bolt.filter.FilterMessageBolt;
import org.keedio.storm.bolt.tcp.TCPBolt;
import org.keedio.storm.bolt.filterkey.FilterkeyBolt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;


public class StormTCPTopology {
	public static final Logger LOG = LoggerFactory
			.getLogger(StormTCPTopology.class);

	private final TopologyProperties topologyProperties;

	public StormTCPTopology(TopologyProperties topologyProperties) {
		this.topologyProperties = topologyProperties;
	}
	
	public void runTopology() throws Exception{

		StormTopology stormTopology = buildTopology();
		String stormExecutionMode = topologyProperties.getStormExecutionMode();
	
		switch (stormExecutionMode){
			case ("cluster"):
				StormSubmitter.submitTopology(topologyProperties.getTopologyName(), topologyProperties.getStormConfig(), stormTopology);
				break;
			case ("local"):
			default:
				LocalCluster cluster = new LocalCluster();
				cluster.submitTopology(topologyProperties.getTopologyName(), topologyProperties.getStormConfig(), stormTopology);
				Thread.sleep(topologyProperties.getLocalTimeExecution());
				cluster.killTopology(topologyProperties.getTopologyName());
				cluster.shutdown();
				System.exit(0);
		}	
	}
	
	private StormTopology buildTopology()
	{
		BrokerHosts kafkaBrokerHosts = new ZkHosts(topologyProperties.getZookeeperHosts());
		String kafkaTopic = topologyProperties.getKafkaTopic();
		SpoutConfig kafkaConfig = new SpoutConfig(kafkaBrokerHosts, kafkaTopic, "/storm/kafka/"+topologyProperties.getTopologyName(), kafkaTopic);
		kafkaConfig.forceFromStart = topologyProperties.isKafkaStartFromBeginning();
		
		boolean filterBoltEnabled = topologyProperties.isFilterBoltEnabled();
		boolean filterKeyBoltEnabled = topologyProperties.isFilterKeyBoltEnabled();
		

		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("KafkaSpout", new KafkaSpout(kafkaConfig), topologyProperties.getKafkaSpoutParallelism());
		
		if (filterBoltEnabled==true && filterKeyBoltEnabled==true){
			builder.setBolt("FilterBolt", new FilterMessageBolt(), topologyProperties.getFilterBoltParallelism()).shuffleGrouping("KafkaSpout");
			builder.setBolt("FilterKeyBolt", new FilterkeyBolt(), topologyProperties.getFilterkeyBoltParallelism()).shuffleGrouping("FilterBolt");
			builder.setBolt("TCPBolt", new TCPBolt(), topologyProperties.getTcpBoltParallelism()).shuffleGrouping("FilterKeyBolt");		
		}
		if (filterBoltEnabled==true && filterKeyBoltEnabled==false){
			builder.setBolt("FilterBolt", new FilterMessageBolt(), topologyProperties.getFilterBoltParallelism()).shuffleGrouping("KafkaSpout");
			builder.setBolt("TCPBolt", new TCPBolt(), topologyProperties.getTcpBoltParallelism()).shuffleGrouping("FilterBolt");		
		}
		if (filterBoltEnabled==false && filterKeyBoltEnabled==true){
			builder.setBolt("FilterKeyBolt", new FilterkeyBolt(), topologyProperties.getFilterkeyBoltParallelism()).shuffleGrouping("KafkaSpout");
			builder.setBolt("TCPBolt", new TCPBolt(), topologyProperties.getTcpBoltParallelism()).shuffleGrouping("FilterKeyBolt");		
		}
		if (filterBoltEnabled==false && filterKeyBoltEnabled==false){	
			builder.setBolt("TCPBolt", new TCPBolt(), topologyProperties.getTcpBoltParallelism()).shuffleGrouping("KafkaSpout");		
		}
                
		return builder.createTopology();
	}
	
	public static void main(String[] args) throws Exception {
		String propertiesFile = args[0];
		TopologyProperties topologyProperties = new TopologyProperties(propertiesFile);
		StormTCPTopology topology = new StormTCPTopology(topologyProperties);
		topology.runTopology();
	}
}
