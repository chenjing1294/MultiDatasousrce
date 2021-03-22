package net.gvsun.datasource.dto;

import java.io.Serializable;

public class CommonConfig implements Serializable {
    private Footnote footnote;

    public Footnote getFootnote() {
        return footnote;
    }

    public void setFootnote(Footnote footnote) {
        this.footnote = footnote;
    }


}