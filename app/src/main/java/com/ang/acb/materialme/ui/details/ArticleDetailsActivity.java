package com.ang.acb.materialme.ui.details;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.ang.acb.materialme.R;
import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.databinding.ActivityArticleDetailsBinding;
import com.ang.acb.materialme.ui.list.ArticleListViewModel;
import com.ang.acb.materialme.utils.InjectorUtils;
import com.ang.acb.materialme.utils.ViewModelFactory;
import com.google.android.material.snackbar.Snackbar;

import timber.log.Timber;

public class ArticleDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_ARTICLE_ID = "EXTRA_ARTICLE_ID";
    public static final int INVALID_ARTICLE_ID = -1;

    private ActivityArticleDetailsBinding binding;
    private ArticleDetailsViewModel viewModel;
    private long articleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initBinding();
        setupToolbar();
        initArticleId();
        initViewModel();
        observeArticleDetails();


    }

    private void initBinding() {
        // Inflate view and obtain an instance of the binding class.
        binding = DataBindingUtil.setContentView(this, R.layout.activity_article_details);

        // Specify the current activity as the lifecycle owner.
        binding.setLifecycleOwner(this);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.detailsToolbar);
        if (getSupportActionBar() != null) {
            // Handle Up navigation and hide title
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void initArticleId() {
        articleId = getIntent().getLongExtra(EXTRA_ARTICLE_ID, INVALID_ARTICLE_ID);
        if (articleId == INVALID_ARTICLE_ID) closeOnError();
    }

    private void initViewModel() {
        ViewModelFactory viewModelFactory = InjectorUtils.provideViewModelFactory(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ArticleDetailsViewModel.class);
        Timber.d("Setup article details view model.");
    }

    private void observeArticleDetails() {
        viewModel.getArticleLiveData(articleId).observe(this, new Observer<Article>() {
            @Override
            public void onChanged(Article article) {
                binding.setArticle(article);
            }
        });
    }

    private void closeOnError() {
        Snackbar.make(
                binding.detailsCoordinatorLayout,
                R.string.data_not_available,
                Snackbar.LENGTH_SHORT)
                    .show();
        finish();
    }
}
