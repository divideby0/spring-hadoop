/*
 * Copyright 2011-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.hadoop.pig;

import java.util.Collection;

import org.apache.pig.PigServer;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Pig tasklet. Note the same {@link PigServer} is shared between invocations. 
 * 
 * @author Costin Leau
 */
public class PigTasklet implements InitializingBean, BeanFactoryAware, Tasklet {

	private PigServer pig;
	private Collection<PigScript> scripts;
	private BeanFactory beanFactory;
	private String pigName;


	@Override
	public void afterPropertiesSet() {
		Assert.isTrue(pig != null || StringUtils.hasText(pigName), "A Pig instance or bean name is required");

		Assert.notEmpty(scripts, "At least one script needs to be specified");
		if (StringUtils.hasText(pigName)) {
			Assert.notNull(beanFactory, "a bean factory is required if the job is specified by name");
			Assert.isTrue(beanFactory.containsBean(pigName), "beanFactory does not contain any bean named [" + pigName
					+ "]");
		}
	}

	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		PigServer p = (pig != null ? pig : beanFactory.getBean(pigName, PigServer.class));

		PigScriptRunner.run(p, scripts, pig == null, pig == null);
		return RepeatStatus.FINISHED;
	}

	/**
	 * Sets the pig scripts to be executed by this tasklet.
	 * 
	 * @param scripts The scripts to set.
	 */
	public void setScripts(Collection<PigScript> scripts) {
		this.scripts = scripts;
	}

	/**
	 * Sets the pig server instance used by this tasklet.
	 * 
	 * @param pig The pig to set.
	 */
	public void setPigServer(PigServer pig) {
		this.pig = pig;
	}

	/**
	 * Sets the PigServer to use, by (bean) name. This is the default
	 * method used by the hdp name space to allow lazy initialization and potential scoping
	 * to kick in.
	 * 
	 * @param pigName The pigName to use.
	 */
	public void setPigServerName(String pigName) {
		this.pigName = pigName;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
}