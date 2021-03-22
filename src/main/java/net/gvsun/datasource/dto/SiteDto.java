package net.gvsun.datasource.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 站点信息
 *
 * @author 陈敬
 * @since 1.2.0-SNAPSHOT
 */
public class SiteDto implements Serializable {
    private String siteUrl;
    private String projectName;
    private List<GvsunDataSourceDto> dataSourceDtos;

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<GvsunDataSourceDto> getDataSourceDtos() {
        return dataSourceDtos;
    }

    public void setDataSourceDtos(List<GvsunDataSourceDto> dataSourceDtos) {
        this.dataSourceDtos = dataSourceDtos;
    }

    @Override
    public String toString() {
        return "SiteDto{" +
                "siteUrl='" + siteUrl + '\'' +
                ", projectName='" + projectName + '\'' +
                ", dataSourceDtos=" + dataSourceDtos +
                '}';
    }
}
