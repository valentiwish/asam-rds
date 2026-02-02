<template>
    <div class="fileshow-item" :key="item.id">
        <span class="fileshow-item-text" :title="item.name">
            <i class="fa fa-fw" :class="getIcon(item.type)"></i>
            {{item.name}} 
        </span>
        <Button type="dashed" size="small" icon="ios-cloud-download-outline" style="margin:0px 5px;" :title="'下载 - '+item.name" @click="$utils.openUrl(item.downloadurl,true)" v-if="item.downloadurl">下载</Button>
        <Button type="dashed" size="small"  icon="ios-eye" @click="$utils.openUrl(item.showurl,true)" :title="'预览 - '+item.name" v-if="item.showurl">预览</Button>
        <Button type="dashed" size="small"  icon="ios-eye" @click="remove(item)" :title="'预览 - '+item.name" v-if="item.removeEnable">删除</Button>
    </div>
</template>

<script>
export default {
    name: 'fileshowItem',
    props: ['item'],
    data: function() {
        return {
            icons:{
                ".doc":"fa-file-word-o",
                ".docx":"fa-file-word-o",
                ".xls":"fa-file-excel-o",
                ".xlsx":"fa-file-excel-o",

                ".jpg":"fa-file-photo-o",
                ".png":"fa-file-photo-o",
                ".bmp":"fa-file-photo-o",
                ".pdf":"fa-file-photo-o",

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
    methods:{
        getIcon:function(type){
            type=type.toUpperCase();
            var icon = this.icons[type];
            if(!icon){
                return "fa-file";
            }
            return icon;   
        },
        remove:function(item){
            this.$emit("onRemove",item);
        }
    }
};
</script>

<style>
    .fileshow-item{
        margin:10px 0;
        height:30px;
    }
    .fileshow-item button{
        display: none;
    }
    .fileshow-item i {
        font-size:12px;
        vertical-align: middle;
    }
    .fileshow-item .fileshow-item-text{
        display:inline-block;
        padding:4px 5px;
        border-radius: 3px;
    }
    .fileshow-item:hover .fileshow-item-text{
        background:#ecebeb;
        color:#2d8cf0;
    }
    .fileshow-item:hover button{
        display: inline-block;
    }
</style>


