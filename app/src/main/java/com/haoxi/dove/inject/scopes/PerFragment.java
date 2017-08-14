package com.haoxi.dove.inject.scopes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by lifei on 2017/3/16.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerFragment {


}
