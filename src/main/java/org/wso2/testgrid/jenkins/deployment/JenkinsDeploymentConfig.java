/*
* Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* WSO2 Inc. licenses this file to you under the Apache License,
* Version 2.0 (the "License"); you may not use this file except
* in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.testgrid.jenkins.deployment;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import javax.annotation.Nonnull;
import java.util.List;

/**
 * Defines the variables in the Deployment config section of the UI
 * there is an accompanying config.xml file that defines the UI components
 * in the resource folder.
 */
public class JenkinsDeploymentConfig implements Describable<JenkinsDeploymentConfig>{

    private List<JenkinsDeploymentPatternConfig> deploymentPatternConfigs;

    @DataBoundConstructor
    public JenkinsDeploymentConfig(List<JenkinsDeploymentPatternConfig> deploymentPatternConfigs) {
        this.deploymentPatternConfigs = deploymentPatternConfigs;
    }

    public List<JenkinsDeploymentPatternConfig> getDeploymentPatternConfigs() {
        return deploymentPatternConfigs;
    }

    @Override
    public Descriptor<JenkinsDeploymentConfig> getDescriptor() {
        return DESCRIPTOR;
    }

    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    /**
     * Static inner class that extends the {@link Descriptor} class to provide the
     * metadata for the {@link JenkinsDeploymentConfig} UI describable class.
     */
    public static class DescriptorImpl extends Descriptor<JenkinsDeploymentConfig>{

        @Nonnull
        @Override
        public String getDisplayName() {
            return "DeploymentConfig";
        }
    }
}
