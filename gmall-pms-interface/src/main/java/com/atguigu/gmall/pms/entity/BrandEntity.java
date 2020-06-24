package com.atguigu.gmall.pms.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 品牌
 *
 * @author chucai
 * @email 1561763825@qq.com
 * @date 2020-06-24 13:55:38
 */
@ApiModel
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

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
	/**
	 *
	 */
	@ApiModelProperty(name = "createTime",value = "")
	@TableField(value = "create_time",fill=FieldFill.INSERT)
	private LocalDateTime createTime;
	/**
	 *
	 */
	@ApiModelProperty(name = "updateTime",value = "")
	@TableField(value = "update_time",fill=FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;
	/**
	 * 版本号
	 */
	@ApiModelProperty(name = "version",value = "版本号")
	@Version
	private Integer version;
	/**
	 * 是否删除
	 */
	@ApiModelProperty(name = "deleted",value = "是否删除")
	@TableLogic
	private Integer deleted;
	/**
	 * 创建人id
	 */
	@ApiModelProperty(name = "createUser",value = "创建人id")
	private Long createUser;
	/**
	 * 更新人id
	 */
	@ApiModelProperty(name = "updateUser",value = "更新人id")
	private Long updateUser;
	/**
	 * 创建人名称
	 */
	@ApiModelProperty(name = "createUserName",value = "创建人名称")
	private String createUserName;
	/**
	 * 更新人名称
	 */
	@ApiModelProperty(name = "updateUserName",value = "更新人名称")
	private String updateUserName;

}
