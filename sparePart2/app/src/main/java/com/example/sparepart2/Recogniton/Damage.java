package com.example.sparepart2.Recogniton;

public class Damage {
    private String damageCategory;
    private String damageLocation;
    private double score;


    public Damage(String damageCategory, String damageLocation , Double score){

        this.damageCategory = damageLocation;
        this.damageLocation = damageLocation;
        this.score = score;
    }

    public Damage() {

    }

    public String getDamageCategory() {
        return damageCategory;
    }

    public void setDamageCategory(String damageCategory) {
        this.damageCategory = damageCategory;
    }

    public String getDamageLocation() {
        return damageLocation;
    }

    public void setDamageLocation(String damageLocation) {
        this.damageLocation = damageLocation;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}


