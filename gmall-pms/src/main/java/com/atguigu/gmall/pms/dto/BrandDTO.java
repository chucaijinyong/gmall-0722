package com.atguigu.gmall.pms.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author chubaodong
 * @version v1.0.0
 * @Package : com.atguigu.gmall.pms.dto
 * @Description : TODO
 * @Create on : 2020/7/21 09:19
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class BrandDTO {
    /**
     * 品牌id
     */
    @TableId
    @ApiModelProperty(name = "brandId",value = "品牌id")
    private Long brandId;
    /**
     * 品牌名
     */
    @ApiModelProperty(name = "name",value = "品牌名")
    private String name;
    /**
     * 品牌logo地址
     */
    @ApiModelProperty(name = "logo",value = "品牌logo地址")
    private String logo;
    /**
     * 介绍
     */
    @ApiModelProperty(name = "descript",value = "介绍")
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     */
    @ApiModelProperty(name = "showStatus",value = "显示状态[0-不显示；1-显示]")
    private Integer showStatus;
    /**
     * 检索首字母
     */
    @ApiModelProperty(name = "firstLetter",value = "检索首字母")
    private String firstLetter;
    /**
     * 排序
     */
    @ApiModelProperty(name = "sort",value = "排序")
    private Integer sort;
//    /**
//     *
//     */
//    @ApiModelProperty(name = "createTime",value = "")
//    @TableField(value = "create_time",fill= FieldFill.INSERT,exist = false)
//    private LocalDateTime createTime;
//    /**
//     *
//     */
//    @ApiModelProperty(name = "updateTime",value = "")
//    @TableField(value = "update_time",fill=FieldFill.INSERT_UPDATE,exist = false)
//    private LocalDateTime updateTime;
//    /**
//     * 版本号
//     */
//    @ApiModelProperty(name = "version",value = "版本号")
//    @Version
//    @TableField(exist = false)
//    private Integer version;
    /**
     * 是否删除
     */
    @ApiModelProperty(name = "deleted",value = "是否删除")
    @TableLogic
    @TableField(exist = false)
    private Integer deleted;
    /**
     * 创建人id
     */
    @ApiModelProperty(name = "createUser",value = "创建人id")
    @TableField(exist = false)
    private Long createUser;
    /**
     * 更新人id
     */
    @ApiModelProperty(name = "updateUser",value = "更新人id")
    @TableField(exist = false)
    private Long updateUser;
    /**
     * 创建人名称
     */
    @ApiModelProperty(name = "createUserName",value = "创建人名称")
    @TableField(exist = false)
    private String createUserName;
    /**
     * 更新人名称
     */
    @ApiModelProperty(name = "updateUserName",value = "更新人名称")
    @TableField(exist = false)
    private String updateUserName;

    /**
     * spu名称
     */
    private String spuName;

}
