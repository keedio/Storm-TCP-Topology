package com.keedio.storm.metric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;

public class GroupMetric {

	private static final Logger LOG = LoggerFactory
			.getLogger(GroupMetric.class);

	private MetricRegistry meter;
	
	public GroupMetric(MetricRegistry meter) {
		this.meter = meter;
	}
	
	
	
}
