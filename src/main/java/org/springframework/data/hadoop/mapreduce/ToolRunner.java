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
package org.springframework.data.hadoop.mapreduce;

import org.apache.hadoop.util.Tool;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Wrapper around {@link org.apache.hadoop.util.ToolRunner} allowing for an easier configuration and execution
 * of {@link Tool}  instances inside Spring.
 * Optionally returns the execution result (as an int per {@link Tool#run(String[])}).
 * <p/>Note by default, the runner is configured to execute at startup. One can customize this behaviour through {@link #setRunAtStartup(boolean)}/
 * <p/>This class is a factory bean - if {@link #setRunAtStartup(boolean)} is set to false, then the action (namely the execution of the Tool) is postponed by the call
 * to {@link #getObject()}.
 * 
 * @author Costin Leau
 */
public class ToolRunner extends ToolExecutor implements FactoryBean<Integer>, InitializingBean {

	private volatile Integer result = null;
	private boolean runAtStartup = false;

	@Override
	public Integer getObject() throws Exception {
		if (result == null) {
			result = runCode();
		}
		return result;
	}

	@Override
	public Class<?> getObjectType() {
		return int.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();

		if (runAtStartup) {
			getObject();
		}
	}

	/**
	 * Indicates whether the tool should run at container startup (the default) or not.
	 *
	 * @param runAtStartup The runAtStartup to set.
	 */
	public void setRunAtStartup(boolean runAtStartup) {
		this.runAtStartup = runAtStartup;
	}
}