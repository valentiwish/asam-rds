package com.asam.human.entity;

import com.asam.common.base.model.BaseModelL;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 人员信息表
 * @Author: fyy
 * @Create: 2022-04-19
 */

@SuppressWarnings("serial")
@Data
@Table(name = "hum_user")
@AllArgsConstructor // 全参数构造函数
@NoArgsConstructor // 空参数的构造函数
@Accessors(chain = true) // 链表的访问
public class HumUser extends BaseModelL {

	/**
	 * 工号   //   0:无效  1：在职  2：离职
	 */
	@Column(name = "job_number", length = 50)
	private String jobNumber;

	/**
	 * 姓名
	 */
	@Column(name = "user_name", length = 50)
	private String userName;

	/**
	 * 自定义登录名
	 */
	@Column(name = "login_name", length = 50)
	private String loginName;

	/**
	 * 联系电话
	 */
	@Column(name = "user_phone", length = 50)
	private String userPhone;

	/**
	 * 身份证号
	 */
	@Column(name = "card_id", length = 50)
	private String cardId;

	/**
	 * 性别  男 女
	 */
	@Column(name = "sex", length = 50)
	private String sex;

	/**
	 * 部门id
	 */
	@Column(name = "org_id", length = 50)
	private String orgId;

	/**
	 * 部门编码
	 */
	@Column(name = "org_code",length = 50)
	private String orgCode;

	/**
	 * 部门名称
	 */
	@Column(name = "org_name",length = 100)
	private String orgName;

	/**
	 * 出生日期
	 */
	@Column(name = "birthday",type = MySqlTypeConstant.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthday;

	/**
	 * 婚姻状况：0未婚 1已婚 2未知
	 */
	@Column(name = "marry_state",length = 10)
	private String marryState;

	/**
	 * 邮箱
	 */
	@Column(name = "email", length = 50)
	private String email;

	/**
	 * 籍贯
	 */
	@Column(name = "native_place")
	private String nativePlace;

	/**
	 * 家庭住址
	 */
	@Column(name = "account_location")
	private String accountLocation;

	/**
	 * 户口类型
	 */
	@Column(name = "residence_type", length = 10)
	private String residenceType;

	/**
	 * 政治面貌类型
	 */
	@Column(name = "politics_face_name", length = 10)
	private String politicsFaceName;

	/**
	 * 学历类型
	 */
	@Column(name = "education_type", length = 10)
	private String educationType;

	/**
	 * 职称类型
	 */
	@Column(name = "technical_type", length = 10)
	private String technicalType;

	/**
	 * 头像照片
	 */
	@Column(name = "photo", length = 50)
	private String photo;

	/**
	 * 入职时间
	 */
	@Column(name = "entry_date",type = MySqlTypeConstant.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date entryDate;

	/**
	 * 职务  引用数据字典id
	 */
	@Column(name = "user_position")
	private Long userPosition;

	/**
	 * 岗位  引用数据字典id
	 */
	@Column(name = "user_post")
	private Long userPost;

	/**
	 * 工种  引用数据字典id
	 */
	@Column(name = "user_profession")
	private Long userProfession;

	/**
	 * 是否创建账户（1：创建   0：不创建）
	 */
	@Column(name = "is_account", length = 1)
	private Integer isAccount;

	/**
	 * 所属班组ID
	 */
	@Column(name = "team_id", length = 50)
	private String teamId;
	/**
	 * 班组编码
	 */
	@Column(name = "team_code", length = 50)
	private String teamCode;
	/**
	 * 所属班组名称
	 */
	@Column(name = "team_name", length = 50)
	private String teamName;
	/**
	 * 是否班组长 1:是  0：否
	 */
	@Column(name = "monitor_mark", length =11)
	private Integer monitorMark;
	/**
	 * 免登录码
	 */
	@Column(name = "unique_code", length = 100)
	private String uniqueCode;

	/**
	 * 备用电话
	 */
	/*@Column
	@TableField(insertStrategy = FieldStrategy.IGNORED)
	private String telephone;*/

	/**
	 * 政治面貌
	 */
	/*@Column
	@TableField(insertStrategy = FieldStrategy.IGNORED)
	private Long politicsFaceId;*/
}
