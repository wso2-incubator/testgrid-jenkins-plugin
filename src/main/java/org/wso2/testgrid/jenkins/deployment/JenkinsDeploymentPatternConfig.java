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
import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.util.List;

/**
 * Defines the variables in the Deployment pattern config section of the UI
 * there is an accompanying config.xml file that defines the UI components
 * in the resource folder.
 */
public class JenkinsDeploymentPatternConfig implements Describable<JenkinsDeploymentPatternConfig> {

    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
    private String name;
    private String description;
    private String gitURL;
    private String gitBranch;
    private List<JenkinsDeploymentPatternScript> deploymentPatternScripts;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<JenkinsDeploymentPatternScript> getDeploymentPatternScripts() {
        return deploymentPatternScripts;
    }

    public String getGitURL() {
        return gitURL;
    }

    public String getGitBranch() {
        return gitBranch;
    }

    @DataBoundConstructor
    public JenkinsDeploymentPatternConfig(String name, String description, List<JenkinsDeploymentPatternScript> deploymentPatternScripts,String gitURL,String gitBranch) {
        this.name = name;
        this.description = description;
        this.gitURL = gitURL;
        this.deploymentPatternScripts = deploymentPatternScripts;
        this.gitBranch = gitBranch;
    }

    @Override
    public Descriptor<JenkinsDeploymentPatternConfig> getDescriptor() {
        return DESCRIPTOR;
    }

    public static class DescriptorImpl extends Descriptor<JenkinsDeploymentPatternConfig>{

        public FormValidation doCheckName(@QueryParameter String value){
            if(value.isEmpty()){
                return FormValidation.error("* Required Parameter..");
            }else{
                return FormValidation.ok();
            }
        }

        public FormValidation doCheckGitURL(@QueryParameter String value){
            if(value.isEmpty()){
                return FormValidation.error("* Required Parameter..");
            }else{
                return FormValidation.ok();
            }
        }

        public FormValidation doCheckGitBranch(@QueryParameter String value){
            if(value.isEmpty()){
                return FormValidation.error("* Required Parameter..");
            }else{
                return FormValidation.ok();
            }
        }
    }
}
