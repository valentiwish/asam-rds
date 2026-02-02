<template>
    <div>
        <div ref="picker" style="display:none;">选择文件</div>
        <div>
            <div class="ivu-upload ivu-upload-drag" v-if="types=='drag'" ref="uploaddrag" @click="open_dialog">
                <div style="padding: 10px 0px;">
                    <i class="ivu-icon ivu-icon-ios-cloud-upload" style="font-size: 52px; color: rgb(51, 153, 255);"></i>
                    <p>点击此处或者拖入文件进行上传</p>
                    <div style="font-size:12px;margin-top:5px;color:#4c99e6;">
                        <slot name="text"></slot>
                    </div>
                </div>
            </div>
            <div class="ivu-upload ivu-upload-select" v-else style="display:inline-block;">
                <button type="button" class="ivu-btn" :class="'ivu-btn-'+buttonType" @click="open_dialog">
                    <i class="fa fa-fw fa-upload"></i>
                    <span>{{btnText}}</span>
                </button>
                <slot name="btntext"></slot>
                <div style="font-size:12px;margin-top:10px;color:#555;" v-if="$slots.text"><slot name="text"></slot></div>
            </div>
        </div>
        <!-- / 文件回显-->
        <ul class="ivu-upload-list" v-if="showFileList && fileList.length>0">
            <li class="ivu-upload-list-file ivu-upload-list-file-finish" v-for="obj in fileList" :key="obj.id">
                <span>
                    <i class="fa fa-fw" :class="getIcon(obj)" style="margin:0 5px;color:#696969;"></i>
                    <span>{{obj.name}}</span>
                    <i class="fa-fw fa fa-bolt" v-if="obj.isFastUpload" style="color:#2D8CF0;" title="支持极速上传"></i>
                </span>
                <span @click="makethumb(obj)" v-if="obj.type && obj.type.match('image')" style="font-size:14px;margin-left:5px;display:inline-block;vertical-align:middle;">
                    <Icon type="ios-eye" style="color:#2d8cf0;"/>
                </span>
                <span v-else-if="obj.isServerFile" style="margin-left:10px;" @click="showServerFile(obj)" title="预览文件">
                    <Icon type="ios-eye" style="color:#2d8cf0;"/>
                </span>

                <a :href="getDownloadUrl(obj)" target="_blank" v-if="obj.isServerFile" style="margin-left:10px;" title="下载文件">
                    <span><Icon type="md-download"/></span>
                </a>
                <i class="ivu-icon ivu-icon-ios-close ivu-upload-list-remove" @click="deleteFile(obj.id)"></i>

                <Progress :percent="obj.percentage*100" :stroke-width="5">
                    <template v-if="obj.percentage>=1">
                        <Icon type="checkmark-circled"></Icon>
                        <span>成功</span>
                    </template>
                    <template v-else-if="obj.percentage==0">
                        <Icon type="ios-cloud-upload-outline"></Icon>
                        <span>待上传</span>
                    </template>
                    <template v-else>
                        <Icon type="ios-loop"></Icon>
                        <span>上传中</span>
                    </template>
                </Progress>
            </li>
        </ul>
        <!-- 查看图片-->
        <Modal v-model="modal_view" :title="modaldata.title" width="70%" :draggable="false">
            <div style="text-align:center;">
                <iframe :src="modaldata.src" frameborder="0" v-if="modaldata.src" style="width:100%;height:600px;"></iframe>
                <i class="fa fa-circle-o-notch fa-spin" aria-hidden="true" v-else style="color:#ccc;font-size:50px;"></i>
            </div>
            <div slot="footer">
            </div>
        </Modal>
    </div>
</template>

<script>
import CONFIG from "@/config/index"
import UTILS from "@/libs/util.js"

var vueInstance =null,curmd5 ="",skipSuccessData = {};

