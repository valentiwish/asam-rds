package com.robotCore.scheduing.common.constant;

/**
 * @Des:
 * @author: zxl
 * @date: 2023/6/6
 **/
public class RobotTaskConstant {

    public static final String ROBOT_TASK = "{\n" +
            "    \"id\": \"c80455db-938a-41ca-98cc-0b46146d20a0\",\n" +
            "    \"version\": 20,\n" +
            "    \"label\": \"test_robot\",\n" +
            "    \"remark\": \"\",\n" +
            "    \"templateDescription\": \"@{task.enum.UserTemplate}\",\n" +
            "    \"templateName\": \"userTemplate\",\n" +
            "    \"inputParams\": [],\n" +
            "    \"outputParams\": [],\n" +
            "    \"rootBlock\": {\n" +
            "        \"id\": -1,\n" +
            "        \"name\": \"-1\",\n" +
            "        \"blockType\": \"RootBp\",\n" +
            "        \"inputParams\": {},\n" +
            "        \"children\": {\n" +
            "            \"submodules\": [\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"b1\",\n" +
            "                    \"blockType\": \"CSelectAgvBp\",\n" +
            "                    \"children\": {\n" +
            "                        \"submodules\": [\n" +
            "                            {\n" +
            "                                \"id\": 2,\n" +
            "                                \"name\": \"b2\",\n" +
            "                                \"blockType\": \"CAgvOperationBp\",\n" +
            "                                \"children\": {},\n" +
            "                                \"inputParams\": {\n" +
            "                                    \"agvId\": {\n" +
            "                                        \"type\": \"Expression\",\n" +
            "                                        \"value\": \"blocks.b1.selectedAgvId\"\n" +
            "                                    },\n" +
            "                                    \"targetSiteLabel\": {\n" +
            "                                        \"type\": \"Simple\",\n" +
            "                                        \"value\": \"AP45\"\n" +
            "                                    },\n" +
            "                                    \"scriptName\": {\n" +
            "                                        \"type\": \"Simple\",\n" +
            "                                        \"value\": \"JackLoad\"\n" +
            "                                    },\n" +
            "                                    \"var_jack_height\": {\n" +
            "                                        \"type\": \"Simple\",\n" +
            "                                        \"value\": \"\"\n" +
            "                                    }\n" +
            "                                },\n" +
            "                                \"refTaskDefId\": \"\",\n" +
            "                                \"selected\": false,\n" +
            "                                \"expanded\": true\n" +
            "                            },\n" +
            "                            {\n" +
            "                                \"id\": 3,\n" +
            "                                \"name\": \"b3\",\n" +
            "                                \"blockType\": \"CAgvOperationBp\",\n" +
            "                                \"children\": {},\n" +
            "                                \"inputParams\": {\n" +
            "                                    \"agvId\": {\n" +
            "                                        \"type\": \"Expression\",\n" +
            "                                        \"value\": \"blocks.b1.selectedAgvId\"\n" +
            "                                    },\n" +
            "                                    \"targetSiteLabel\": {\n" +
            "                                        \"type\": \"Simple\",\n" +
            "                                        \"value\": \"AP47\"\n" +
            "                                    },\n" +
            "                                    \"scriptName\": {\n" +
            "                                        \"type\": \"Simple\",\n" +
            "                                        \"value\": \"JackUnload\"\n" +
            "                                    }\n" +
            "                                },\n" +
            "                                \"refTaskDefId\": \"\",\n" +
            "                                \"selected\": false,\n" +
            "                                \"expanded\": true\n" +
            "                            }\n" +
            "                        ]\n" +
            "                    },\n" +
            "                    \"inputParams\": {\n" +
            "                        \"group\": {\n" +
            "                            \"type\": \"Simple\",\n" +
            "                            \"value\": \"T1\"\n" +
            "                        },\n" +
            "                        \"keyRoute\": {\n" +
            "                            \"type\": \"Simple\",\n" +
            "                            \"value\": \"AP45\"\n" +
            "                        }\n" +
            "                    },\n" +
            "                    \"refTaskDefId\": \"\",\n" +
            "                    \"selected\": false,\n" +
            "                    \"expanded\": true\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        \"selected\": false,\n" +
            "        \"refTaskDefId\": \"\",\n" +
            "        \"expanded\": true\n" +
            "    },\n" +
            "    \"periodicTask\": 0,\n" +
            "    \"ifEnable\": 0,\n" +
            "    \"period\": 3000,\n" +
            "    \"delay\": 1000,\n" +
            "    \"windcategoryId\": 0\n" +
            "}";

