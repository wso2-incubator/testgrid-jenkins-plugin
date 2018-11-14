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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.testgrid.jenkins;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.wso2.testgrid.common.TestScenario;
import org.wso2.testgrid.common.config.DeploymentConfig;
import org.wso2.testgrid.common.config.InfrastructureConfig;
import org.wso2.testgrid.common.config.ScenarioConfig;
import org.wso2.testgrid.common.config.Script;
import org.wso2.testgrid.common.config.TestgridYaml;
import org.wso2.testgrid.jenkins.deployment.JenkinsDeploymentConfig;
import org.wso2.testgrid.jenkins.deployment.JenkinsDeploymentPatternConfig;
import org.wso2.testgrid.jenkins.deployment.JenkinsDeploymentPatternScript;
import org.wso2.testgrid.jenkins.infrastructure.JenkinsInfrastructureConfig;
import org.wso2.testgrid.jenkins.infrastructure.JenkinsScriptConfig;
import org.wso2.testgrid.jenkins.scenario.JenkinsScenario;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * This class adds a new Job property using the extension point to add new configuration
 * options to capture testgrid related configurations
 */
public final class TestgridJobProperty extends JobProperty<Job<?, ?>> {

    private final List<JenkinsInfrastructureConfig> infrastructureConfigs;
    private final List<JenkinsDeploymentConfig> deploymentConfigs;
    private List<JenkinsScenario> scenarioList;
    private String scenarioGitURL;
    private String scenarioTestType;
    private String scenarioGitBranch;
    private List<IncludeParameter> includeParameters;
    private List<ExcludeParameter> excludeParameters;
    private boolean useIncludeParameters;
    private boolean useExcludeParameters;

    private final String testgridYaml;

    /**
     * Data bound constructor receives all values via UI and perform
     * the config.xml persistence in the file system
     */
    @DataBoundConstructor
    public TestgridJobProperty(List<JenkinsInfrastructureConfig> infrastructureConfigs, List<JenkinsDeploymentConfig> deploymentConfigs
            , List<IncludeParameter> includeParameters, List<ExcludeParameter> excludeParameters,
                               boolean useIncludeParameters, boolean useExcludeParameters, List<JenkinsScenario> scenarioList, String scenarioGitURL, String scenarioTestType
            , String scenarioGitBranch) {

        this.infrastructureConfigs = infrastructureConfigs;
        this.deploymentConfigs = deploymentConfigs;
        this.includeParameters = useIncludeParameters ? includeParameters : null;
        this.excludeParameters = useExcludeParameters ? excludeParameters : null;
        this.useIncludeParameters = useIncludeParameters;
        this.useExcludeParameters = useExcludeParameters;
        this.scenarioList = scenarioList;
        this.scenarioGitURL = scenarioGitURL;
        this.scenarioTestType = scenarioTestType;
        this.scenarioGitBranch = scenarioGitBranch;
        //save TestGrid yaml string content
        this.testgridYaml = getTestGridYaml();
    }

    public List<JenkinsInfrastructureConfig> getInfrastructureConfigs() {
        return infrastructureConfigs;
    }

    public List<JenkinsScenario> getScenarioList() {
        return scenarioList;
    }

    public List<JenkinsDeploymentConfig> getDeploymentConfigs() {
        return deploymentConfigs;
    }

    public String getTestgridYaml() {
        return testgridYaml;
    }

    public List<IncludeParameter> getIncludeParameters() {
        return includeParameters;
    }

    public List<ExcludeParameter> getExcludeParameters() {
        return excludeParameters;
    }

    public boolean getUseIncludeParameters() {
        return useIncludeParameters;
    }

    public boolean getUseExcludeParameters() {
        return useExcludeParameters;
    }

    public String getScenarioGitURL() {
        return scenarioGitURL;
    }

    public String getScenarioTestType() {
        return scenarioTestType;
    }

    public String getScenarioGitBranch() {
        return scenarioGitBranch;
    }

