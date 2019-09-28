package com.magneton.api2.builder.doc;

/**
 * @author zhangmingshuang
 * @since 2019/9/18
 */
public enum TagType {

    See("@see"),
    Author("@author"),
    Version("@version"),
    Since("@since"),
    Return("@return"),
    Deprecated("@deprecated"),
    Throws("@throws"),
    Param("@param");

    public final String kind;

    TagType(String kind) {
        this.kind = kind;
    }

    public static TagType getTag(String kind) {
        for (TagType value : TagType.values()) {
            if (value.kind.equals(kind.toLowerCase())) {
                return value;
            }
        }
        return null;
    }
}
