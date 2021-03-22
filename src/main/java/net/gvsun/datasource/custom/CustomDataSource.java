package net.gvsun.datasource.custom;

import com.mchange.v2.c3p0.AbstractComboPooledDataSource;

public class CustomDataSource extends AbstractComboPooledDataSource {
    private String identifier;//该数据源的唯一标识

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "CustomDataSource{" +
                "identifier='" + identifier + '\'' +
                ", url='" + super.getJdbcUrl() + '\'' +
                ", username='" + super.getUser() + '\'' +
                ", password='" + super.getPassword() + '\'' +
                '}';
    }
}
