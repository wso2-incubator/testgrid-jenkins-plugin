/*
* Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.testgrid.jenkins.infrastructure;

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
 * Defines the variables in the Infrastructure script config section of the UI
 * there is an accompanying config.xml file that defines the UI components
 * in the resource folder.
 */
public class JenkinsScriptConfig implements Describable<JenkinsScriptConfig>{

    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
    private String name;
    private String file;
    private String description;
    private String parameters;
    private String iacProvider;

    @DataBoundConstructor
    public JenkinsScriptConfig(String name, String file, String description, String iacProvider, String parameters) {
        this.name = name;
        this.file = file;
        this.description = description;
        this.iacProvider = iacProvider;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getIacProvider() {
        return iacProvider;
    }

    public void setIacProvider(String iacProvider) {
        this.iacProvider = iacProvider;
    }

    @Override
    public Descriptor<JenkinsScriptConfig> getDescriptor() {
        return DESCRIPTOR;
    }

    public static class DescriptorImpl extends Descriptor<JenkinsScriptConfig>{

        /**
         * Method called by Jenkins internaly to populate the
         * iacProvider comboBox
         *
         */
        public ListBoxModel doFillIacProviderItems(){
            ListBoxModel model = new ListBoxModel();
            model.add(new ListBoxModel.Option("CLOUDFORMATION"));
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

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Scripts";
        }
    }
}
