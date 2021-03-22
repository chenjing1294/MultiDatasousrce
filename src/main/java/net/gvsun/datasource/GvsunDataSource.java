package net.gvsun.datasource;


import com.mchange.v2.c3p0.AbstractComboPooledDataSource;

import java.util.Objects;

/**
 * 扩展{@link AbstractComboPooledDataSource}，添加学校和项目信息
 *
 * @author 陈敬
 * @since 1.1.0-SNAPSHOT
 */
public class GvsunDataSource extends AbstractComboPooledDataSource {
    private String SchoolName;
    private String schoolCname;
    private boolean defaultDataSource;
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

    public boolean isDefaultDataSource() {
        return defaultDataSource;
    }

    public void setDefaultDataSource(boolean defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }

    public String getSchoolCname() {
        return schoolCname;
    }

    public void setSchoolCname(String schoolCname) {
        this.schoolCname = schoolCname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GvsunDataSource that = (GvsunDataSource) o;
        return Objects.equals(SchoolName, that.SchoolName) &&
                Objects.equals(schoolCname, that.schoolCname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), SchoolName, schoolCname);
    }

    @Override
    public String toString() {
        return "GvsunDataSource{" +
                "SchoolName='" + SchoolName + '\'' +
                ", schoolCname='" + schoolCname + '\'' +
                ", defaultDataSource=" + defaultDataSource +
                ", personInCharge='" + personInCharge + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
