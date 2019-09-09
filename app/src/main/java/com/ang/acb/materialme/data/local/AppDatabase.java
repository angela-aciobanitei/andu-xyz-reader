package com.ang.acb.materialme.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.ang.acb.materialme.data.model.Article;

/**
 * The Room database for this app.
 */
@Database(entities = {Article.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ArticleDao articleDao();

}