WebUploader.Uploader.register({
    "before-send-file": "beforeSendFile"
}, {
    //时间点1：所有分块进行上传之前调用此函数
    beforeSendFile: function(file,a,b) {
        var deferred = WebUploader.Deferred();
        if(vueInstance.isNeedCheck){
            //获取md5值
            (new WebUploader.Uploader()).md5File(file, 0, 10 * 1024 * 1024)
                .then(function(val) {
                    if(vueInstance){
                        vueInstance.checkFile({
                            MD5:val,
                            userName:vueInstance.userInfo.phone,
                            filename:file.name,
                            moudleName:vueInstance.uploader.options.formData.moudleName,
                            size:file.size,
                        },function(flag,data){
                            if(flag){
                                skipSuccessData = data;
                                vueInstance.uploader.skipFile(file);
                            }
                            else{
                                curmd5 = val;
                            }
                            deferred.resolve();
                        })
                    }
                    else{
                        deferred.resolve();
                    }
                });
        }
        else{
            setTimeout(()=>{
                deferred.resolve();
            },0);
        }
        return deferred.promise();
    }
});

export default {
    name: 'webuploader',
    props: ['option',"webUploaderId"],  //id用于标识当前组件
    data: function() {
        return {
            picker: null,
            uploader: null,
            btnText:"上传文件",
            fileList: [],
            modal_view: false,
            modaldata: {
                src: null,
                title: ""
            },
            isError: false,
            userInfo:{},
            icons:{
                ".doc":"fa-file-word-o",
                ".docx":"fa-file-word-o",
                ".xls":"fa-file-excel-o",
                ".xlsx":"fa-file-excel-o",

                "image":"fa-file-photo-o",
                ".jpg":"fa-file-photo-o",
                ".png":"fa-file-photo-o",
                ".bmp":"fa-picture-o",
                ".pdf":"fa-file-pdf-o",

                ".zip":"fa-file-archive-o",
                ".rar":"fa-file-archive-o",

                ".rar":"fa-file-archive-o",

                ".mp4":"fa-file-movie-o",
                ".rmvb":"fa-file-movie-o",

                ".mp3":"fa-file-sound-o",
                ".wav":"fa-file-sound-o",
                ".amr":"fa-file-sound-o"
            }
        }
    },
    computed: {
        types: function() {
            //return this.option.type || 'select';
            return this.option.type || 'drag';
        },
        deleteDefaultFile: function() {
            return this.option.deleteDefaultFile || false;
        },
        showFileList: function() {
            if (this.option.showFileList == void 0) {
                return true;
            } else {
                return this.option.showFileList;
            }
        },
        isNeedCheck:function(){
            return this.option.server === void 0 || this.option.server === "/service_file/upload" || this.option.server === "/service_file/zip";
        },
        buttonType:function(){
            return this.option.buttonType || 'primary';
        }
    },
    "methods": {
        getIcon:function(obj){
            var type=obj.type.toLowerCase();
            var icon = this.icons[type];
            var ret = "fa-file";
            if(!icon){
                if(obj.name){
                    var namearr = obj.name.split(".");
                    var suffer = namearr[namearr.length-1];
                    if(suffer){
                        var icon2 = this.icons["."+suffer];
                        if(icon2){
                            ret = icon2;
                        }
                    }
                }
            }
            else{
               ret =  icon;
            }
            return ret;
        },
        getDownloadUrl:function(obj){
            return UTILS.getFileDownloadUrl(obj.id);
        },
        showServerFile:function(obj){
            var url = UTILS.getFileShowUrl(obj.id);
            if((obj.type && obj.type.match("image")) || (obj.name && obj.name.match(".pdf"))){
                this.modal_view = true;
                this.modaldata.title = obj.name;
                this.modaldata.src = url;
            }
            else{
                var that = this;
                this.$Modal.confirm({
                    title: '温馨提醒',
                    content: obj.name+'<br/>不支持在线预览，是否下载查看？',
                    onOk() {
                        window.open(url);
                    }
                })
            }
        },
        //打开文件上传窗口
        open_dialog: function() {
            $(this.picker).find("input").click();
        },
        //回显默认文件
        addDefaultFile: function(arr) {
            arr.map((j, i) => {
                j.isServerFile = true;
                j.percentage = 1;
                this.fileList.push(j);
            })
        },
        //上传队列中的文件
        upload: function() {
            if (this.isError) {
                this.retry();
            } else {
                this.uploader.upload();
            }
        },
        //重新上传队列中的文件
        retry: function(file) {
            this.uploader.retry(file);
        },
        //根据id获取文件
        getFile: function(fileid) {
            return this.uploader.getFile(fileid);
        },
        getFiles: function() {
            return this.uploader.getFiles();
        },
        _initWebuploader: function() {
            this.uploader = WebUploader.create(Object.assign({
                // swf文件路径
                swf: '@/assets/js/webuploader/Uploader.swf',
                auto: false,
                server: "/service_file/upload",
                resize: false,
                pick: this.picker,
                dnd:(this.types =='drag' ? this.$refs.uploaddrag : undefined),
                disableGlobalDnd:true,
                accept: null,
                duplicate: true,
                fileSingleSizeLimit: 10 * 1024 * 1024
            }, this.option));

            if(this.option.btnText){
                this.btnText = this.option.btnText
            }
        },
        //删除某个文件
        deleteFile: function(fileId) {
            this.fileList.some((j, i) => {
                if (j.id == fileId) {
                    this.$emit('deleteFile', {
                        file:j,
                        webUploaderId:this.webUploaderId
                    });
                    this.fileList.splice(i, 1);
                    if(!j.isServerFile){
                        //删除队列
                        this.uploader.removeFile(fileId, true);
                    }
                    return true;
                } else {
                    return false;
                }
            })
        },
        makethumb: function(file) {
            this.modal_view = true;
            this.modaldata.title = file.name;
            if (file.isServerFile) {
                this.modaldata.src = file.src;
            } else {
                this.modaldata.src = null;
                this.uploader.makeThumb(file, (error, src) => {
                    this.modaldata.src = src;
                }, 1, 1);
            }
        },
        //判断文件是否重复
        checkFile:function(obj,callback){
            this.$ajax.post("/service_file/getFileByMD5",obj)
                .then((res)=>{
                    if(res.data.code === 200){
                        callback(true,res.data)
                    }
                    else{
                         callback(false)
                    }
                },()=>{
                    callback(false)
                })
        },
        //快速判断文件是否被上传 标记支持极速上传按钮
        checkFileFast:function(obj,callback){
            this.$ajax.post("/service_file/getFileByMD5NoSave",obj)
                .then((res)=>{
                    if(res.data.code === 200){
                        callback(true)
                    }
                    else{
                         callback(false)
                    }
                },()=>{
                    callback(false)
                })
        },
        _addEvent: function() {
            var webUploaderId = this.webUploaderId;
            // uploader在初始化的时候
            this.uploader.on("ready", () => {
                this.$emit('ready',{webUploaderId});
            })
            //当文件被加入队列之前触发，此事件的handler返回值为false，则此文件不会被添加进入队列
            this.uploader.on('beforeFileQueued', (file) => {
                if(this.fileList.length >= this.uploader.options.fileNumLimit){
                    this.$Message.warning('文件数量超出不能超过'+this.uploader.options.fileNumLimit+"个");
                    return false;
                }
                else{
                    return true;
                }
            });

            this.uploader.on('beforeFileQueued', (file) => {
                if (typeof this.option.beforeFileQueued == "function") {
                    return this.option.beforeFileQueued({file,webUploaderId})
                }
            });
            // 当有文件被添加进队列的时候
            this.uploader.on("fileQueued", (file) => {
                file["percentage"] = 0;
                this.fileList.push(file);
                this.$emit('fileQueued',{file,webUploaderId});
                if(this.isNeedCheck){
                    //计算文件 md5 值
                    this.uploader.md5File(file)
                        .then((val) => {
                            this.fileList.some((j, i) => {
                                if (j.id == file.id) {
                                    j.md5 = val;
                                    this.checkFileFast({MD5:val},(flag)=>{
                                        j.isFastUpload = flag;
                                        this.fileList.splice(i, 1, j);
                                    })
                                    return true;
                                } else {
                                    return false;
                                }
                            })
                        });
                }

            })
            //上传开始时触发
            this.uploader.on('uploadStart', (file) => {
                this.$emit('uploadStart', {file,webUploaderId});
            });

             //上传开始时触发
            this.uploader.on('reset', () => {
                this.fileList = [];
                this.$emit('reset', {webUploaderId});
            });


            //当某个文件上传到服务端响应后，会派送此事件来询问服务端响应是否有效。
            this.uploader.on('uploadAccept', (object, response) => {
                if (typeof this.option.uploadAccept == "function") {
                    return this.option.uploadAccept({object, response,webUploaderId});
                }
                else{
                    if (response.code==200) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            });
            //上传过程中触发
            this.uploader.on('uploadProgress', (file, percentage) => {
                this.fileList.some((j, i) => {
                    if (j.id == file.id) {
                        j.percentage = percentage;
                        this.fileList.splice(i, 1, j);
                        return true;
                    } else {
                        return false;
                    }
                })
                this.$emit('uploadProgress', { file, percentage,webUploaderId });
            });
            //上传成功触发
            this.uploader.on('uploadSuccess', (file, response) => {
                //skipSuccessData
                this.fileList.some((j, i) => {
                    if (j.id == file.id) {
                        j.percentage = 1;
                        this.fileList.splice(i, 1, j);
                        return true;
                    } else {
                        return false;
                    }
                });
                if(!response){
                    response = skipSuccessData;
                }
                this.$emit('uploadSuccess', { file, response,webUploaderId });
            })
            //上传出错中触发
            this.uploader.on('uploadError', (file, reason) => {
                this.$emit('uploadError', { file, reason,webUploaderId });
                this.fileList.filter((j, i) => {
                    if (j.id == file.id) {
                        j.percentage = 0;
                        this.fileList.splice(i, 1, j);
                        return true;
                    } else {
                        return false;
                    }
                })
            })

            this.uploader.on("uploadBeforeSend", (object, data, headers) => {
                data.userName = this.userInfo.phone;
                data.MD5 = curmd5;
                headers['access-token'] = localStorage.access_token;
                this.$emit('uploadBeforeSend', { object, data, headers,webUploaderId });
            });

            //当所有文件上传结束时触发。
            this.uploader.on("uploadFinished", (a, b) => {
                this.isError = this.uploader.getFiles('error').length == 0;
                this.$emit('uploadFinished', {
                    error:!this.isError,
                    fileCount:this.uploader.getFiles().length,
                    webUploaderId:webUploaderId
                });
            });
            //当开始上传流程时触发。
            this.uploader.on("startUpload", () => {
                this.$emit('startUpload',{webUploaderId});
            });
            //当开始上传流程暂停时触发。
            this.uploader.on("stopUpload", () => {
                this.$emit('stopUpload',{webUploaderId});
            });
            //当开始上传流程出错。
            this.uploader.on("error", (type) => {
                switch (type) {
                    case "F_EXCEED_SIZE":
                        this.$Message.warning('请选择小于'+(this.uploader.options.fileSingleSizeLimit/1024/1024)+"Mb的文件");
                        break;
                    case "Q_EXCEED_NUM_LIMIT":
                        this.$Message.warning('文件数量超出不能超过'+this.uploader.options.fileNumLimit+"个");
                        break;
                    case "Q_TYPE_DENIED":
                        this.$Message.warning('请选择'+this.uploader.options.accept[0].extensions+"文件");
                        break;
                }
                this.$emit('error', {type,webUploaderId});
            });
        }
    },
    mounted: function() {
        //解决一个页面同时不能初始化多个实例的问题
        var time = Math.random() * 1000;
        setTimeout(()=>{
            this.picker = this.$refs.picker;
            this._initWebuploader();
            this._addEvent();
        },time)
    },
    beforeDestroy: function() {
        if(this.uploader){
            this.uploader.destroy();
        }

    },
    created:function(){
        vueInstance = this;
        //获取用户信息
        try{
            this.userInfo = JSON.parse(localStorage.userInfo);
        }
        catch(e){
        }
    }
};
</script>

<style>
    .webuploader-container {
        position: relative;
    }
    .webuploader-element-invisible {
        position: absolute !important;
        clip: rect(1px 1px 1px 1px); /* IE6, IE7 */
        clip: rect(1px,1px,1px,1px);
    }
    .webuploader-pick {
        position: relative;
        display: inline-block;
        cursor: pointer;
        background: #00b7ee;
        padding: 10px 15px;
        color: #fff;
        text-align: center;
        border-radius: 3px;
        overflow: hidden;
    }
    .webuploader-pick-hover {
        background: #00a2d4;
    }

    .webuploader-pick-disable {
        opacity: 0.6;
        pointer-events:none;
    }
</style>
