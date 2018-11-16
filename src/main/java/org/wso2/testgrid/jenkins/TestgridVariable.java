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
import hudson.model.Run;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.jenkinsci.plugins.workflow.cps.GlobalVariable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Export a global variable called 'testgrid' to
 * retrieve the testgrid configuration details.
 * This variable can be referenced via pipelines.
 *
 * This variable can be referenced similar to 'env' variable that is used
 * to access environment variables, and 'params' variable that is used
 * to access build parameters.
 *
 */
@Extension
public class TestgridVariable extends GlobalVariable {

    @Override public String getName() {
        return "testgrid";
    }

    @SuppressWarnings("unchecked")
    @Override public Object getValue(CpsScript script) throws Exception {
        Run<?,?> b = script.$build();
        if (b == null) {
            throw new IllegalStateException("cannot find owning build");
        }

        final TestgridJobProperty testgridJobProperty = b.getParent().getProperty(TestgridJobProperty.class);
        final String testgridYaml = testgridJobProperty.getTestgridYaml();
        // Could extend AbstractMap and make a Serializable lazy wrapper, but getValue impls seem cheap anyway.
        Map<String,Object> values = new HashMap<>();
        values.put("yaml", testgridYaml);

        return Collections.unmodifiableMap(values);
    }
}