    public static final String ROBOT_JACK_TASK = "{\n" +
            "    \"id\": \"c80455db-938a-41ca-98cc-0b46146d20a0\",\n" +
            "    \"version\": 17,\n" +
            "    \"label\": \"test_robot\",\n" +
            "    \"remark\": \"\",\n" +
            "    \"templateDescription\": \"@{task.enum.UserTemplate}\",\n" +
            "    \"templateName\": \"userTemplate\",\n" +
            "    \"inputParams\": [],\n" +
            "    \"outputParams\": [],\n" +
            "    \"rootBlock\": {\n" +
            "        \"id\": -1,\n" +
            "        \"name\": \"-1\",\n" +
            "        \"blockType\": \"RootBp\",\n" +
            "        \"inputParams\": {},\n" +
            "        \"children\": {\n" +
            "            \"submodules\": [\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"b1\",\n" +
            "                    \"blockType\": \"CSelectAgvBp\",\n" +
            "                    \"children\": {\n" +
            "                        \"submodules\": [\n" +
            "                            {\n" +
            "                                \"id\": 2,\n" +
            "                                \"name\": \"b2\",\n" +
            "                                \"blockType\": \"CAgvOperationBp\",\n" +
            "                                \"children\": {},\n" +
            "                                \"inputParams\": {\n" +
            "                                    \"agvId\": {\n" +
            "                                        \"type\": \"Expression\",\n" +
            "                                        \"value\": \"blocks.b1.selectedAgvId\"\n" +
            "                                    },\n" +
            "                                    \"targetSiteLabel\": {\n" +
            "                                        \"type\": \"Simple\",\n" +
            "                                        \"value\": \"AP45\"\n" +
            "                                    },\n" +
            "                                    \"scriptName\": {\n" +
            "                                        \"type\": \"Simple\",\n" +
            "                                        \"value\": \"JackLoad\"\n" +
            "                                    },\n" +
            "                                    \"var_jack_height\": {\n" +
            "                                        \"type\": \"Simple\",\n" +
            "                                        \"value\": \"\"\n" +
            "                                    }\n" +
            "                                },\n" +
            "                                \"refTaskDefId\": \"\",\n" +
            "                                \"selected\": false,\n" +
            "                                \"expanded\": true\n" +
            "                            },\n" +
            "                            {\n" +
            "                                \"id\": 3,\n" +
            "                                \"name\": \"b3\",\n" +
            "                                \"blockType\": \"CAgvOperationBp\",\n" +
            "                                \"children\": {},\n" +
            "                                \"inputParams\": {\n" +
            "                                    \"agvId\": {\n" +
            "                                        \"type\": \"Expression\",\n" +
            "                                        \"value\": \"blocks.b1.selectedAgvId\"\n" +
            "                                    },\n" +
            "                                    \"targetSiteLabel\": {\n" +
            "                                        \"type\": \"Simple\",\n" +
            "                                        \"value\": \"AP47\"\n" +
            "                                    },\n" +
            "                                    \"scriptName\": {\n" +
            "                                        \"type\": \"Simple\",\n" +
            "                                        \"value\": \"JackUnload\"\n" +
            "                                    }\n" +
            "                                },\n" +
            "                                \"refTaskDefId\": \"\",\n" +
            "                                \"selected\": false,\n" +
            "                                \"expanded\": true\n" +
            "                            }\n" +
            "                        ]\n" +
            "                    },\n" +
            "                    \"inputParams\": {\n" +
            "                        \"group\": {\n" +
            "                            \"type\": \"Simple\",\n" +
            "                            \"value\": \"T1\"\n" +
            "                        },\n" +
            "                        \"keyRoute\": {\n" +
            "                            \"type\": \"Simple\",\n" +
            "                            \"value\": \"AP45\"\n" +
            "                        }\n" +
            "                    },\n" +
            "                    \"refTaskDefId\": \"\",\n" +
            "                    \"selected\": false,\n" +
            "                    \"expanded\": true\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        \"selected\": false,\n" +
            "        \"refTaskDefId\": \"\",\n" +
            "        \"expanded\": true\n" +
            "    },\n" +
            "    \"periodicTask\": 0,\n" +
            "    \"ifEnable\": 0,\n" +
            "    \"period\": 3000,\n" +
            "    \"delay\": 1000,\n" +
            "    \"windcategoryId\": 0\n" +
            "}";

}