    /**
     * Process the data and generate the TestGrid YAML file.
     *
     * @return the YAML formatted string
     */
    private String getTestGridYaml() {

        TestgridYaml testgridYaml = new TestgridYaml();
        InfrastructureConfig config = new InfrastructureConfig();
        config.setInfrastructureProvider(InfrastructureConfig.InfrastructureProvider.AWS);
        config.setIacProvider(InfrastructureConfig.IACProvider.CLOUDFORMATION);
        config.setContainerOrchestrationEngine(InfrastructureConfig.ContainerOrchestrationEngine.None);
        List<InfrastructureConfig.Provisioner> provisioners = new ArrayList<>();

        if (includeParameters != null) {
            List<String> includes = new ArrayList<>();
            for (IncludeParameter includeParameter : includeParameters) {
                includes.add(includeParameter.getIncludeParameter());
            }
            config.setIncludes(includes);
        }

        if (excludeParameters != null) {
            List<String> excludes = new ArrayList<>();
            for (ExcludeParameter excludeParameter : excludeParameters) {
                excludes.add(excludeParameter.getExcludeParameter());
            }
            config.setIncludes(excludes);
        }

        if (infrastructureConfigs !=null){
            for (JenkinsInfrastructureConfig infraConfig : infrastructureConfigs) {
                InfrastructureConfig.Provisioner provisioner = new InfrastructureConfig.Provisioner();
                provisioner.setName(infraConfig.getName());
                provisioner.setDescription(infraConfig.getDescription());
                if (infraConfig.getGitBranch() != null) {
                    provisioner.setRemoteRepository("-b " + infraConfig.getGitBranch() + " " + infraConfig.getGitURL());
                } else {
                    provisioner.setRemoteRepository(infraConfig.getGitURL());
                }
                List<JenkinsScriptConfig> scriptConfigs = infraConfig.getScriptConfigs();
                if (scriptConfigs != null) {
                    List<Script> scripts = new ArrayList<>();
                    for (JenkinsScriptConfig scriptConfig : scriptConfigs) {
                        Script script = new Script();
                        script.setName(scriptConfig.getName());
                        script.setDescription(scriptConfig.getDescription());
                        script.setType(Script.ScriptType.valueOf(scriptConfig.getIacProvider()));
                        script.setFile(scriptConfig.getFile());
                        Properties properties = new Properties();
                        Arrays.stream(scriptConfig.getParameters().split("\n")).forEach(s -> {
                            String[] split = s.split(":");
                            if (split.length == 2) {
                                properties.setProperty(split[0].trim(), split[1].trim());
                            }
                        });
                        script.setInputParameters(properties);
                        scripts.add(script);
                    }
                    provisioner.setScripts(scripts);
                }
                provisioners.add(provisioner);
            }
            config.setProvisioners(provisioners);
        }

        //DeploymentConfig
        DeploymentConfig tgDeploymentConfig = new DeploymentConfig();

        if(deploymentConfigs != null){
            for (JenkinsDeploymentConfig deploymentConfig : deploymentConfigs) {
                List<JenkinsDeploymentPatternConfig> deploymentPatternConfigs = deploymentConfig.getDeploymentPatternConfigs();
                List<DeploymentConfig.DeploymentPattern> deploymentPatterns = new ArrayList<>();
                for (JenkinsDeploymentPatternConfig deploymentPatternConfig : deploymentPatternConfigs) {
                    DeploymentConfig.DeploymentPattern pattern = new DeploymentConfig.DeploymentPattern();
                    pattern.setName(deploymentPatternConfig.getName());
                    pattern.setDescription(deploymentPatternConfig.getDescription());
                    pattern.setDir("."); //hard-coded
                    pattern.setRemoteRepository(deploymentPatternConfig.getGitURL());

                    List<Script> deploymentScripts = new ArrayList<>();

                    List<JenkinsDeploymentPatternScript> deploymentPatternScripts = deploymentPatternConfig.getDeploymentPatternScripts();
                    if (deploymentPatternScripts != null) {
                        for (JenkinsDeploymentPatternScript deploymentPatternScript : deploymentPatternScripts) {
                            Script script = new Script();
                            script.setName(deploymentPatternScript.getName());
                            script.setType(Script.ScriptType.valueOf(deploymentPatternScript.getType()));
                            script.setFile(deploymentPatternScript.getFile());
                            Properties properties = new Properties();
                            Arrays.stream(deploymentPatternScript.getParameters().split("\n")).forEach(s -> {
                                String[] split = s.split(":");
                                if (split.length == 2) {
                                    properties.setProperty(split[0].trim(), split[1].trim());
                                }
                            });
                            script.setInputParameters(properties);
                            deploymentScripts.add(script);
                        }
                    }
                    pattern.setScripts(deploymentScripts);
                    deploymentPatterns.add(pattern);
                }
                tgDeploymentConfig.setDeploymentPatterns(deploymentPatterns);
            }
        }

        //Scenario Config
        ScenarioConfig tgScenarioConfig = new ScenarioConfig();

        if (scenarioGitBranch != null) {
            tgScenarioConfig.setRemoteRepository("-b " + scenarioGitBranch + " " + scenarioGitURL);
        } else {
            tgScenarioConfig.setRemoteRepository(scenarioGitURL);
        }
        tgScenarioConfig.setTestType(scenarioTestType);
        List<TestScenario> testScenarios = new ArrayList<>();

        if(scenarioList !=null){
            for (JenkinsScenario jenkinsScenario : scenarioList) {
                TestScenario scenario = new TestScenario();
                scenario.setName(jenkinsScenario.getName());
                scenario.setDescription(jenkinsScenario.getDescription());
                scenario.setDir(jenkinsScenario.getDir());
                testScenarios.add(scenario);
            }
            tgScenarioConfig.setScenarios(testScenarios);
        }

        //set the main config sections to testgrid yaml object
        testgridYaml.setInfrastructureConfig(config);
        testgridYaml.setDeploymentConfig(tgDeploymentConfig);
        testgridYaml.setScenarioConfig(tgScenarioConfig);

        //Representer to filter out null values in TestGridYaml object
        Representer representer = new Representer() {
            @Override
            protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
                if (propertyValue == null) {
                    return null;
                } else {
                    return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
                }
            }
        };

