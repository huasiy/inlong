/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.inlong.tubemq.server.tools.cli;

import org.apache.inlong.tubemq.corebase.utils.TStringUtils;
import org.apache.inlong.tubemq.server.common.fielddef.CliArgDef;
import org.apache.inlong.tubemq.server.common.utils.HttpUtils;

import com.google.gson.JsonObject;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CliTopicAdmin extends CliAbstractBase {

    private static final Logger logger =
            LoggerFactory.getLogger(CliBrokerAdmin.class);

    private static final String defBrokerPortal = "127.0.0.1:8080";
    private String createUser = "webapi";
    private String unflushThresold = "1000";
    private String accpetPublish = "true";
    private String numPartitions = "3";
    private String deletePolicy = "delete,168h";
    private String unflushInterval = "10000";
    private String acceptSubscibe = "true";
    private String confModAuthToken;

    public CliTopicAdmin() {
        super("tubectl.sh topic");
        initCommandOptions();
    }

    /**
     * Init command options
     */
    @Override
    protected void initCommandOptions() {
        // add the cli required parameters
        addCommandOption(CliArgDef.MASTERPORTAL);
        addCommandOption(CliArgDef.ADMINMETHOD);
        addCommandOption(CliArgDef.METHOD);
        addCommandOption(CliArgDef.OPETOPIC);
        addCommandOption(CliArgDef.BROKERID);
        addCommandOption(CliArgDef.SUBSCRIBE);
        addCommandOption(CliArgDef.PUBLISH);
        addCommandOption(CliArgDef.AUTHTOKEN);
    }

    @Override
    public boolean processParams(String[] args) throws Exception {
        CommandLine cli = parser.parse(options, args);
        if (cli == null) {
            throw new ParseException("Parse args failure");
        }
        if (cli.hasOption(CliArgDef.VERSION.longOpt)) {
            version();
        }
        if (cli.hasOption(CliArgDef.HELP.longOpt)) {
            help();
        }
        if (cli.hasOption(CliArgDef.ADMINMETHOD.longOpt)) {
            System.out.println(
                    "admin_query_topic_broker_config_info, admin_add_new_topic_record, admin_delete_topic_info, admin_modify_topic_info");
            System.exit(0);
        }
        String masterAddr = cli.getOptionValue(CliArgDef.MASTERPORTAL.longOpt);
        if (TStringUtils.isBlank(masterAddr)) {
            masterAddr = defBrokerPortal;
        }
        String methodStr = cli.getOptionValue(CliArgDef.METHOD.longOpt);
        if (TStringUtils.isBlank(methodStr)) {
            throw new Exception(CliArgDef.METHOD.longOpt + " is required!");
        }
        String topicStr = cli.getOptionValue(CliArgDef.OPETOPIC.longOpt);
        if (!methodStr.equals("admin_query_topic_broker_config_info") && TStringUtils.isBlank(topicStr)) {
            throw new Exception(CliArgDef.OPETOPIC.longOpt + " is required!");
        }
        String brokerId = cli.getOptionValue(CliArgDef.BROKERID.longOpt);
        if (!methodStr.equals("admin_query_topic_broker_config_info") && TStringUtils.isBlank(brokerId)) {
            throw new Exception(CliArgDef.BROKERID.longOpt + " is required!");
        }
        String subscribe = cli.getOptionValue(CliArgDef.SUBSCRIBE.longOpt);
        String publish = cli.getOptionValue(CliArgDef.PUBLISH.longOpt);
        if (methodStr.equals("admin_modify_topic_info")
                && TStringUtils.isBlank(subscribe) && TStringUtils.isBlank(publish)) {
            throw new Exception(CliArgDef.SUBSCRIBE.longOpt + " or " + CliArgDef.PUBLISH.longOpt + " is required");
        }
        String typeStr = "op_modify";
        if (methodStr.equals("admin_query_topic_broker_config_info")) {
            typeStr = "op_query";
        }
        confModAuthToken = cli.getOptionValue(CliArgDef.AUTHTOKEN.longOpt);
        if (typeStr.equals("op_modify") && TStringUtils.isBlank(confModAuthToken)) {
            throw new Exception(CliArgDef.AUTHTOKEN.longOpt + " is required");
        }
        JsonObject result = null;
        Map<String, String> inParamMap = new HashMap<>();
        String masterUrl = "http://" + masterAddr + "/webapi.htm";
        inParamMap.put(CliArgDef.METHOD.longOpt, methodStr);
        inParamMap.put("type", typeStr);
        inParamMap.put(CliArgDef.OPETOPIC.longOpt, topicStr);
        inParamMap.put(CliArgDef.BROKERID.longOpt, brokerId);
        if (methodStr.equals("admin_add_new_topic_record")) {
            inParamMap.put("createUser", createUser);
            inParamMap.put("unflushThreshold", unflushThresold);
            inParamMap.put("acceptPublish", "true");
            inParamMap.put("numPartitions", numPartitions);
            inParamMap.put("deletePolicy", deletePolicy);
            inParamMap.put("unflushInterval", unflushInterval);
            inParamMap.put("acceptSubscribe", "true");
            inParamMap.put("confModAuthToken", confModAuthToken);
        } else if (methodStr.equals("admin_delete_topic_info")) {
            inParamMap.put("modifyUser", createUser);
            inParamMap.put("confModAuthToken", confModAuthToken);
        } else if (methodStr.equals("admin_modify_topic_info")) {
            inParamMap.put("modifyUser", createUser);
            inParamMap.put("acceptPublish", publish);
            inParamMap.put("acceptSubscribe", subscribe);
            inParamMap.put("confModAuthToken", confModAuthToken);
        }
        result = HttpUtils.requestWebService(masterUrl, inParamMap);
        System.out.println(result.toString());
        return true;
    }

    public static void main(String[] args) {
        CliTopicAdmin cliTopicAdmin = new CliTopicAdmin();
        try {
            cliTopicAdmin.processParams(args);
        } catch (Throwable ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage());
            System.out.println(ex.getMessage());
            System.exit(0);
        }
    }
}
