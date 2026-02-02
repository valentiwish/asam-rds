package com.asam.common.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 菜单树形结构各节点对象
 * @Author: zhangqi
 * @Create: 2018-08-17 15:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MenuNode extends BasisNode {
    /**
     * 图片图标
     */
    public String imgUrl;

    /**
     * 图片图标
     */
    public String icon;

    /**
     * 是否附带按钮:1是  0否
     */
    public Integer isOperation = 0;

    /**
     * 状态--是否显示菜单：0不显示，1显示
     */
    public Integer isDisplay;

    /**
     * json描述，用户存放一些json格式的配置信息
     */
    public String jsonCss;

    /**
     * 包含的按钮元素
     */
    public List<String> allow = new ArrayList<String>();


    public MenuNode(String id,  String code,String name, String pid, String url, String imgUrl, String icon, Integer isOperation,Integer isDisplay){
        this.setId(id);
        this.setCode(code);
        this.setName(name);
        this.setPid(pid);
        this.setUrl(url);
        this.setImgUrl(imgUrl);
        this.setIcon(icon);
        this.setIsOperation(isOperation);
        this.setIsDisplay(isDisplay);
    }

    public boolean Equals(MenuNode other)
    {
        if((getId().compareTo(other.getId())) == 0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MenuNode other = (MenuNode) obj;
        if (getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!getId().equals(other.getId())) {
            return false;
        }
        return true;
    }
}
