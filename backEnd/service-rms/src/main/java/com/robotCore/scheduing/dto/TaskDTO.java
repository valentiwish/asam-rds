package com.robotCore.scheduing.dto;

import lombok.Data;

import java.util.List;

/**
 * @Des:
 * @author: leo update
 * @date: 2026/1/15
 **/
@Data
public class TaskDTO {

    private Integer taskId;

    private String desc;

    private List<ActionGroup> actionGroups;

    private boolean checked;

    @Data
    public static class ActionGroup {

        private List<Action> actions;

        private String actionGroupName;

        private Integer actionGroupId;

        private boolean checked;

        @Data
        public static class Action {

            private String actionName;

            private String pluginName;

            private List<Param> params;

            private boolean ignoreReturn;

            private Integer overtime;

            private Integer externalOverId;

            private boolean needResult;

            private Integer sleepTime;

            private Integer actionId;

            @Data
            public static class Param {

                private String key;

                private String stringValue;

                private Integer int32Value;

                private Boolean boolValue;

            }

//            /**
//             * Param 基类：只有 key
//             * 子类分别只包含一种 value 字段，从结构上避免输出多余 null 字段
//             */
//            @Data
//            public static abstract class Param {
//                private String key;
//
//                protected Param() {}
//
//                protected Param(String key) {
//                    this.key = key;
//                }
//            }
//
//            /**
//             * string 参数：序列化后只有 key + stringValue
//             */
//            @Data
//            public static class StringParam extends Param {
//                private String stringValue;
//
//                public StringParam() {}
//
//                public StringParam(String key, String stringValue) {
//                    super(key);
//                    this.stringValue = stringValue;
//                }
//            }
//
//            /**
//             * int32 参数：序列化后只有 key + int32Value
//             */
//            @Data
//            public static class Int32Param extends Param {
//                private Integer int32Value;
//
//                public Int32Param() {}
//
//                public Int32Param(String key, Integer int32Value) {
//                    super(key);
//                    this.int32Value = int32Value;
//                }
//            }
//
//            /**
//             * bool 参数：序列化后只有 key + boolValue
//             */
//            @Data
//            public static class BoolParam extends Param {
//                private Boolean boolValue;
//
//                public BoolParam() {}
//
//                public BoolParam(String key, Boolean boolValue) {
//                    super(key);
//                    this.boolValue = boolValue;
//                }
//            }
        }
    }

}
