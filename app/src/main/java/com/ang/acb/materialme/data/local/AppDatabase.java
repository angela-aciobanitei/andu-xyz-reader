package com.ang.acb.materialme.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ang.acb.materialme.data.model.Article;

/**
 * The Room database for this app.
 *
 * See: https://medium.com/androiddevelopers/7-steps-to-room-27a5fe5f99b2
 * See: https://medium.com/androiddevelopers/7-pro-tips-for-room-fbadea4bfbd1
 */
@Database(entities = {Article.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "articles.db";
    private static final Object sLock = new Object();
    private static AppDatabase sInstance;

    public abstract ArticleDao articleDao();

    private static AppDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                DATABASE_NAME)
                .build();
    }

    // Returns the single instance of this class, creating it if necessary.
    public static AppDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = buildDatabase(context);
            }
            return sInstance;
        }
    }
}
