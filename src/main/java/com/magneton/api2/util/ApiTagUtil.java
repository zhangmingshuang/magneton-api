package com.magneton.api2.util;

import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangmingshuang
 * @since 2019/8/20
 */
public class ApiTagUtil {

    public static boolean isSee(Tag tag) {
        return tag.kind().equalsIgnoreCase("@see");
    }

    public static boolean isAuthor(Tag tag) {
        return tag.kind().equalsIgnoreCase("@author");
    }

    public static boolean isSince(Tag tag) {
        return tag.kind().equalsIgnoreCase("@since");
    }

    public static boolean isReturn(Tag tag) {
        return tag.kind().equalsIgnoreCase("@return");
    }

    public static boolean isParam(Tag tag) {
        return tag.kind().equalsIgnoreCase("@param");
    }


//
//
//    public static boolean isIncludeSee(Tag[] tags) {
//        if (ArrayUtils.isEmpty(tags)) {
//            return false;
//        }
//        for (Tag tag : tags) {
//            if (tag.kind().equals("@see")) {
//                return true;
//            }
//        }
//        return false;
//    }
//    public static Tag[] getReturnTags(Tag[] tags) {
//        if (ArrayUtils.isEmpty(tags)) {
//            return null;
//        }
//        List list = null;
//        for (Tag tag : tags) {
//            if (tag.kind().equals("@return")) {
//                if (list == null) {
//                    list = new ArrayList(1);
//                }
//                list.add(tag);
//            }
//        }
//        if (list == null) {
//            return null;
//        }
//        Tag[] returnTags = new Tag[list.size()];
//        list.toArray(returnTags);
//        return returnTags;
//    }
//    public static SeeTag[] getSeeTags(Tag[] tags) {
//        if (ArrayUtils.isEmpty(tags)) {
//            return null;
//        }
//        List list = null;
//        for (Tag tag : tags) {
//            if (tag.kind().equals("@see")) {
//                if (list == null) {
//                    list = new ArrayList(1);
//                }
//                list.add(tag);
//            }
//        }
//        if (list == null) {
//            return null;
//        }
//        SeeTag[] seeTags = new SeeTag[list.size()];
//        list.toArray(seeTags);
//        return seeTags;
//    }
//
//    public static boolean isJustText(Tag[] tags) {
//        if (ArrayUtils.isEmpty(tags) || tags.length > 1) {
//            return false;
//        }
//        return tags[0].kind().equals("Text");
//    }
//
//    public static String generateDescription(Tag[] tags, String linkPrefix) {
//        if (ArrayUtils.isEmpty(tags)) {
//            return "";
//        }
//        StringBuilder description = new StringBuilder();
//        for (int i = 0; i < tags.length; i++) {
//            Tag tag = tags[i];
//            String kind = tag.kind();
//            switch (kind) {
//                case "Text":
//                    description.append(tag.text()).append(" ");
//                    break;
//                case "@see":
//                    //link or see
//                    String text = tag.text();
//                    if (text.indexOf(".") != -1) {
//                        text = text.substring(text.lastIndexOf(".") + 1);
//                    }
//                    String link = text;
//                    if (link.indexOf("#") != -1) {
//                        link = text.substring(0, link.lastIndexOf("#"));
//                    }
//                    description.append("<a href='").append(linkPrefix)
//                            .append(link)
//                            .append("'>").append(text).append("</a>");
//                    break;
//                case "@code":
//                    description.append("<font style=\"color:blue;\">")
//                            .append(tag.text())
//                            .append("</font>");
//                    break;
//            }
//        }
//        return description.toString();
//    }
//
//    /**
//     * 判断是否为一个方法的引用
//     * 即 {xxxx#method}的格式
//     *
//     * @param tag Tag
//     * @return boolean
//     */
//    public static boolean isMethodLink(Tag tag) {
//        if (tag == null) {
//            return false;
//        }
//        return tag.text().indexOf("#") != -1;
//    }
//
//    public static SeeTag getSeeTag(SeeTag[] seeTags, String name) {
//        if (ArrayUtils.isEmpty(seeTags)) {
//            return null;
//        }
//        for (SeeTag seeTag : seeTags) {
//            if (seeTag.text().equals(name)) {
//                return seeTag;
//            }
//        }
//        return null;
//    }


}
