package com.ang.acb.materialme.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.ang.acb.materialme.data.model.Article;

import java.util.List;

/**
 * Interface for database access on Article related operations.
 */
@Dao
public interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllArticles(List<Article> articles);

    @Transaction
    @Query("SELECT * FROM articles WHERE id= :articleId")
    LiveData<Article> getArticleById(long articleId);

    @Transaction
    @Query("SELECT * FROM articles")
    LiveData<List<Article>> getAllArticles();

}
