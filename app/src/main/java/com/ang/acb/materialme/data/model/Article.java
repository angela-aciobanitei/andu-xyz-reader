package com.ang.acb.materialme.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Immutable model class for an Article.
 */
@Entity(tableName = "articles")
public class Article implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    @Expose
    private long id;

    @SerializedName("body")
    @Expose
    private String body;

    @ColumnInfo(name = "photo_url")
    @SerializedName("photo")
    @Expose
    private String photoUrl;

    @ColumnInfo(name = "thumb_url")
    @SerializedName("thumb")
    @Expose
    private String thumbUrl;

    @SerializedName("author")
    @Expose
    private String author;

    @SerializedName("title")
    @Expose
    private String title;

    @ColumnInfo(name = "aspect_ratio")
    @SerializedName("aspect_ratio")
    @Expose
    private float aspectRatio;

    @ColumnInfo(name = "published_date")
    @SerializedName("published_date")
    @Expose
    private String publishedDate;

    public Article(long id, String body, String photoUrl, String thumbUrl, String author,
                   String title, float aspectRatio, String publishedDate) {
        this.id = id;
        this.body = body;
        this.photoUrl = photoUrl;
        this.thumbUrl = thumbUrl;
        this.author = author;
        this.title = title;
        this.aspectRatio = aspectRatio;
        this.publishedDate = publishedDate;
    }

    @Ignore
    protected Article(Parcel in) {
        id = in.readLong();
        body = in.readString();
        photoUrl = in.readString();
        thumbUrl = in.readString();
        author = in.readString();
        title = in.readString();
        aspectRatio = in.readFloat();
        publishedDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(body);
        dest.writeString(photoUrl);
        dest.writeString(thumbUrl);
        dest.writeString(author);
        dest.writeString(title);
        dest.writeFloat(aspectRatio);
        dest.writeString(publishedDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }
}
