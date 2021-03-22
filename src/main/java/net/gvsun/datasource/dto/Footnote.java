package net.gvsun.datasource.dto;


import java.io.Serializable;

public class Footnote implements Serializable {
    private String address;     //联系地址
    private String phone;       //联系电话
    private String aq;          //备案号
    private String aqLink;      //备案号查询地址
    private String copyright;   //版权所有

    public String getAqLink() {
        return aqLink;
    }

    public void setAqLink(String aqLink) {
        this.aqLink = aqLink;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAq() {
        return aq;
    }

    public void setAq(String aq) {
        this.aq = aq;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }
}
