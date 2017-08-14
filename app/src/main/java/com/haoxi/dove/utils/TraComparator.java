package com.haoxi.dove.utils;

import android.util.Log;

import com.haoxi.dove.bean.RealFlyBean;

import java.util.Comparator;

/**
 * Created by lifei on 2017/3/11.
 */

public class TraComparator implements Comparator<RealFlyBean> {
    @Override
    public int compare(RealFlyBean o1, RealFlyBean o2) {

        //2017-04-12 11:03:56


//        if (toCompareTime(o1.getGENERATE_TIME(), o2.getGENERATE_TIME())) {
//
//            return 1;
//        } else {
//            return -1;
//        }




        return toCompareTime1(o1.getGENERATE_TIME(), o2.getGENERATE_TIME());

    }

    private int toCompareTime1(String generateTime, String generateTime1) {

        Log.e("generateTime",generateTime+"------比较器------"+generateTime1);

        //2017-04-12 11:41:48------比较器------2017-04-12 11:44:54

        if (generateTime != null && generateTime1 != null) {

            String[] strings_1 = generateTime.split(" ");
            String[] strings1_1 = strings_1[0].split("-");
            String[] strings2_1 = strings_1[1].split(":");

            int year_1 = Integer.parseInt(strings1_1[0]);
            int month_1 = Integer.parseInt(strings1_1[1]);
            int day_1 = Integer.parseInt(strings1_1[2]);

            int hour_1 = Integer.parseInt(strings2_1[0]);
            int min_1 = Integer.parseInt(strings2_1[1]);
            int sencond_1 = Integer.parseInt(strings2_1[2]);

            String[] strings_2 = generateTime1.split(" ");
            String[] strings1_2 = strings_2[0].split("-");
            String[] strings2_2 = strings_2[1].split(":");

            int year_2 = Integer.parseInt(strings1_2[0]);
            int month_2 = Integer.parseInt(strings1_2[1]);
            int day_2 = Integer.parseInt(strings1_2[2]);

            int hour_2 = Integer.parseInt(strings2_2[0]);
            int min_2 = Integer.parseInt(strings2_2[1]);
            int sencond_2 = Integer.parseInt(strings2_2[2]);

            if (year_2 > year_1) {
                return -1;
            }
            else if (year_1 == year_2) {

                if (month_2 > month_1) {
                    return -1;
                } else if (month_2 == month_1) {

                    if (day_2 > day_1) {
                        return -1;
                    } else if (day_2 == day_1) {

                        if (hour_2 > hour_1) {
                            return -1;
                        } else if (hour_2 == hour_1) {

                            if (min_2 > min_1) {
                                return -1;
                            } else if (min_1 == min_2) {

                                if (sencond_2 > sencond_1) {
                                    return -1;
                                } else if (sencond_2 < sencond_1) {
                                    return 1;
                                }
                            }
                        }

                    }
                }

            }

        }


        return 0;
    }

    private boolean toCompareTime(String generateTime, String generateTime1) {

        Log.e("generateTime",generateTime+"------比较器------"+generateTime1);

        //2017-04-12 11:41:48------比较器------2017-04-12 11:44:54

        if (generateTime != null && generateTime1 != null) {

            String[] strings_1 = generateTime.split(" ");
            String[] strings1_1 = strings_1[0].split("-");
            String[] strings2_1 = strings_1[1].split(":");

            int year_1 = Integer.parseInt(strings1_1[0]);
            int month_1 = Integer.parseInt(strings1_1[1]);
            int day_1 = Integer.parseInt(strings1_1[2]);

            int hour_1 = Integer.parseInt(strings2_1[0]);
            int min_1 = Integer.parseInt(strings2_1[1]);
            int sencond_1 = Integer.parseInt(strings2_1[2]);

            String[] strings_2 = generateTime1.split(" ");
            String[] strings1_2 = strings_2[0].split("-");
            String[] strings2_2 = strings_2[1].split(":");

            int year_2 = Integer.parseInt(strings1_2[0]);
            int month_2 = Integer.parseInt(strings1_2[1]);
            int day_2 = Integer.parseInt(strings1_2[2]);

            int hour_2 = Integer.parseInt(strings2_2[0]);
            int min_2 = Integer.parseInt(strings2_2[1]);
            int sencond_2 = Integer.parseInt(strings2_2[2]);

            if (year_2 > year_1) {
                return true;
            } else if (year_1 == year_2) {

                if (month_2 > month_1) {
                    return true;
                } else if (month_2 == month_1) {

                    if (day_2 > day_1) {
                        return true;
                    } else if (day_2 == day_1) {

                        if (hour_2 > hour_1) {
                            return true;
                        } else if (hour_2 == hour_1) {

                            if (min_2 > min_1) {
                                return true;
                            } else if (min_1 == min_2) {

                                if (sencond_2 > sencond_1) {
                                    return true;
                                } else if (sencond_2 < sencond_1) {
                                    return false;
                                }
                            }

                        }

                    }
                }

            }

        }


        return false;
    }
}
