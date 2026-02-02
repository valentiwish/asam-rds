<template>
    <div v-if="list" class="header-menu-content">
        <template v-for="obj in list">
            <template v-if="obj.children && obj.children.length>0">
                <div class="header-menu-item" :key="obj.id" :class="{'active':checkActive(obj)}">
                    <div class="header-menu-main">
                        <div class="header-menu-main_content">
                            <i :class="obj.icon+' fa-fw'"></i>
                            <span>{{obj.name}}</span>
                            <template v-if="obj.name=='请假管理'">
                                <Badge :count="$store.state.auth.myTaskNum"></Badge>
                            </template>
                            <template v-if="obj.name=='工作报告'">
                                <Badge :count="$store.state.auth.reviewNum"></Badge>
                            </template>
                        </div>
                        
                    </div>
                    <div class="header-menu-subcontentwrap">
                        <div class="header-menu-subcontent">
                            <Row type="flex" gutter="30">
                                <Col :span="getSpan(obj.children.length)" :key="item2.id" v-for="item2 in obj.children">
                                    <div class="header-menu-subitem">
                                        <div class="header-menu-subitem_title">
                                            <!-- <i :class="item2.icon+' fa-fw'"></i> -->
                                            <span>{{item2.name}}</span>
                                        </div>
                                        <div class="header-menu-subitem_list">
                                            <template v-if="item2.children && item2.children.length>0"> 
                                                <div class="header-menu-subitem_item" :key="item3.id" :title="item3.name"  v-for="item3 in item2.children" :class="{'active':checkActive(item3)}" @click="$emit('on-select',item3.url)">
                                                    <!-- <i :class="item3.icon+' fa-fw'"></i> -->
                                                    <span>{{item3.name}}</span>
                                                    <template v-if="item3.name=='请假审核'">
                                                        <Badge :count="$store.state.auth.myTaskNum"></Badge>
                                                    </template>
                                                    <template v-if="item3.name=='审阅'">
                                                        <Badge :count="$store.state.auth.reviewNum"></Badge>
                                                    </template>
                                                </div>
                                            </template>  

                                            <template v-else> 
                                                <div class="header-menu-subitem_item" @click="$emit('on-select',item2.url)" :title="item2.name" :class="{'active':checkActive(item2)}">
                                                    <!-- <i :class="item2.icon+' fa-fw'"></i> -->
                                                    <span>{{item2.name}}</span>
                                                    <template v-if="item2.name=='请假审核'">
                                                        <Badge :count="$store.state.auth.myTaskNum"></Badge>
                                                    </template>
                                                    <template v-if="item2.name=='审阅'">
                                                        <Badge :count="$store.state.auth.reviewNum"></Badge>
                                                    </template>
                                                </div>
                                            </template>  
                                            
                                        </div>
                                    </div>
                                </Col>
                            </Row>
                        </div>
                    </div>
                </div>
            </template>
            <div class="header-menu-item" v-else :key="obj.id" @click="$emit('on-select',obj.url)" :title="obj.name" :class="{'active':checkActive(obj)}">
                <i :class="obj.icon+' fa-fw'" slot="icon"></i>
                <span>{{obj.name}}</span>
            </div>
        </template>
    </div>
</template>

<script>

export default {
    name:"headerMenuItem",
    props: {
        list: Array
    },
    data: function () {
        return {};
    },
    computed:{
        
    },
    methods: {
        getSpan(len){
            var span = Math.floor(24/len);
            if(span>4){
                return 4;
            }
            return span;
        },
        deepCheck(obj){
            var url =this.$route.name;
            if(obj.url == url){
                return true;
            }
            else if(obj.children && obj.children.length>0){
                return obj.children.some((j)=>{
                    var ret = this.deepCheck(j);
                    return ret;
                })
            }
            return false;
        },
        checkActive(obj){
            var ret = this.deepCheck(obj)
            return ret;
            
        }
    },
}
</script>

<style scoped>
    .header-menu-content{
        display: inline-block;
        line-height: 59px;
        color:#333;
        font-size:15px;
    }
    .header-menu-item{
        display: inline-block;
        padding:0 10px;
        height:46px;
    }
    .header-menu-item  i{
        font-size:14px;
        margin-right:5px;
        vertical-align: middle;
        margin-top:-3px;
    }
    
    .header-menu-subcontentwrap{
        display: none;
        position: absolute;
        top:65px;
        left:0;
        right:0;
        z-index:12;
        background: #fff;
        padding:10px 30px 50px;
        box-shadow: 0px 2px 4px 0px #d0cccc;
        text-align: left;
        font-weight:normal;
    }
    .header-menu-subcontentwrap .header-menu-subcontent{
        width:95%;
        margin:auto;
        padding-left:255px;
        line-height:2.5;
    }
    .header-menu-item .header-menu-main{
        color:#fff;
        height: 64px;
    }
    .header-menu-item .header-menu-main .header-menu-main_content{
        padding:0 15px;
        border-radius:20px; 
        height: 32px;
        line-height: 32px;
        cursor:pointer;
    }
    .header-menu-item:hover .header-menu-main .header-menu-main_content,
    .header-menu-item.active .header-menu-main .header-menu-main_content{
        background-image: linear-gradient(to bottom,#65FAE9, #746EF5);
    }
    .header-menu-item:hover .header-menu-subcontentwrap{
        display: block;
    }
    .header-menu-subitem{
        margin:10px 20px 10px 0;
    }
    .header-menu-subitem_title{
        font-size:18px;
        padding: 0 10px;
        color:#888;
        border-bottom:1px solid #e6e6e6;
    }
    .header-menu-subitem_item{
        cursor:pointer;
        padding:5px 10px 0;
        font-size:16px;
        cursor:pointer;
    }
    .header-menu-subitem_item:hover{
        text-decoration: underline;
        color:#2D8CF0;
    }
    .header-menu-subitem_item.active{
        color:#2D8CF0;
    }
</style>