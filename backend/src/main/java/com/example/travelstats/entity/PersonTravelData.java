package com.example.travelstats.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 人员出行数据实体类
 * 对应数据库表 person_travel_data
 */
@TableName("person_travel_data")
public class PersonTravelData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 人员ID */
    private Long personId;

    /** 性别: 0-女, 1-男 */
    private Integer gender;

    /** 出生年份 */
    private Integer birthYear;

    /** 总旅行里程 */
    private Long totalMileage;

    /** 总旅行时间(分钟) */
    private Long totalTravelTime;

    public PersonTravelData() {
    }

    public PersonTravelData(Long personId, Integer gender, Integer birthYear,
                            Long totalMileage, Long totalTravelTime) {
        this.personId = personId;
        this.gender = gender;
        this.birthYear = birthYear;
        this.totalMileage = totalMileage;
        this.totalTravelTime = totalTravelTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public Long getTotalMileage() {
        return totalMileage;
    }

    public void setTotalMileage(Long totalMileage) {
        this.totalMileage = totalMileage;
    }

    public Long getTotalTravelTime() {
        return totalTravelTime;
    }

    public void setTotalTravelTime(Long totalTravelTime) {
        this.totalTravelTime = totalTravelTime;
    }

    @Override
    public String toString() {
        return "PersonTravelData{" +
                "id=" + id +
                ", personId=" + personId +
                ", gender=" + gender +
                ", birthYear=" + birthYear +
                ", totalMileage=" + totalMileage +
                ", totalTravelTime=" + totalTravelTime +
                '}';
    }
}
