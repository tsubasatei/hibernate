package com.xt.hibernate.bean;

import javax.persistence.*;
import java.sql.Blob;
import java.util.Date;
import java.util.Objects;

/**
 * @author xt
 * @create 2019/4/3 11:15
 * @Desc
 */
@Entity
public class News {
    private int id;
    private String title;
    private String author;
    private Date date;
    private String desc;
    /**
     * 大文本
     */
    private String content;

    /**
     * 二进制数据
     */
    private Blob image;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "author")
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Basic
    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        News news = (News) o;
        return id == news.id &&
                Objects.equals(title, news.title) &&
                Objects.equals(author, news.author) &&
                Objects.equals(date, news.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, date);
    }

    public News() {
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", date=" + date +
                '}';
    }

    public News(String title, String author, Date date) {
        this.title = title;
        this.author = author;
        this.date = date;
    }
}
