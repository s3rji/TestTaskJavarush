package com.game.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "PLAYER")
public class Player implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; //ID игрока

    @Column(name = "name", nullable = false, columnDefinition = "TEXT", length = 12)
    private String name; //Имя персонажа (до 12 знаков включительно)

    @Column(name = "title", nullable = false, columnDefinition = "TEXT", length = 30)
    private String title; //Титул персонажа (до 30 знаков включительно)

    @Column(name = "race", nullable = false)
    @Enumerated(EnumType.STRING)
    private Race race; //Расса персонажа

    @Column(name = "profession", nullable = false)
    @Enumerated(EnumType.STRING)
    private Profession profession; //Профессия персонажа

    @Column(name = "experience", nullable = false)
    private Integer experience; //Опыт персонажа. Диапазон значений 0..10,000,000

    @Column(name = "level", nullable = false)
    private Integer level; //Уровень персонажа

    @Column(name = "untilNextLevel", nullable = false)
    private Integer untilNextLevel; //Остаток опыта до следующего уровня

    @Temporal(TemporalType.DATE)
    @Column(name = "birthday", nullable = false)
    private Date birthday; //Дата регистрации - Диапазон значений года 2000..3000 включительно

    @Column(name = "banned", nullable = false)
    private Boolean banned; //Забанен / не забанен

    protected Player() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getUntilNextLevel() {
        return untilNextLevel;
    }

    public void setUntilNextLevel(Integer untilNextLevel) {
        this.untilNextLevel = untilNextLevel;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    public void calculateLevel() {
        level = (int) (Math.sqrt(2500 + 200 * experience) - 50) / 100;
    }

    public void calculateUntilNextLevel() {
        untilNextLevel = 50 * (level + 1) * (level + 2) - experience;
    }

    public boolean isValidPlayer() {
        if (name == null || title == null || race == null || profession == null || experience == null || birthday == null)
            return false;

        if (name.length() == 0 || name.length() > 12 || title.length() > 30) return false;

        if (birthday.getTime() < 0 || experience < 0 || experience > 10000000) return false;

        return birthday.getYear() >= 100 && birthday.getYear() <= 1100;
    }

    public boolean update(Player player) {
        this.name = player.name != null ? player.name : this.name;
        this.title = player.title != null ? player.title : this.title;
        this.race = player.race != null ? player.race : this.race;
        this.profession = player.profession != null ? player.profession : this.profession;
        this.experience = player.experience != null ? player.experience : this.experience;
        this.birthday = player.birthday != null ? player.birthday : this.birthday;
        this.banned = player.banned != null ? player.banned : this.banned;

        if (birthday.getTime() < 0 || experience < 0 || experience > 10000000) return false;

        calculateLevel();
        calculateUntilNextLevel();

        return true;
    }
}
