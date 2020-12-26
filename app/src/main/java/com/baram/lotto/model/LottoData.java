package com.baram.lotto.model;

public class LottoData {
    String totSellamnt;
    String returnValue;
    String drwNoDate;
    String firstWinamnt;
    String drwtNo6;
    String drwtNo4;
    String firstPrzwnerCo;
    String drwtNo5;
    String bnusNo;
    String firstAccumamnt;
    String drwNo;
    String drwtNo2;
    String drwtNo3;
    String drwtNo1;

    public String getTotSellamnt() { return totSellamnt; }
    public String getReturnValue() { return returnValue; }
    public String getDrwNoDate() { return drwNoDate; }
    public String getFirstWinamnt() { return firstWinamnt; }
    public String getFirstPrzwnerCo() { return firstPrzwnerCo; }
    public String getFirstAccumamnt() { return firstAccumamnt; }
    public String getDrwNo() { return drwNo; }
    public String getDrwtNo1() { return drwtNo1; }
    public String getDrwtNo2() { return drwtNo2; }
    public String getDrwtNo3() { return drwtNo3; }
    public String getDrwtNo4() { return drwtNo4; }
    public String getDrwtNo5() { return drwtNo5; }
    public String getDrwtNo6() { return drwtNo6; }
    public String getBnusNo() { return bnusNo; }
    public String getNumber(int n) {
        switch (n) {
            case 1: return drwtNo1;
            case 2: return drwtNo2;
            case 3: return drwtNo3;
            case 4: return drwtNo4;
            case 5: return drwtNo5;
            case 6: return drwtNo6;
            case 7: return  bnusNo;
            default: return "";
        }
    }

    public void setTotSellamnt(String Data) { totSellamnt = Data; }
    public void setReturnValue(String Data) { returnValue = Data; }
    public void setDrwNoDate(String Data) { drwNoDate = Data; }
    public void setFirstWinamnt(String Data) { firstWinamnt = Data; }
    public void setFirstPrzwnerCo(String Data) { firstPrzwnerCo = Data; }
    public void setFirstAccumamnt(String Data) {  firstAccumamnt = Data; }
    public void setDrwNo(String Data) { drwNo = Data; }
    public void setDrwtNo1(String Data) { drwtNo1 = Data; }
    public void setDrwtNo2(String Data) { drwtNo2 = Data; }
    public void setDrwtNo3(String Data) { drwtNo3 = Data; }
    public void setDrwtNo4(String Data) { drwtNo4 = Data; }
    public void setDrwtNo5(String Data) { drwtNo5 = Data; }
    public void setDrwtNo6(String Data) { drwtNo6 = Data; }
    public void setBnusNo(String Data) { bnusNo = Data; }
}
