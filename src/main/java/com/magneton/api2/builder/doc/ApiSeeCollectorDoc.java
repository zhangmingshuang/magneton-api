package com.magneton.api2.builder.doc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangmingshuang
 * @since 2019/10/9
 */
public class ApiSeeCollectorDoc extends ApiDoc {

    private List<SeeTag> sees;


    public void seeCollector(Tag[] tags) {
        if (tags == null || tags.length < 1) {
            return;
        }
        for (Tag tag : tags) {
            TagType tagType = TagType.getTag(tag.kind());
            if (tagType != TagType.See) {
                continue;
            }
            final SeeTag seeTag = (SeeTag) tag;
            ClassDoc classDoc = seeTag.referencedClass();
            if (classDoc != null) {
                this.addSee(seeTag);
            }
        }
    }

    public void addSee(SeeTag seeTag) {
        ClassDoc classDoc = seeTag.referencedClass();
        if (classDoc == null) {
            return;
        }
        if (this.sees == null) {
            this.sees = new ArrayList<>();
        }
        this.sees.add(seeTag);
    }


    public void addSees(List<SeeTag> sees) {
        if (sees == null) {
            return;
        }
        if (this.sees == null) {
            this.sees = new ArrayList<>();
        }
        for (SeeTag see : sees) {
            this.addSee(see);
        }
    }

    protected List<SeeTag> getSees() {
        return sees == null ? null : new ArrayList<>(sees);
    }
}
