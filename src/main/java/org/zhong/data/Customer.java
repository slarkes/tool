package org.zhong.data;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@ColumnWidth(20)
public class Customer {

    @ColumnWidth(10)
    @ExcelProperty("序号")
    private int no;

    @ExcelProperty("微信粉丝ID")
    private long wechat_fan_id;

    @DateTimeFormat("yyyy/MM/dd HH:mm")
    @ExcelProperty("成为会员时间")
    private Date create_time;

    @DateTimeFormat("yyyy/MM/dd HH:mm")
    @ExcelProperty("上次消费时间")
    private Date last_pay_time;

    @ColumnWidth(10)
    @ExcelProperty("性别")
    private String sex;

    @ColumnWidth(10)
    @ExcelProperty("是否会员")
    private String is_member;

    @DateTimeFormat("yyyy/MM/dd HH:mm")
    @ExcelProperty("最后下单时间")
    private Date last_order_time;

    @ColumnWidth(10)
    @NumberFormat("#.##")
    @ExcelProperty("客单价")
    private Double pct; //per customer transaction

    @ColumnWidth(10)
    @NumberFormat("#.##")
    @ExcelProperty("累计消费金额")
    private Double total_success_order_price;

    @ColumnWidth(10)
    @ExcelProperty("累计消费订单数")
    private long total_success_order_num;

    @ColumnWidth(10)
    @NumberFormat("#.##")
    @ExcelProperty("累计退款金额")
    private Double total_refund_order_price;

    @ColumnWidth(10)
    @ExcelProperty("累计退款订单数")
    private int total_refund_order_num;

    @ColumnWidth(10)
    @ExcelProperty("客户积分")
    private long customer_point;

    @ExcelProperty("客户姓名")
    private String customer_name;

    @ExcelProperty("手机号码")
    private String mobile;

    @ExcelProperty("省份")
    private String province;

    @ExcelProperty("地级市")
    private String city;

    @ExcelProperty("县级市、区")
    private String district;

    @ColumnWidth(80)
    @ExcelProperty("详细地址")
    private String address;

    @ExcelProperty("生日")
    private String  birthday;

    @ExcelProperty("客户标签")
    private String customer_tag;

    @ExcelProperty("权益卡")
    private String customer_power;

    @ColumnWidth(10)
    @NumberFormat("#.##")
    @ExcelProperty("储值余额")
    private Double customer_balance;


    public Customer(int no) {
        this.no = no;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public long getWechat_fan_id() {
        return wechat_fan_id;
    }

    public void setWechat_fan_id(Long wechat_fan_id) {
        this.wechat_fan_id = null == wechat_fan_id?0:wechat_fan_id;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = new Date(create_time*1000);
    }

    public Date getLast_pay_time() {
        return last_pay_time;
    }

    public void setLast_pay_time(Date last_pay_time) {
        this.last_pay_time = last_pay_time;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(byte sex) {
        if (1 == sex) {
            this.sex ="男";
        }
        if (2 == sex) {
            this.sex ="女";
        }
        this.sex = "未知";
    }

    public String getIs_member() {
        return is_member;
    }

    public void setIs_member(int is_member) {
        this.is_member = is_member==1?"是":"否";
    }

    public Date getLast_order_time() {
        return last_order_time;
    }

    public void setLast_order_time(Date last_order_time) {
        this.last_order_time = last_order_time;
    }

    public Double getPct() {
        return pct;
    }

    public void setPct(Double pct) {
        this.pct = null == pct?0:pct;
    }

    public Double getTotal_success_order_price() {
        return total_success_order_price;
    }

    public void setTotal_success_order_price(Double total_success_order_price) {
        this.total_success_order_price = null == total_success_order_price?0:total_success_order_price;
    }

    public long getTotal_success_order_num() {
        return total_success_order_num;
    }

    public void setTotal_success_order_num(Long total_success_order_num) {
        this.total_success_order_num = null == total_success_order_num?0:total_success_order_num;
    }

    public Double getTotal_refund_order_price() {
        return total_refund_order_price;
    }

    public void setTotal_refund_order_price(Double total_refund_order_price) {
        this.total_refund_order_price = null  == total_refund_order_price?0:total_refund_order_price;
    }

    public int getTotal_refund_order_num() {
        return total_refund_order_num;
    }

    public void setTotal_refund_order_num(Integer total_refund_order_num) {
        this.total_refund_order_num = null == total_refund_order_num?0:total_refund_order_num;
    }

    public long getCustomer_point() {
        return customer_point;
    }

    public void setCustomer_point(Long customer_point) {
        this.customer_point = null == customer_point?0:customer_point;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCustomer_tag() {
        return customer_tag;
    }

    public void setCustomer_tag(String customer_tag) {
        this.customer_tag = customer_tag;
    }

    public String getCustomer_power() {
        return customer_power;
    }

    public void setCustomer_power(String customer_power) {
        this.customer_power = customer_power;
    }

    public Double getCustomer_balance() {
        return customer_balance;
    }

    public void setCustomer_balance(Double customer_balance) {
        this.customer_balance = customer_balance;
    }
}
