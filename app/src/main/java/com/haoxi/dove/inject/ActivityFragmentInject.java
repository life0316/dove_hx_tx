package com.haoxi.dove.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lifei on 2017/3/29.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ActivityFragmentInject {

    /**
     * 布局id
     * @return
     */
    int contentViewId() default -1;


    int menuId() default -1;

    int toolbarTitle() default -1;

    int toolbarIndicator() default -1;
}
