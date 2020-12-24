package com.baram.lotto.model;

import android.graphics.drawable.Drawable;

public class LottoListItem {

    private Drawable num1;
    private Drawable num2;
    private Drawable num3;
    private Drawable num4;
    private Drawable num5;
    private Drawable num6;
    private Drawable bonus;
    private String round;

    public Drawable getNum1() {
        return num1;
    }

    public Drawable getNum2() {
        return num2;
    }

    public Drawable getNum3() {
        return num3;
    }

    public Drawable getNum4() {
        return num4;
    }

    public Drawable getNum5() {
        return num5;
    }

    public Drawable getNum6() {
        return num6;
    }

    public Drawable getBonus() {
        return bonus;
    }

    public String getRound() {
        return round;
    }

    public void setNum1(Drawable Number) {
        this.num1 = Number;
    }

    public void setNum2(Drawable Number) {
        this.num2 = Number;
    }

    public void setNum3(Drawable Number) {
        this.num3 = Number;
    }

    public void setNum4(Drawable Number) {
        this.num4 = Number;
    }

    public void setNum5(Drawable Number) {
        this.num5 = Number;
    }

    public void setNum6(Drawable Number) {
        this.num6 = Number;
    }

    public void setBonus(Drawable Number) {
        this.bonus = Number;
    }

    public void setRound(String Round) {
        this.round = Round;
    }
}
