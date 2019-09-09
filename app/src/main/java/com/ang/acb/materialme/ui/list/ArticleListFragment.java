package com.ang.acb.materialme.ui.list;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ang.acb.materialme.R;
import com.ang.acb.materialme.data.model.Article;
import com.ang.acb.materialme.data.model.Resource;
import com.ang.acb.materialme.databinding.FragmentArticleListBinding;
import com.ang.acb.materialme.ui.common.ArticlesViewModel;
import com.ang.acb.materialme.ui.common.MainActivity;
import com.ang.acb.materialme.ui.common.NavigationController;
import com.ang.acb.materialme.utils.InjectorUtils;
import com.ang.acb.materialme.utils.SpacingItemDecoration;
import com.ang.acb.materialme.utils.ViewModelFactory;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import timber.log.Timber;

public class ArticleListFragment extends Fragment {

    private FragmentArticleListBinding binding;
    private ArticlesViewModel viewModel;
    private ArticlesAdapter adapter;
    private NavigationController navigationController;

    // Required empty public constructor
    public ArticleListFragment() {}


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentArticleListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        setupRecyclerView();
        initViewModel();
        populateUi();
    }

    private void setupToolbar(){
        ((MainActivity)getActivity()).setSupportActionBar(binding.mainToolbar);
        if(((MainActivity)getActivity()).getSupportActionBar() != null) {
            ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void initViewModel() {
        ViewModelFactory viewModelFactory = InjectorUtils.provideViewModelFactory(getActivity());
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ArticlesViewModel.class);
        Timber.d("Setup article list view model.");
    }

    private void setupRecyclerView(){
        GridLayoutManager layoutManager = new GridLayoutManager(
                getActivity(), getResources().getInteger(R.integer.grid_column_count));
        binding.rvArticleList.setLayoutManager(layoutManager);
        binding.rvArticleList.addItemDecoration(
                new SpacingItemDecoration(getActivity(), R.dimen.item_offset));
        navigationController = new NavigationController((MainActivity) getActivity());
        adapter =  new ArticlesAdapter(position ->
                navigationController.navigateToArticlesPager(position));
        binding.rvArticleList.setAdapter(adapter);
        Timber.d("Setup article list adapter.");
    }

    private void populateUi() {
        viewModel.getArticleListLiveData().observe(this,
                new Observer<Resource<List<Article>>>() {
                    @Override
                    public void onChanged(Resource<List<Article>> resource) {
                        Timber.d("Observe article list.");
                        if (resource != null && resource.data != null) {
                            adapter.submitList(resource.data);
                        }
                    }
                });
    }

}
