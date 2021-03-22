package net.gvsun.datasource.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * 数据源信息
 *
 * @author 陈敬
 * @since 1.2.0-SNAPSHOT
 */
public class GvsunDataSourceDto implements Serializable {
    private String schoolName;
    private String schoolCname;
    private boolean defaultDataSource;
    private String url;
    private String username;
    private String password;
    private String personInCharge;
    private String date;

    public String getPersonInCharge() {
        return personInCharge;
    }

    public void setPersonInCharge(String personInCharge) {
        this.personInCharge = personInCharge;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolCname() {
        return schoolCname;
    }

    public void setSchoolCname(String schoolCname) {
        this.schoolCname = schoolCname;
    }

    public boolean isDefaultDataSource() {
        return defaultDataSource;
    }

    public void setDefaultDataSource(boolean defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GvsunDataSourceDto that = (GvsunDataSourceDto) o;
        return Objects.equals(schoolName, that.schoolName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(schoolName);
    }

    @Override
    public String toString() {
        return "GvsunDataSourceDto{" +
                "schoolName='" + schoolName + '\'' +
                ", schoolCname='" + schoolCname + '\'' +
                ", defaultDataSource=" + defaultDataSource +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", personInCharge='" + personInCharge + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
