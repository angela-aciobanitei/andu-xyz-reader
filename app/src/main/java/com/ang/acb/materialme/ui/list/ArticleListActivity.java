package com.ang.acb.materialme.ui.list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;

import com.ang.acb.materialme.R;
import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.data.model.Resource;
import com.ang.acb.materialme.databinding.ActivityArticleListBinding;
import com.ang.acb.materialme.utils.GridSpacingItemDecoration;
import com.ang.acb.materialme.utils.InjectorUtils;
import com.ang.acb.materialme.utils.ViewModelFactory;

import java.util.List;

import timber.log.Timber;

public class ArticleListActivity extends AppCompatActivity {

    private ActivityArticleListBinding binding;
    private ArticleListViewModel viewModel;
    private ArticlesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initBinding();
        setupToolbar();
        initViewModel();
        setupRecyclerView();
        initArticleList();
    }

    private void initBinding() {
        // Inflate view and obtain an instance of the binding class.
        binding = DataBindingUtil.setContentView(this, R.layout.activity_article_list);
        // Specify the current activity as the lifecycle owner.
        binding.setLifecycleOwner(this);
    }

    private void setupToolbar(){
        setSupportActionBar(binding.mainToolbar);
    }

    private void initViewModel() {
        ViewModelFactory viewModelFactory = InjectorUtils.provideViewModelFactory(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                    .get(ArticleListViewModel.class);
        Timber.d("Setup article list view model.");
    }

    private void setupRecyclerView(){
        GridLayoutManager layoutManager = new GridLayoutManager(
                this, getResources().getInteger(R.integer.span_count));
        binding.rvArticleList.setLayoutManager(layoutManager);
        binding.rvArticleList.addItemDecoration(
                new GridSpacingItemDecoration(this, R.dimen.item_offset));
        adapter =  new ArticlesAdapter();
        binding.rvArticleList.setAdapter(adapter);
        Timber.d("Setup article list adapter.");
    }

    private void initArticleList() {
        // Observe data and network status.
        viewModel.getArticleListLiveData().observe(this,
                new Observer<Resource<List<Article>>>() {
                    @Override
                    public void onChanged(Resource<List<Article>> resource) {
                        Timber.d("Observe article list.");
                        if (resource != null && resource.data != null) {
                            adapter.submitList(resource.data);
                        }
                        binding.setResource(resource);
                        binding.executePendingBindings();
                    }
        });
    }
}