        //Dumper options set to achieve proper yaml format
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(representer,options);

        return yaml.dump(testgridYaml);
    }

    @Symbol("testgrid")
    @Extension
    public static class PropertyImpl extends JobPropertyDescriptor {

        @Override
        public boolean isApplicable(Class<? extends Job> jobType) {
            //TODO applicable only in pipeline projects
            return true;
        }

        @Override
        public JobProperty<?> newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return super.newInstance(req, formData);
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "TestGrid";
        }

        public ListBoxModel doFillScenarioTestTypeItems() {
            ListBoxModel model = new ListBoxModel();
            model.add(new ListBoxModel.Option("INTEGRATION"));
            return model;
        }

        public FormValidation doCheckScenarioGitURL(@QueryParameter String value){
            if(value.isEmpty()){
                return FormValidation.error("* Required Parameter..");
            }else{
                return FormValidation.ok();
            }
        }

        public FormValidation doCheckScenarioGitBranch(@QueryParameter String value){
            if(value.isEmpty()){
                return FormValidation.error("* Required Parameter..");
            }else{
                return FormValidation.ok();
            }
        }

        public FormValidation doCheckInfrastructureConfigs(@QueryParameter List<JenkinsInfrastructureConfig> value){
            if(value.isEmpty()){
                return FormValidation.error("* Required Parameter..");
            }else{
                return FormValidation.ok();
            }
        }
    }


    public static class IncludeParameter implements Describable<IncludeParameter> {

        private String includeParameter;

        @DataBoundConstructor
        public IncludeParameter(String includeParameter) {
            this.includeParameter = includeParameter;
        }

        public String getIncludeParameter() {
            return includeParameter;
        }

        @Override
        public Descriptor<IncludeParameter> getDescriptor() {
            return Jenkins.getInstance().getDescriptor(getClass());
        }

        @Extension
        public static class ParameterDescriptorImpl extends Descriptor<IncludeParameter> {}
    }

    public static class ExcludeParameter implements Describable<ExcludeParameter> {

        private String excludeParameter;

        @DataBoundConstructor
        public ExcludeParameter(String excludeParameter) {
            this.excludeParameter = excludeParameter;
        }

        public String getExcludeParameter() {
            return excludeParameter;
        }

        @Override
        public Descriptor<ExcludeParameter> getDescriptor() {
            return Jenkins.getInstance().getDescriptor(getClass());
        }

        @Extension
        public static class ParameterDescriptorImpl extends Descriptor<ExcludeParameter> {}
    }
}
