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
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Defines the variables in the Deployment Pattern script config section of the UI
 * there is an accompanying config.xml file that defines the UI components
 * in the resource folder.
 */
public class JenkinsDeploymentPatternScript implements Describable<JenkinsDeploymentPatternScript> {

    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
    private String name;
    private String type;
    private String file;
    private String parameters;

    @DataBoundConstructor
    public JenkinsDeploymentPatternScript(String name, String type, String file, String parameters) {
        this.name = name;
        this.type = type;
        this.file = file;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getFile() {
        return file;
    }

    public String getParameters() {
        return parameters;
    }

    @Override
    public Descriptor<JenkinsDeploymentPatternScript> getDescriptor() {
        return DESCRIPTOR;
    }


    public static class DescriptorImpl extends Descriptor<JenkinsDeploymentPatternScript>{

        @Nonnull
        @Override
        public String getDisplayName() {
            return"Deployment Pattern Script";
        }

        public ListBoxModel doFillTypeItems() throws IOException {
            ListBoxModel model = new ListBoxModel();
            model.add(new ListBoxModel.Option("SHELL"));
            return model;
        }

        public FormValidation doCheckName(@QueryParameter String value){
            if(value.isEmpty()){
                return FormValidation.error("* Required Parameter..");
            }else{
                return FormValidation.ok();
            }
        }

        public FormValidation doCheckFile(@QueryParameter String value){
            if(value.isEmpty()){
                return FormValidation.error("* Required Parameter..");
            }else{
                return FormValidation.ok();
            }
        }
    }
}
