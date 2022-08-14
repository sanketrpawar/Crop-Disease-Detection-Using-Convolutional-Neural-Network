package com.example.plantdiseaseimage.myutils;

public class Feature {
    String name;
    double []feat=new double[7];

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double[] getFeat() {
        return feat;
    }

    public void setFeat(double[] feat) {
        this.feat = feat;
    }
}
