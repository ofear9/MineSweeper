package com.example.ranendelman.minesweeper.GameLogic;


import java.io.Serializable;

/**
 * Created by ofear on 1/7/2017.
 * ADD LEVEL TO SCORE
 */


public class Score implements Serializable {
    private com.example.ranendelman.minesweeper.GameLogic.gameLevel gameLevel;
    private long id;
    private String name;
    private Long score;
    private String latitude;
    private String longitude;
    private String address;
    private String country;
    private String city;

    public Score() {
    }

    public Score(String name, Long score) {
        this.name = name;
        this.score = score;
    }

    public Score(String name, Long score, gameLevel gameLevel, String latitude, String longitude, String address, String country, String city) {
        this.name = name;
        this.score = score;
        this.gameLevel = gameLevel;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.country = country;
        this.city = city;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public String getGameLevel() {
        switch (this.gameLevel) {
            default:
                return null;
            case BEGINNER:
                return "beginner";

            case MEDIUM:
                return "medium";

            case HARD:
                return "hard";
        }
    }


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Score score1 = (Score) o;

        if (id != score1.id) return false;
        if (name != null ? !name.equals(score1.name) : score1.name != null) return false;
        return !(score != null ? !score.equals(score1.score) : score1.score != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (score != null ? score.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String scoreStr = (String.format("%02d:%02d", ((score % 3600) / 60), score % 60));
        String scoreToView = ("Score{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", score= " + scoreStr +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}');
        return scoreToView;
    }
}


