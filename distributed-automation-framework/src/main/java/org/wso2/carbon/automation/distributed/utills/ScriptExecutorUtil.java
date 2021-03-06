/*
*Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.carbon.automation.distributed.utills;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.distributed.beans.DockerImageInfoBeans;
import org.wso2.carbon.automation.distributed.beans.EnvironmentInfoBeans;
import org.wso2.carbon.automation.distributed.beans.RepositoryInfoBeans;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;

/**
 * This class serves as the automation script runner and related operations
 */
public class ScriptExecutorUtil {

    private static final Log log = LogFactory.getLog(ScriptExecutorUtil.class);

    private static EnvironmentInfoBeans instanceBeans = new EnvironmentInfoBeans();

    public void scriptExecution() throws IOException, InterruptedException {

        //TODO - Calling for assigning values only ....
        tempFunction();

        //TODO - selecting base image - This will not work for default deployment ....
        String distributedSetupScriptLocation = FrameworkPathUtil.getSystemResourceLocation()
                + "artifacts" + File.separator + "AM" + File.separator + "scripts"
                + File.separator + "bashscripts";

        String[] command = new String[0];

        if (instanceBeans.getPatternName().equals("default")) {

            command = new String[]{"/bin/bash", distributedSetupScriptLocation
                    + File.separator + "distributedsetup.sh", RepositoryInfoBeans.getDockerRegistryLocation(),
                    instanceBeans.getPatternName(), instanceBeans.getDatabaseName(), instanceBeans.getJdkVersion(),
                    FrameworkPathUtil.getSystemResourceLocation()};
        } else {
            command = new String[]{"/bin/bash", distributedSetupScriptLocation
                    + File.separator + "distributedsetup.sh", RepositoryInfoBeans.getDockerRegistryLocation(),
                    instanceBeans.getPatternName(), instanceBeans.getDatabaseName(), instanceBeans.getJdkVersion(),
                    FrameworkPathUtil.getSystemResourceLocation()};
        }

        processOutputGenerator(command);

        scriptValueReader();

    }

    private void processOutputGenerator(String[] command) throws IOException {

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        log.info(" Listing Docker Run Output .... " + Arrays.toString(command) + " is: ");

        while ((line = br.readLine()) != null) {
            log.info(line);

        }
    }

    private static void tempFunction() {
        // setting some values
        instanceBeans.setOsVersion("ubuntu:latest");
        instanceBeans.setJdkVersion("1.7.0_80");
        instanceBeans.setDatabaseName("MySql");
        instanceBeans.setPatternName("default");
    }


    private void scriptValueReader() throws IOException {

        HashMap<String, HashMap> hashMap =  DockerImageInfoBeans.getDockerImagesMap();

        List<String> lines = FileUtils.readLines(new File(FrameworkPathUtil.getSystemResourceLocation() +
                "/artifacts" + File.separator + "AM" + File.separator + "scripts" + File.separator
                + "bashscripts" + File.separator + "images.txt"));

        for (String line : lines) {

            log.info(line);
            HashMap<String, String> hm = new HashMap<String, String>();

            if (!line.equals("")) {
                String key = line.split(":", 2)[0].trim();
                hm.put("image", line.split(":", 2)[1].trim().split("tag:")[0].split("image:")[1]);
                hm.put("tag", line.split(":", 2)[1].trim().split("tag:")[1]);

                hashMap.put(key, hm);
            }
        }
    }
}

